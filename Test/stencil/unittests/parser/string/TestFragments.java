package stencil.unittests.parser.string;

import java.io.FileInputStream;
import java.util.Properties;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;

import stencil.adapters.java2D.Adapter;
import stencil.module.Module;
import stencil.module.ModuleCache;
import stencil.parser.ParseStencil;
import stencil.parser.string.AdHocOperators;
import stencil.parser.string.AnnotateEnvironmentSize;
import stencil.parser.string.DefaultSpecializers;
import stencil.parser.string.EnsureOrders;
import stencil.parser.string.Imports;
import stencil.parser.string.LiftStreamPrototypes;
import stencil.parser.string.OperatorExplicit;
import stencil.parser.string.OperatorExtendFacets;
import stencil.parser.string.OperatorInstantiateTemplates;
import stencil.parser.string.OperatorToOpTemplate;
import stencil.parser.string.PrepareCustomArgs;
import stencil.parser.string.SeparateRules;
import stencil.parser.string.StencilParser;
import stencil.parser.string.PreparsePython;
import stencil.parser.tree.*;
import stencil.testUtilities.StringUtils;
import junit.framework.TestCase;

import static stencil.parser.ParseStencil.TREE_ADAPTOR;

public class TestFragments extends TestCase {
	private interface Test {
		public boolean check(Tree t);
		public String getMessage();
	}

	private Program init(String source) throws Exception {
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream("./TestData/Stencil.properties"));
		stencil.Configure.loadProperties(props);

		return ParseStencil.checkParse(source);
	}
	
	public void testImport() throws Exception {
		CommonTreeNodeStream treeTokens;
		Tree t = init("import JUNG");

		treeTokens = new CommonTreeNodeStream(t);
		Imports imports = new Imports(treeTokens);
		ModuleCache modules = imports.processImports(t);
		
		assertNotNull("Import unsuccessful; could not find module by name.", modules.getModule("JUNG"));
		assertNotNull("Could not find imported operator's module.", modules.findModuleForOperator("BalloonLayout"));		

		
		t = init("import JUNG as JG");

		treeTokens = new CommonTreeNodeStream(t);
		imports = new Imports(treeTokens);
		modules = imports.processImports(t);
		
		assertNotNull("Proxy name import unsuccessful; could not find  module by prefix.", modules.getModule("JG"));
		
		assertNotNull("Could not find prefixed operator's module.", modules.findModuleForOperator("JG::BalloonLayout"));
	}
	
	public void testAdHocPrime() throws Exception {
		String source = StringUtils.getContents("./TestData/RegressionImages/SeeTest/SeeTest.stencil", true);
		Program p = init(source);
		Adapter adapter = Adapter.INSTANCE;
		CommonTreeNodeStream treeTokens = new CommonTreeNodeStream(p);


		//Create prototype definitions for internally defined streams
		LiftStreamPrototypes liftStreams = new LiftStreamPrototypes(treeTokens);
		liftStreams.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) liftStreams.downup(p);

		//Group the operator chains
		SeparateRules separate = new SeparateRules(treeTokens);
		separate.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) separate.downup(p);

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
		p = (Program) defaultSpecializers.downup(p);

		//Converting all operator defs to template/ref pairs
		OperatorToOpTemplate opToTemplate = new OperatorToOpTemplate(treeTokens);
		opToTemplate.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) opToTemplate.downup(p);		

		//Remove all operator references
		treeTokens = new CommonTreeNodeStream(p);
		treeTokens.setTreeAdaptor(TREE_ADAPTOR);
		OperatorInstantiateTemplates opInstTemplates = new OperatorInstantiateTemplates(treeTokens, modules);
		opInstTemplates.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) opInstTemplates.downup(p);

		OperatorExplicit opExplicit = new OperatorExplicit(treeTokens);
		opExplicit.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) opExplicit.downup(p);		
		
		//Expand operatorDefs to include query and stateID
		OperatorExtendFacets opExtendFacets = new OperatorExtendFacets(treeTokens);
		opExtendFacets.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) opExtendFacets.transform(p);

		//Annotate call chains with the environment size (must be done before layer creation because defaults can have call chains)
		AnnotateEnvironmentSize envSize = new AnnotateEnvironmentSize(treeTokens);
		envSize.setTreeAdaptor(TREE_ADAPTOR);
		p = (Program) envSize.downup(p);
		
		treeTokens = new CommonTreeNodeStream(p);
		AdHocOperators adHoc = new AdHocOperators(treeTokens, modules, adapter);
		adHoc.setTreeAdaptor(TREE_ADAPTOR);
		adHoc.transform(p);
		
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
					return ((Function) t).getTarget().getOperator() != null;
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
	
	public void testLiftConstantRules() throws Exception {
		String source = StringUtils.getContents("./TestData/RegressionImages/Misc/dynamicCircular.stencil", true);
		Adapter adapter = Adapter.INSTANCE;
		Program program = ParseStencil.parse(source, adapter);
		assertEquals("Incorrectly identified defaults count.", 2, program.getLayer("Nodes").getDefaults().size()); 
		assertEquals("Incorrectly identified defaults count.", 2, program.getLayer("Edges").getDefaults().size()); 
	}
	
}
