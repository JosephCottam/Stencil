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

import stencil.module.ModuleCache;
import stencil.adapters.Adapter;
import stencil.parser.string.*;
import stencil.parser.string.validators.*;
import stencil.parser.tree.*;

public abstract class ParseStencil {
	/**Should an exception be thrown when validation fails?
	 * If false, a warning message is printed still printed.
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
	
	public static TuplePrototype parsePrototype(String source, boolean allowEmpty) throws ProgramParseException {
		try {
			ANTLRStringStream input = new ANTLRStringStream(source);
	
			StencilLexer lexer = new StencilLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
	
			StencilParser parser = new StencilParser(tokens);
			parser.setTreeAdaptor(TREE_ADAPTOR);
			StencilParser.tuple_return parserRV = parser.tuple(allowEmpty);
			if (parser.getNumberOfSyntaxErrors() >0) {throw new SyntaxException(parser.getNumberOfSyntaxErrors(), source);}
			
			TuplePrototype p =(TuplePrototype) parserRV.getTree();
			//Makes sure that there are as many parts to the prototype as parts to the definition
			assert p.size() == source.split(",").length : "Prototpye type length not as expected";
			validate(p);
			
			return p;
		} catch (Exception e) {
			throw new ProgramParseException(String.format("Error parsing prototype: '%1$s'.", source), e);
		}	
	}
	
	
	public static Specializer parseSpecializer(String source) throws ProgramParseException {
		try {
			ANTLRStringStream input = new ANTLRStringStream(source);
	
			StencilLexer lexer = new StencilLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
	
			StencilParser parser = new StencilParser(tokens);
			parser.setTreeAdaptor(TREE_ADAPTOR);
			StencilParser.specializer_return parserRV = parser.specializer();
			if (parser.getNumberOfSyntaxErrors() >0) {throw new SyntaxException(parser.getNumberOfSyntaxErrors(), source);}
						
			return (Specializer) parserRV.getTree();
		} catch (Exception e) {
			throw new ProgramParseException(String.format("Error parsing specializer: '%1$s'.", source), e);
		}
	}
	
	/**Checks to see if a program can be parsed.  This is the first stage of a full
	 * parse.  It includes only minimal validation and few transformations.*/
	public static Program checkParse(String source) throws ProgramParseException {
		if (source == null) {throw new IllegalArgumentException("Source passed to parser cannot be null.");}

		ANTLRStringStream input = new ANTLRStringStream(source);
		
		StencilLexer lexer = new StencilLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		StencilParser parser = new StencilParser(tokens);
		parser.poolErrors(true);
		
		parser.setTreeAdaptor(TREE_ADAPTOR);
		Program p;
		try {p = (Program) parser.program().getTree();}
		catch (Exception e) {
			throw new ProgramParseException("Error parsing Stencil program.", e);
		}
		
		if (parser.getErrors().size() >0) {
			throw new ProgramParseException("Error(s) parsing Stencil program.", parser.getErrors());
		}
		
		return p;
	}
	
	/**
	 *
	 * @param source
	 * @return
	 * @throws Exception Any exception thrown by the parser is propagated out.
	 * @throws SyntaxException Syntax errors were encountered.
	 */
	public static Program parse(String source, Adapter adapter) throws ProgramParseException, Exception {
		Program p = checkParse(source);

		p = LiftStreamPrototypes.apply(p);		//Create prototype definitions for internally defined streams
		p = SeparateRules.apply(p);				//Group the operator chains
		p = EnsureOrders.apply(p);				//Ensure the proper order blocks

		ModuleCache modules = Imports.apply(p);	//Do module imports
		
		PreparsePython.apply(p);				//Verify that Python operators are syntactically correct and appropriately indented
		p = PrepareCustomArgs.apply(p);			//Parse custom argument blocks
		p = Predicate_Expand.apply(p);			//Convert filters to standard rule chains
		p = TupleRefDeLast.apply(p);			//Remove all uses of the LAST tuple reference
		p = SpecializerDeconstant.apply(p);	//Remove references to constants in specializers
		p = DefaultSpecializers.apply(p, modules, adapter, true); 			//Add default specializers where required

		p = DefaultPack.apply(p);				//Add default packs where required
		p = OperatorToOpTemplate.apply(p);		//Converting all operator defs to template/ref pairs
		p = OperatorInstantiateTemplates.apply(p, modules);		//Remove all operator references
		p = OperatorExplicit.apply(p);		
		p = OperatorInlineSimple.apply(p);			//In-line simple synthetic operators		
		p = OperatorExtendFacets.apply(p);  		//Expand operatorDefs to include query and stateID


		p = AnnotateEnvironmentSize.apply(p);			//Annotate call chains with the environment size (must be done before layer creation because defaults can have call chains)
		p = ElementToLayer.apply(p);					//Convert "element" statements into layers
		p = AdHocOperators.apply(p, modules, adapter);	//Create ad-hoc operators 
		NoOperatorReferences.apply(p);					//Validate the ad-hocs are all created
		
		
		//BEGIN GUIDE SYSTEM----------------------------------------------------------------------------------
		p = GuideDefaultSelector.apply(p); 
		p = GuideDistinguish.apply(p);		//Distinguish between guide types		
		p = GuideInsertSeedOp.apply(p, modules);		//Ensure that auto-guide requirements are met


		//Add default specializers to all function nodes (cover things recently added)
		p = DefaultSpecializers.apply(p, modules, adapter, false); 		
		p = SetOperators.apply(p, modules);			//Prime tree nodes with operators from the modules cache

		
		p = GuideTransfer.apply(p, modules);		
		p = GuideLiftGenerator.apply(p);
		p = GuideDefaultRules.apply(p);

		p = DefaultSpecializers.apply(p, modules, adapter, false); 
		p = SetOperators.apply(p, modules);

		GuideSampleOp.apply(p);
		p = GuideExtendQuery.apply(p);
		p = GuideClean.apply(p);

		//END GUIDE SYSTEM----------------------------------------------------------------------------------
		
		p = OperatorStateQuery.apply(p);
		
		//BEGIN DYNAMIC BINDING ----------------------------------------------------------------------------------

		p = DynamicSeparateRules.apply(p);
		p = DynamicToSimple.apply(p);
	    p = DynamicCompleteRules.apply(p);
	    
		//END DYNAMIC BINDING ----------------------------------------------------------------------------------
	    
		p = TupleRefChain.apply(p);
		p = RemoveOpTemplates.apply(p);
		p = FrameTupleRefs.apply(p, modules);				//Ensure that all tuple references have a frame reference
		p = ReplaceConstants.apply(p);  					//Replace all references to CONST values with the actual value
		p = NumeralizeTupleRefs.apply(p, modules); 			//Numeralize all tuple references
		p = Predicate_Compact.apply(p);						//Improve performance of filter rules by removing all the scaffolding		
		p = UnifyTargetTypes.apply(p);
		p = AnnotateEnvironmentSize.apply(p);				//Since some transformations change chain lengths, this must be re-run.
		p = ReplaceConstantOps.apply(p, modules);			//Evaluate constant rules, propagate results out		
		p = LiftSharedConstantRules.apply(p);				//Move all constant rules up to the defaults section so they are only evaluated once.

		validate(p);
		
		return p;
	}
	
	
	//Run common validators.
	private static void validate(Tree p) {
		try {
			SpecializerValidator.apply(p);
			FullNumeralize.apply(p);
			StreamDeclarationValidator.apply(p);			
			AllInvokeables.apply(p);
			TargetMatchesPack.apply(p);
			OperatorPrefilter.apply(p);
			MapFoldBalance.apply(p);
		} catch (RuntimeException e) {
			if (abortOnValidationException) {throw e;}
			else {e.printStackTrace();}
		}
	}
}
