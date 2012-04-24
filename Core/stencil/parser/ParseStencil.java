package stencil.parser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;

import org.antlr.runtime.Token;

import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.Program;
import stencil.interpreter.tree.Specializer;
import stencil.module.ModuleCache;
import stencil.adapters.Adapter;
import stencil.parser.string.*;
import stencil.parser.string.validators.*;
import stencil.parser.tree.*;
import stencil.tuple.prototype.TuplePrototype;

import static stencil.parser.ParserConstants.*;


public abstract class ParseStencil {
	/**Should an exception be thrown when validation fails?
	 * If false, a warning message is printed still printed.
	 * 
	 * This flag used for testing, and should be set to true during normal data analysis
	 */
	public static boolean abortOnValidationException = true;

	/**Exception indicating that errors were founding parsing the program.  Individual
	 * errors may not propagated to allow the parser to attempt automatic recovery
	 * and improve error reporting past the first instance.
	 */
	public static class SyntaxException extends Exception {
		protected String input;
		protected int errorCount;
		public SyntaxException(int c) {this(c, null);}
		public SyntaxException(int c, String input) {
			super(String.format("%1$s error(s) parsing the input program.",c));
			errorCount = c;
			this.input = input;
		}
		public int getCount() {return errorCount;}
		public String getInput() {return input;}
	}

	public static final StencilTreeAdapter TREE_ADAPTOR = new StencilTreeAdapter();
	public static final TreeNodeStream TOKEN_STREAM = new CommonTreeNodeStream(TREE_ADAPTOR, null);

	public static TuplePrototype prototype(String source, boolean allowEmpty) throws ProgramParseException {
		try {
			if (!(source.startsWith("(") && source.endsWith(")"))) {throw new SyntaxException(2, source);}
			ANTLRStringStream input = new ANTLRStringStream(source);

			StencilLexer lexer = new StencilLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			StencilParser parser = new StencilParser(tokens);
			parser.setTreeAdaptor(TREE_ADAPTOR);
			StencilParser.tuple_return parserRV = parser.tuple(allowEmpty);
			if (parser.getNumberOfSyntaxErrors() >0) {throw new SyntaxException(parser.getNumberOfSyntaxErrors(), source);}

			StencilTree pt = (StencilTree) parserRV.getTree();
			validate(pt);

			//Makes sure that there are as many parts to the prototype as parts to the definition
			TuplePrototype p = Freezer.prototype(pt);
			assert p.size() == source.replaceAll("[^,]","").length() + 1 || (source.equals("()") && p.size()==0): "Prototpye type length not as expected";

			return p;
		} catch (Exception e) {
			throw new ProgramParseException(String.format("Error parsing prototype: '%1$s'.", source), e);
		}	
	}

	public static Specializer specializer(String source) throws ProgramParseException {
		return Freezer.specializer(specializerTree(source));
	}

	public static StencilTree specializerTree(String source) throws ProgramParseException {
		try {
			ANTLRStringStream input = new ANTLRStringStream(source);

			StencilLexer lexer = new StencilLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			StencilParser parser = new StencilParser(tokens);
			parser.setTreeAdaptor(TREE_ADAPTOR);
			StencilParser.specializer_return parserRV = parser.specializer();
			if (parser.getNumberOfSyntaxErrors() >0) {throw new SyntaxException(parser.getNumberOfSyntaxErrors(), source);}

			StencilTree spec = (StencilTree) parserRV.getTree();			
			return spec;
		} catch (Exception e) {
			throw new ProgramParseException(String.format("Error parsing specializer: '%1$s'.", source), e);
		}
	}


	//Create a Stencil rule object that binds like to : from
	//Actually performs the parsing so a tree is returned
	public static final StencilTree ruleTree(String to, String from) {
		String input; 
		if (from != null) {
			input = to + BIND_OPERATOR + STREAM_FRAME + NAME_SEPARATOR + from;
		} else {
			input = to + BIND_OPERATOR + STREAM_FRAME;
		}

		ANTLRStringStream input1 = new ANTLRStringStream(input);
		StencilLexer lexer = new StencilLexer(input1);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		StencilParser parser = new StencilParser(tokens);
		parser.setTreeAdaptor(ParseStencil.TREE_ADAPTOR);

		try {
			return (StencilTree) parser.rule(StencilParser.TARGET).getTree();
		} catch (Exception e) {
			throw new RuntimeException("Error constructing default rule for guides.",e);
		}
	}


	/**Checks to see if a program can be parsed.  This is the first stage of a full
	 * parse.  It includes only minimal validation and few transformations.*/
	public static StencilTree checkParse(String source) throws ProgramParseException {
		if (source == null) {throw new IllegalArgumentException("Source passed to parser cannot be null.");}

		ANTLRStringStream input = new ANTLRStringStream(source);

		StencilLexer lexer = new StencilLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		StencilParser parser = new StencilParser(tokens);
		parser.poolErrors(true);

		parser.setTreeAdaptor(TREE_ADAPTOR);
		StencilTree p;
		try {p = (StencilTree) parser.program().getTree();}
		catch (Exception e) {
			throw new ProgramParseException("Error parsing Stencil program.", e);
		}

		if (parser.getErrors().size() >0) {
			throw new ProgramParseException("Error(s) parsing Stencil program.", parser.getErrors());
		} else if (tokens.index() != tokens.size()-1) {	// -1 because of EOF
			Token lastToken = tokens.get(tokens.index()-1);
			throw new ProgramParseException("Error parsing Stencil program; no input consumed after line " + lastToken.getLine());
		}

		p = DefaultPack.apply(p);				//Add default packs where required

		parseValidate(p);
		return p;
	}

	/**Process imports**/
	public static ModuleCache processImports(StencilTree program) {return Imports.apply(program);}

	/**After check parse, tree gets normalized!**/
	public static StencilTree normalizeTree(StencilTree program, ModuleCache modules, Adapter adapter) throws ProgramParseException, Exception {
		RulesProvided.apply(program);					//Verify that it is a reasonably-complete program...
		
		program = CompletePrototypeTypes.apply(program);
		program = SimplifyViewCanvas.apply(program);		//Remove this pass when multiple views/canvases are supported 
		program = LiftStreamPrototypes.apply(program);		//Create prototype definitions for internally defined streams
		program = ElementToLayer.apply(program);			//Convert "element" statements into layers		
		program = OperatorExpandCompactForm.apply(program);	 
		program = DefaultPack.apply(program);				//Add default packs where required

		program = SeparateRules.apply(program);				//Group the operator chains, switch everything to "Target"
		program = EnsureOrders.apply(program);				//Ensure the proper order blocks
		program = OperatorCustomArgs.apply(program);				//Convert custom-format arguments to printf/call pairs
		program = Predicate_Expand.apply(program);				//Convert filters to standard rule chains
		program = SpecializerDeconstant.apply(program);			//Remove references to constants in specializers

		program = DefaultSpecializers.apply(program, modules, adapter); //Add default specializers where required
		program = OperatorToOpTemplate.apply(program);			//Converting all operator defs to template/ref pairs
		program = OperatorExpandTemplates.apply(program);		//Remove all template references
		program = ViewCanvasOps.apply(program);					//Add view/canvas operator calls
		program = OperatorExplicit.apply(program);				//Remove anonymous operator references; replaced with named instances and regular references
		program = OperatorExtendFacets.apply(program); 			//Expand operatorDefs to include query and stateID

		program = AdHocOperators.apply(program, modules, adapter);	//Create ad-hoc operators 
		program = OperatorOptimize.apply(program, modules);
		//TODO: Do some analysis here so higher-order ops can update their memory use and tuple prototypes IFF they have constant operator/facet information.
		
		program = SemanticFacetResolve.apply(program);			//Replace semantic facet labels with actual facet names
		program = GuideDistinguish.apply(program);				//Distinguish between guide types
		program = FrameTupleRefs.apply(program, modules);		//Ensure that all tuple references have a frame reference
		program = OperatorInlineSimple.apply(program);			//In-line simple synthetic operators		
		

		return program;
	}

	/**Normalized trees have guides and dynamic bindings...*/
	public static StencilTree buildAbstractions(StencilTree program, ModuleCache modules, Adapter adapter) throws ProgramParseException, Exception {

		program = GuideDefaultSelector.apply(program); 
		program = GuideInsertMonitorOp.apply(program, modules);		//Ensure that auto-guide requirements are met
		program = GuideSampleInSpec.apply(program);
		program = DefaultSpecializers.apply(program, modules, adapter);
		program = SemanticFacetResolve.apply(program);				//Replace semantic facet labels with actual facet names
		program = GuideTransfer.apply(program, modules);		
		program = GuideModifyGenerator.apply(program);
		program = GuideDefaultRules.apply(program);
		program = GuideAutoLabel.apply(program);
		program = GuideLegendGeom.apply(program);

		program = DefaultSpecializers.apply(program, modules, adapter); 
		program = GuideSampleOp.apply(program);
		program = GuideExtendQuery.apply(program);
		program = GuideSetID.apply(program);
		program = GuideClean.apply(program);
//		//END GUIDE SYSTEM----------------------------------------------------------------------------------

		//ANIMATED BINDINGS --------------------------------------------------------------------------------
//		p = AnimatedBinding.apply(p);
		
		//DYNAMIC BINDING ----------------------------------------------------------------------------------
		program = DynamicSeparateRules.apply(program);
		program = DynamicToSimple.apply(program);
	    program = DynamicCompleteRules.apply(program);
		program = SemanticFacetResolve.apply(program);			//Replace semantic facet labels with actual facet names
	    program = DynamicReducer.apply(program);
	    program = DynamicStoreSource.apply(program, modules);
		program = DefaultSpecializers.apply(program, modules, adapter); 		

		program = OperatorStateQuery.apply(program);

		return program;
	}

	/**Optimizations on the tree.
	 * **/
	public static StencilTree optimize(StencilTree program, ModuleCache modules) {
		// SIMPlIFICATIONS AND OPTIMIZATIONS
		program = ReplaceConstants.apply(program);  					//Replace all references to CONST values with the actual value
		program = FillCoConsumes.apply(program);
		program = CombineRules.apply(program);
		program = NumeralizeTupleRefs.apply(program); 					//Numeralize all tuple references
		program = Predicate_Compact.apply(program);						//Improve performance of filter rules by removing all the scaffolding				
		program = SemanticFacetResolve.apply(program);					//Replace semantic facet labels with actual facet names
		program = ReplaceConstantOps.apply(program, modules);			//Evaluate functions that only have constant arguments, propagate results around
		program = LiftLayerConstants.apply(program);					//Move constant property assignments to the defaults section so they are only applied once.
		program = GuideAdoptLayerDefaults.apply(program);				//Take identified layer constants, apply them to the guides
		program = OperatorAlign.apply(program);
		program = LayerAlign.apply(program);
		program = CombineRules.apply(program);

		return program;
	}

	/**
	 *
	 * @param source
	 * @return
	 * @throws Exception Any exception thrown by the parser is propagated out.
	 * @throws SyntaxException Syntax errors were encountered.
	 */
	public static StencilTree programTree(String source, Adapter adapter) throws ProgramParseException, Exception {
		StencilTree p = checkParse(source);
		ModuleCache modules = processImports(p);
		p = normalizeTree(p, modules, adapter);
		p = buildAbstractions(p, modules, adapter);
		p = optimize(p, modules);
		validate(p);
		return p;
	}

	public static Program program(String source, Adapter adapter) throws ProgramParseException, Exception {
		return Freezer.program(programTree(source, adapter));
	}
	
	/**Pretty-print the passed program.*/
	public static String prettyPrint(String source) throws ProgramParseException {
		StencilTree tree = checkParse(source);
		return PrettyPrinter.format(tree);
	}
	
	//Validations to run right after a checkParse
	private static void parseValidate(Tree p) {
		try {
			TargetMatchesPack.apply(p);
			StreamDeclarationValidator.apply(p);
			ViewCanvasSingleDef.apply(p);
			UniqueNames.apply(p);
		} catch (RuntimeException e) {
			if (abortOnValidationException) {throw e;}
			else {System.err.println(e.getMessage());}
		}
	}

	//Run all validators.
	private static void validate(Tree p) {
		try {
			TargetMatchesPack.apply(p);
			SpecializerValidator.apply(p);
			FullNumeralize.apply(p);			
			OperatorProxiesPopulated.apply(p);
			OperatorPrefilter.apply(p);
			LimitDynamicBind.apply(p);
			StoreValidator.apply(p);
			OrderValidator.apply(p);
		} catch (RuntimeException e) {
			if (abortOnValidationException) {throw e;}
			else {System.err.println(e.getMessage());}
		}
	}
}
