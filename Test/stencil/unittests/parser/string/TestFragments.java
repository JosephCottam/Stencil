package stencil.unittests.parser.string;

import java.io.FileInputStream;
import java.util.Properties;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;

import stencil.adapters.java2D.Adapter;
import stencil.operator.module.Module;
import stencil.operator.module.ModuleCache;
import stencil.parser.string.AdHocOperators;
import stencil.parser.string.Imports;
import stencil.parser.string.ParseStencil;
import stencil.parser.string.StencilLexer;
import stencil.parser.string.StencilParser;
import stencil.parser.tree.*;
import stencil.parser.validators.PythonValidator;
import stencil.testUtilities.StringUtils;
import junit.framework.TestCase;

public class TestFragments extends TestCase {
	private interface Test {
		public boolean check(Tree t);
		public String getMessage();
	}

	private Program init(String source) throws Exception {
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream("./TestData/Stencil.properties"));
		stencil.Configure.loadProperties(props);

		ANTLRStringStream input = new ANTLRStringStream(source);
		StencilTreeAdapter treeAdapter = new StencilTreeAdapter();

		StencilLexer lexer = new StencilLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		StencilParser parser = new StencilParser(tokens);
		parser.setTreeAdaptor(treeAdapter);
		Program t= (Program) parser.program().getTree();

		return t;
	}
	
	public void testImport() throws Exception {
		fail("Not tested");
//		CommonTreeNodeStream treeTokens;
//		Tree t = init("");
//
//		treeTokens = new CommonTreeNodeStream(t);
//		Imports imports = new Imports(treeTokens);
//		ModuleCache modules = imports.processImports(t);
	}
	
	public void testAdHocPrime() throws Exception {
		CommonTreeNodeStream treeTokens;
		String source = StringUtils.getContents("./TestData/RegressionImages/SeeTest/SeeTest.stencil", true);
		Program p = init(source);
		Adapter adapter = Adapter.INSTANCE;

		treeTokens = new CommonTreeNodeStream(p);
		Imports imports = new Imports(treeTokens);
		ModuleCache modules = imports.processImports(p);

		//Create ad-hoc operators
		treeTokens = new CommonTreeNodeStream(p);
		PythonValidator pyValidator = new PythonValidator(treeTokens);
		pyValidator.downup(p);
		
		treeTokens = new CommonTreeNodeStream(p);
		AdHocOperators adHoc = new AdHocOperators(treeTokens, modules, adapter);
		adHoc.downup(p);
		
		Module m = modules.getAdHoc();
		int expected = p.getOperators().size() + p.getPythons().size() + p.getLayers().size();
		assertEquals("Ad-hoc legends size incorrect.", expected, m.getModuleData().getOperators().size());
	}
	
	public void testOpCreate() throws Exception {
		String source = StringUtils.getContents("./TestData/RegressionImages/SeeTest/SeeTest.stencil", true);
		Adapter adapter = Adapter.INSTANCE;

		Program program = ParseStencil.parse(source, adapter);
		Test test = new Test() {

			public boolean check(Tree t) {
				if (t instanceof Function) {
					return ((Function) t).getOperator() != null;
				}
				return true;
			}
			
			public String getMessage() {return "Null operator after prime.";}
			
		};
		
		traverse(program, test);
	}
	
	private void traverse(Tree t, Test test) {
		assertTrue(test.getMessage(), test.check(t));
		for (int i=0; i< t.getChildCount(); i++) {traverse(t.getChild(i), test);}
	}
	
	public void testSpecializer() throws Exception {
		String source = StringUtils.getContents("./TestData/RegressionImages/SeeTest/SeeTest.stencil", true);
		Adapter adapter = Adapter.INSTANCE;
		Program program = ParseStencil.parse(source, adapter);
		Test test = new Test() {

			public boolean check(Tree t) {
				if (t instanceof Function) {
					return ((Function) t).getSpecializer() != null &&
						   ((Function) t).getSpecializer().getToken().getType() != StencilParser.DEFAULT;
				}
				return true;
			}

			public String getMessage() {return "Default or Null specializer after specializer pass.";}
			
		};
		traverse(program, test);
		
	}
	
}
