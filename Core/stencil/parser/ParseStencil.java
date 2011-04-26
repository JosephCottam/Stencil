/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.parser;

import java.io.PrintStream;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;

import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.Program;
import stencil.interpreter.tree.Specializer;
import stencil.module.ModuleCache;
import stencil.adapters.Adapter;
import stencil.parser.string.*;
import stencil.parser.string.validators.*;
import stencil.parser.tree.*;
import stencil.tuple.prototype.TuplePrototype;

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

	@SuppressWarnings("unused")
	private static class DumpTree {
	    public static final String HEADER = "Parent, Child, ChildIdx";


	    private static String represent(Tree t) {return t.getText();}
	    public static void printTree(Tree t, PrintStream out) {
	    	out.println(HEADER);
	    	dump(t, out);
	    }

	    public static void dump(Tree root, PrintStream out) {
			for (int i=0; i<root.getChildCount(); i++) {
				Tree child = root.getChild(i);
				out.printf("%1$s, %2$s, %3$d\n", represent(root), represent(child), i);
				dump(child,out);
			}
		}
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
		}
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

		p = LiftStreamPrototypes.apply(p);		//Create prototype definitions for internally defined streams
		p = ElementToLayer.apply(p);					//Convert "element" statements into layers
		p = SeparateRules.apply(p);				//Group the operator chains
		p = EnsureOrders.apply(p);				//Ensure the proper order blocks

		ModuleCache modules = Imports.apply(p);	//Do module imports
		
		p = PrepareCustomArgs.apply(p);			//Parse custom argument blocks
		p = Predicate_Expand.apply(p);			//Convert filters to standard rule chains
		p = LastToAll.apply(p);					//Remove all uses of the LAST tuple reference
		p = SpecializerDeconstant.apply(p);		//Remove references to constants in specializers
		p = DefaultSpecializers.apply(p, modules, adapter, true); 			//Add default specializers where required
		p = DefaultPack.apply(p);				//Add default packs where required
		p = OperatorToOpTemplate.apply(p);		//Converting all operator defs to template/ref pairs
		p = OperatorInstantiateTemplates.apply(p);		//Remove all template references
		p = OperatorExplicit.apply(p);				//Remove anonymous operator references; replaced with named instances and regular references
		p = OperatorExtendFacets.apply(p);  		//Expand operatorDefs to include query and stateID

		p = AdHocOperators.apply(p, modules, adapter);	//Create ad-hoc operators 
		NoOperatorReferences.apply(p);					//Validate the ad-hocs are all created

		
		p = FrameTupleRefs.apply(p, modules, true);//Ensure that all tuple references have a frame reference
		p = OperatorInlineSimple.apply(p);			//In-line simple synthetic operators		
		
//		BEGIN GUIDE SYSTEM----------------------------------------------------------------------------------
		p = GuideDistinguish.apply(p);					//Distinguish between guide types		
		p = GuideDefaultSelector.apply(p); 
		p = GuideInsertMonitorOp.apply(p, modules);		//Ensure that auto-guide requirements are met
		p = FrameTupleRefs.apply(p, modules, true);		
		p = DefaultSpecializers.apply(p, modules, adapter, false); 		
		p = SetOperators.apply(p, modules);			//Prime tree nodes with operators from the modules cache
													//TODO: Move to later since operators are all explicitly named...stop relying on propagation of copies to keep things sharing memory
		
		p = GuideTransfer.apply(p, modules);		
		p = GuideModifyGenerator.apply(p);
		p = GuideDefaultRules.apply(p);
		p = GuideAutoLabel.apply(p);
		p = GuideLegendGeom.apply(p);

		p = DefaultSpecializers.apply(p, modules, adapter, false); 
		p = SetOperators.apply(p, modules);

		p = GuideSampleOp.apply(p);
		p = GuideExtendQuery.apply(p);
		p = GuideRemoveSelectors.apply(p);
		p = GuideClean.apply(p);
		p = FrameTupleRefs.apply(p, modules, false);		//Ensure that all tuple references have a frame reference
		//END GUIDE SYSTEM----------------------------------------------------------------------------------

		//DYNAMIC BINDING ----------------------------------------------------------------------------------
		p = DynamicSeparateRules.apply(p);
		p = DynamicToSimple.apply(p);
	    p = DynamicCompleteRules.apply(p);
	    p = DynamicReducer.apply(p);
	    
		p = OperatorStateQuery.apply(p);
	    
	   // SIMPlIFICATIONS AND OPTIMIZATIONS
		p = ReplaceConstants.apply(p);  					//Replace all references to CONST values with the actual value
		p = CombineRules.apply(p);
		p = NumeralizeTupleRefs.apply(p, modules); 			//Numeralize all tuple references
		p = Predicate_Compact.apply(p);						//Improve performance of filter rules by removing all the scaffolding				
		p = UnifyTargetTypes.apply(p);
		p = ReplaceConstantOps.apply(p, modules);			//Evaluate functions that only have constant arguments, propagate results around
		p = LiftLayerConstants.apply(p);					//Move constant property assignments to the defaults section so they are only applied once.
		p = GuideAdoptLayerDefaults.apply(p);				//Take identified layer constants, apply them to the guides
		p = CombineRules.apply(p);
		
		validate(p);
		return p;
	}

	public static Program program(String source, Adapter adapter) throws ProgramParseException, Exception {
		return Freezer.program(programTree(source, adapter));
	}
	
	
	//Validations to run right after a checkParse
	private static void parseValidate(Tree p) {
		try {
			TargetMatchesPack.apply(p);
			StreamDeclarationValidator.apply(p);
		} catch (RuntimeException e) {
			if (abortOnValidationException) {throw e;}
			else {System.err.println(e.getMessage());}
		}
	}
	
	//Run all validators.
	private static void validate(Tree p) {
		try {
			SpecializerValidator.apply(p);
			FullNumeralize.apply(p);			
			AllInvokeables.apply(p);
			OperatorPrefilter.apply(p);
		} catch (RuntimeException e) {
			if (abortOnValidationException) {throw e;}
			else {System.err.println(e.getMessage());}
		}
	}
}
