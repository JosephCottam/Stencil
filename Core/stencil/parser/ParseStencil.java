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
	
	/**
	 *
	 * @param source
	 * @return
	 * @throws Exception Any exception thrown by the parser is propagated out.
	 * @throws SyntaxException Syntax errors were encountered.
	 */
	public static StencilTree programTree(String source, Adapter adapter) throws ProgramParseException, Exception {
		StencilTree p = checkParse(source);

		RulesProvided.apply(p);					//Verify that it is a reasonably-complete program...
	
		p = SimplifyViewCanvas.apply(p);		//Remove this pass when multiple views/canvases are supported 
		p = LiftStreamPrototypes.apply(p);		//Create prototype definitions for internally defined streams
		p = ElementToLayer.apply(p);			//Convert "element" statements into layers		
		p = OperatorExpandCompactForm.apply(p);	 
		p = DefaultPack.apply(p);				//Add default packs where required

		p = SeparateRules.apply(p);				//Group the operator chains, switch everything to "Target"
		p = EnsureOrders.apply(p);				//Ensure the proper order blocks
		p = OperatorCustomArgs.apply(p);				//Convert custom-format arguments to printf/call pairs
		p = Predicate_Expand.apply(p);				//Convert filters to standard rule chains
		p = SpecializerDeconstant.apply(p);			//Remove references to constants in specializers

		ModuleCache modules = Imports.apply(p);		//Do module imports		
		p = DefaultSpecializers.apply(p, modules, adapter); //Add default specializers where required
		p = OperatorToOpTemplate.apply(p);			//Converting all operator defs to template/ref pairs
		p = OperatorExpandTemplates.apply(p);		//Remove all template references
		p = ViewCanvasOps.apply(p);					//Add view/canvas operator calls
		p = OperatorExplicit.apply(p);				//Remove anonymous operator references; replaced with named instances and regular references
		p = OperatorExtendFacets.apply(p); 			//Expand operatorDefs to include query and stateID

		p = AdHocOperators.apply(p, modules, adapter);	//Create ad-hoc operators 
		p = OperatorOptimize.apply(p, modules);
		//TODO: Do some analysis here so higher-order ops can update their memory use and tuple prototypes IFF they have constant operator/facet information.
		
		p = SemanticFacetResolve.apply(p);			//Replace semantic facet labels with actual facet names
		p = GuideDistinguish.apply(p);				//Distinguish between guide types
		p = FrameTupleRefs.apply(p, modules);		//Ensure that all tuple references have a frame reference
		p = OperatorInlineSimple.apply(p);			//In-line simple synthetic operators		
		
//		BEGIN GUIDE SYSTEM----------------------------------------------------------------------------------
		p = GuideDefaultSelector.apply(p); 
		p = GuideInsertMonitorOp.apply(p, modules);		//Ensure that auto-guide requirements are met
		p = GuideSampleInSpec.apply(p);
		p = DefaultSpecializers.apply(p, modules, adapter);
		p = SemanticFacetResolve.apply(p);				//Replace semantic facet labels with actual facet names
		p = GuideTransfer.apply(p, modules);		
		p = GuideModifyGenerator.apply(p);
		p = GuideDefaultRules.apply(p);
		p = GuideAutoLabel.apply(p);
		p = GuideLegendGeom.apply(p);

		p = DefaultSpecializers.apply(p, modules, adapter); 
		p = GuideSampleOp.apply(p);
		p = GuideExtendQuery.apply(p);
		p = GuideSetID.apply(p);
		p = GuideClean.apply(p);
//		//END GUIDE SYSTEM----------------------------------------------------------------------------------

		//ANIMATED BINDINGS --------------------------------------------------------------------------------
//		p = AnimatedBinding.apply(p);
		
		//DYNAMIC BINDING ----------------------------------------------------------------------------------
		p = DynamicSeparateRules.apply(p);
		p = DynamicToSimple.apply(p);
	    p = DynamicCompleteRules.apply(p);
		p = SemanticFacetResolve.apply(p);			//Replace semantic facet labels with actual facet names
	    p = DynamicReducer.apply(p);
	    p = DynamicStoreSource.apply(p, modules);
		p = DefaultSpecializers.apply(p, modules, adapter); 		

		p = OperatorStateQuery.apply(p);
	    
	   // SIMPlIFICATIONS AND OPTIMIZATIONS
		p = ReplaceConstants.apply(p);  					//Replace all references to CONST values with the actual value
		p = FillCoConsumes.apply(p);
		p = CombineRules.apply(p);
		p = NumeralizeTupleRefs.apply(p); 					//Numeralize all tuple references
		p = Predicate_Compact.apply(p);						//Improve performance of filter rules by removing all the scaffolding				
		p = SemanticFacetResolve.apply(p);					//Replace semantic facet labels with actual facet names
		p = ReplaceConstantOps.apply(p, modules);			//Evaluate functions that only have constant arguments, propagate results around
		p = LiftLayerConstants.apply(p);					//Move constant property assignments to the defaults section so they are only applied once.
		p = GuideAdoptLayerDefaults.apply(p);				//Take identified layer constants, apply them to the guides
		p = OperatorAlign.apply(p);
		p = LayerAlign.apply(p);
		p = CombineRules.apply(p);
		
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
