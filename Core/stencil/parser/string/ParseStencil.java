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
package stencil.parser.string;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import stencil.operator.module.ModuleCache;
import stencil.parser.validators.*;
import stencil.adapters.Adapter;
import stencil.parser.tree.*;
import stencil.parser.ProgramParseException;

public abstract class ParseStencil {
	public static final String ASTNodeType =  "jacottam.tsm.parser.ASTNode";

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

	
	public static final StencilTreeAdapter treeAdaptor = new StencilTreeAdapter(); 
	
	public static Sigil parseSigil(String source) throws ProgramParseException {
		try {
			ANTLRStringStream input = new ANTLRStringStream(source);
			
			StencilLexer lexer = new StencilLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
	
			StencilParser parser = new StencilParser(tokens);
			parser.setTreeAdaptor(treeAdaptor);
			
			StencilParser.sigil_return parserRV = parser.sigil();
			if (parser.getNumberOfSyntaxErrors() >0) {throw new SyntaxException(parser.getNumberOfSyntaxErrors(), source);}

			validate((StencilTree) parserRV.getTree());
			
			return (Sigil) parserRV.getTree();
		} catch (Exception e) {
			throw new ProgramParseException(String.format("Error parsing sigil: '%1$s'.", source), e);
		}
	}
	
	public static Specializer parseSpecializer(String source) throws ProgramParseException {
		try {
			ANTLRStringStream input = new ANTLRStringStream(source);
	
			StencilLexer lexer = new StencilLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
	
			StencilParser parser = new StencilParser(tokens);
			parser.setTreeAdaptor(treeAdaptor);
			StencilParser.specializer_return parserRV = parser.specializer(StencilParser.RuleOpts.All);
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
		
		parser.setTreeAdaptor(treeAdaptor);
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
		CommonTreeNodeStream treeTokens;

		Program p = checkParse(source);

		//Group the operators
		treeTokens = new CommonTreeNodeStream(p);
		SeparateTargets targets = new SeparateTargets(treeTokens);
		targets.setTreeAdaptor(treeAdaptor);
		p = (Program) targets.downup(p);

		
		//Ensure the proper order blocks
		treeTokens = new CommonTreeNodeStream(p);
		EnsureOrders orders = new EnsureOrders(treeTokens);
		orders.setTreeAdaptor(treeAdaptor);
		p = orders.ensureOrder(p);

		//Do module imports
		treeTokens = new CommonTreeNodeStream(p);
		Imports imports = new Imports(treeTokens);
		ModuleCache modules = imports.processImports(p);
		p.setModuleCache(modules);//TODO: Remove when all tuple references are positional

		//Verify that Python operators are syntactically correct and appropriately indented
		treeTokens = new CommonTreeNodeStream(p);
		PythonValidator pyValidator = new PythonValidator(treeTokens);
		pyValidator.downup(p);

		//Add default specializers where required
		treeTokens = new CommonTreeNodeStream(p);
		DefaultSpecializers defaultSpecializers = new DefaultSpecializers(treeTokens, modules);
		defaultSpecializers.setTreeAdaptor(treeAdaptor);
		defaultSpecializers.misc(p);
		
		//Remove all operator references
		treeTokens = new CommonTreeNodeStream(p);
		DereferenceOperators opTemplates = new DereferenceOperators(treeTokens, modules);
		opTemplates.setTreeAdaptor(treeAdaptor);
		opTemplates.downup(p);

		//Create ad-hoc operators
		AdHocOperators adHoc = new AdHocOperators(treeTokens, modules, adapter);
		adHoc.downup(p);



		//Add default specializers where required
		defaultSpecializers.function(p);
		
		
		//Add default packs where required
		treeTokens = new CommonTreeNodeStream(p);
		DefaultPack defaultPack = new DefaultPack(treeTokens, modules);
		defaultPack.setTreeAdaptor(treeAdaptor);
		defaultPack.downup(p);
		
		
		//Insert guide specializers
		treeTokens = new CommonTreeNodeStream(p);
		GuideSpecializers guideSpecailizers  = new GuideSpecializers(treeTokens, adapter);
		guideSpecailizers.setTreeAdaptor(treeAdaptor);
		guideSpecailizers.downup(p);
		
		
		//Ensure that auto-guide requirements are met
		EnsureGuideOp ensure = new EnsureGuideOp(treeTokens,modules); 
		ensure.setTreeAdaptor(treeAdaptor);		
		p = (Program) ensure.transform(p);
		
		
		//Prime tree nodes with operators from the modules cache
		SetOperators set = new SetOperators(treeTokens, modules);
		set.downup(p);
		
		treeTokens = new CommonTreeNodeStream(p);
		AutoGuide ag = new AutoGuide(treeTokens, modules);
		ag.setTreeAdaptor(treeAdaptor);
		p = (Program) ag.transform(p);
		
		treeTokens = new CommonTreeNodeStream(p);
		TupleRefChain trc = new TupleRefChain(treeTokens);
		trc.setTreeAdaptor(treeAdaptor);
		p = (Program) trc.downup(p);
		
		validate(p);
		
		return p;
	}
	
	//Run common validators.
	private static void validate(StencilTree t) {
		//Since validators don't permute the tree in any way, one token stream might be enough...
		CommonTreeNodeStream treeTokens =new CommonTreeNodeStream(t);
		
		RangeValidator rangeValidator = new RangeValidator(treeTokens);
		rangeValidator.downup(t);
	}
}
