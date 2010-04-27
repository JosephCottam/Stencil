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

import stencil.operator.module.ModuleCache;
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
			throw new ProgramParseException(String.format("Error parsing specializer: '%1$s'.", source), e);
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
			
			validate((StencilTree) parserRV.getTree());
			
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
		CommonTreeNodeStream treeTokens = new CommonTreeNodeStream(p);
		treeTokens.setTreeAdaptor(TREE_ADAPTOR);

		//Group the operator chains
		LiftStreamPrototypes liftStreams = new LiftStreamPrototypes(treeTokens);
		liftStreams.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) liftStreams.downup(p);
		
		//Group the operator chains
		SeparateTargets targets = new SeparateTargets(treeTokens);
		targets.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) targets.downup(p);

		//Ensure the proper order blocks
		EnsureOrders orders = new EnsureOrders(treeTokens);
		orders.setTreeAdaptor(TREE_ADAPTOR);
		p = orders.ensureOrder(p);

		//Do module imports
		Imports imports = new Imports(treeTokens);
		ModuleCache modules = imports.processImports(p);
		
		//Verify that Python operators are syntactically correct and appropriately indented
		PreparsePython pyParse = new PreparsePython(treeTokens);
		pyParse.downup(p);

		//Parse custom argument blocks
		PrepareCustomArgs customArgs = new PrepareCustomArgs(treeTokens);
		customArgs.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) customArgs.downup(p);

		//Add default specializers where required
		DefaultSpecializers defaultSpecializers = new DefaultSpecializers(treeTokens, modules, adapter);
		defaultSpecializers.setTreeAdaptor(TREE_ADAPTOR);
		defaultSpecializers.downup(p);
		
		//Remove all operator references
		DereferenceOperators opTemplates = new DereferenceOperators(treeTokens, modules);
		opTemplates.setTreeAdaptor(TREE_ADAPTOR);
		opTemplates.downup(p);

		//Annotate call chains with the environment size (must be done before layer creation because defaults can have call chains)
		AnnotateEnvironmentSize envSize = new AnnotateEnvironmentSize(treeTokens);
		envSize.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) envSize.downup(p);

		
		//Create ad-hoc operators
		AdHocOperators adHoc = new AdHocOperators(treeTokens, modules, adapter);
		adHoc.downup(p);
		
		//Add default specializers to all function nodes
		defaultSpecializers.downup(p);
		
		//Add default packs where required
		DefaultPack defaultPack = new DefaultPack(treeTokens);
		defaultPack.setTreeAdaptor(TREE_ADAPTOR);
		defaultPack.downup(p);

		
		//BEGIN GUIDE SYSTEM----------------------------------------------------------------------------------
		GuideDefaultSelector guideSelector = new GuideDefaultSelector(treeTokens);
		guideSelector.setTreeAdaptor(TREE_ADAPTOR);
		guideSelector.downup(p);

		
		//Distinguish between guide types
		GuideDistinguish guideDistinguish  = new GuideDistinguish(treeTokens, TREE_ADAPTOR);
		guideDistinguish.downup(p);
		
		//Ensure that auto-guide requirements are met
		GuideInsertSeedOp ensure = new GuideInsertSeedOp(treeTokens,modules); 
		ensure.setTreeAdaptor(TREE_ADAPTOR);		
		p = (Program) ensure.transform(p);

		//Add default specializers to all function nodes (cover things recently added)
		defaultSpecializers.downup(p);
		
		//Prime tree nodes with operators from the modules cache
		SetOperators set = new SetOperators(treeTokens, modules);
		set.setTreeAdaptor(TREE_ADAPTOR);
		set.downup(p);
		
		GuideTransfer ag = new GuideTransfer(treeTokens, modules);
		ag.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) ag.transform(p);
		
		GuideLiftGenerator gLiftGenerator = new GuideLiftGenerator(treeTokens);
		gLiftGenerator.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) gLiftGenerator.downup(p);

		GuideDefaultRules gDefaultRules = new GuideDefaultRules(treeTokens);
		gDefaultRules.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) gDefaultRules.downup(p);

		defaultSpecializers.downup(p);

		SetOperators set2 = new SetOperators(treeTokens, modules);
		set2.setTreeAdaptor(TREE_ADAPTOR);
		set2.downup(p);

		GuideSampleOp gSampleOp = new GuideSampleOp(treeTokens);
		gDefaultRules.setTreeAdaptor(TREE_ADAPTOR);
		gSampleOp.downup(p);

		GuideExtendQuery guideExtend = new GuideExtendQuery(treeTokens);
		guideExtend.setTreeAdaptor(TREE_ADAPTOR);
		guideExtend.downup(p);

		GuideClean guideClean = new GuideClean(treeTokens);
		guideClean.setTreeAdaptor(TREE_ADAPTOR);
		guideClean.downup(p);

		//END GUIDE SYSTEM----------------------------------------------------------------------------------
		
		TupleRefChain trc = new TupleRefChain(treeTokens);
		trc.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) trc.downup(p);

		//Ensure that all tuple references have a frame reference
		FrameTupleRefs frameRefs = new FrameTupleRefs(treeTokens, modules);
		frameRefs.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) frameRefs.downup(p);
		
		//Numeralize all tuple references
		NumeralizeTupleRefs numeralize = new NumeralizeTupleRefs(treeTokens, modules);
		numeralize.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) numeralize.downup(p);		

		
		//Since some transformations change chain lengths, this must be re-run.
		envSize.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) envSize.downup(p);
		
		//Move all constant rules up to the defaults section so they are only evaluated once.
		LiftSharedConstantRules sharedLifter = new LiftSharedConstantRules(treeTokens, modules);
		sharedLifter.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) sharedLifter.transform(p);

		validate(p);
		
		return p;
	}
	
	
	//Run common validators.
	private static void validate(StencilTree t) {
		try {
			//Since validators don't permute the tree in any way, one token stream might be enough...
			CommonTreeNodeStream treeTokens =new CommonTreeNodeStream(t);
	
			SpecializerValidator specializer = new SpecializerValidator(treeTokens);
			specializer.downup(t);
	
			FullNumeralize numeralize = new FullNumeralize(treeTokens);
			numeralize.downup(t);
			
			StreamDeclarationValidator stream = new StreamDeclarationValidator(treeTokens);
			stream.downup(t);
			
			AllInvokeables invokeables = new AllInvokeables(treeTokens);
			invokeables.downup(t);
	
			
			TargetMatchesPack targetPack = new TargetMatchesPack(treeTokens);
			targetPack.downup(t);

			OperatorPrefilter opPrefilter = new OperatorPrefilter(treeTokens);
			opPrefilter.downup(t);
		} catch (RuntimeException e) {
			if (abortOnValidationException) {throw e;}
			else {e.printStackTrace();}
		}
	}
}
