package stencil.unittests.parser.string;

import java.io.FileInputStream;
import java.util.Properties;

import org.antlr.runtime.tree.Tree;

import stencil.adapters.java2D.Adapter;
import stencil.module.Module;
import stencil.module.ModuleCache;
import stencil.parser.ParseStencil;
import stencil.parser.string.AdHocOperators;
import stencil.parser.string.AnnotateEnvironmentSize;
import stencil.parser.string.DefaultPack;
import stencil.parser.string.DefaultSpecializers;
import stencil.parser.string.ElementToLayer;
import stencil.parser.string.EnsureOrders;
import stencil.parser.string.Imports;
import stencil.parser.string.LiftStreamPrototypes;
import stencil.parser.string.OperatorExplicit;
import stencil.parser.string.OperatorExtendFacets;
import stencil.parser.string.OperatorInlineSimple;
import stencil.parser.string.OperatorInstantiateTemplates;
import stencil.parser.string.OperatorToOpTemplate;
import stencil.parser.string.Predicate_Expand;
import stencil.parser.string.PrepareCustomArgs;
import stencil.parser.string.SeparateRules;
import stencil.parser.string.StencilParser;
import stencil.parser.string.PreparsePython;
import stencil.parser.string.TupleRefDeLast;
import stencil.parser.tree.*;
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

		return ParseStencil.checkParse(source);
	}
	
	public void testImport() throws Exception {
		Tree t = init("import JUNG");

		ModuleCache modules = Imports.apply(t);
		
		assertNotNull("Import unsuccessful; could not find module by name.", modules.getModule("JUNG"));
		assertNotNull("Could not find imported operator's module.", modules.findModuleForOperator("BalloonLayout"));		

		
		t = init("import JUNG as JG");

		modules = Imports.apply(t);
		
		assertNotNull("Proxy name import unsuccessful; could not find  module by prefix.", modules.getModule("JG"));
		
		assertNotNull("Could not find prefixed operator's module.", modules.findModuleForOperator("JG::BalloonLayout"));
	}
	
	public void testAdHocPrime() throws Exception {
		String source = StringUtils.getContents("./TestData/RegressionImages/SeeTest/SeeTest.stencil", true);
		Program p = init(source);
		Adapter adapter = Adapter.ADAPTER;

		p = LiftStreamPrototypes.apply(p);		//Create prototype definitions for internally defined streams
		p = SeparateRules.apply(p);	//Group the operator chains
		p = EnsureOrders.apply(p);		//Ensure the proper order blocks

		//Do module imports
		ModuleCache modules = Imports.apply(p);
		
		
		PreparsePython.apply(p);		//Verify that Python operators are syntactically correct and appropriately indented
		p = PrepareCustomArgs.apply(p);		//Parse custom argument blocks
		p = Predicate_Expand.apply(p);			//Convert filters to standard rule chains
		p = TupleRefDeLast.apply(p);							//Remove all uses of the LAST tuple reference
		p = DefaultSpecializers.apply(p, modules, adapter, true); 			//Add default specializers where required

		p = DefaultPack.apply(p);			//Add default packs where required
		p = OperatorToOpTemplate.apply(p);							//Converting all operator defs to template/ref pairs
		p = OperatorInstantiateTemplates.apply(p, modules);		//Remove all operator references
		p = OperatorExplicit.apply(p);		
		p = OperatorInlineSimple.apply(p);			//In-line simple synthetic operators		
		p = OperatorExtendFacets.apply(p);  		//Expand operatorDefs to include query and stateID


		AnnotateEnvironmentSize.apply(p);		//Annotate call chains with the environment size (must be done before layer creation because defaults can have call chains)
		p = ElementToLayer.apply(p);		//Convert "element" statements into layers
		p = AdHocOperators.apply(p, modules, adapter);	//Create ad-hoc operators 		
		Module m = modules.getAdHoc();
		int expected = p.getOperators().size() + p.getPythons().size() + p.getLayers().size();
		assertEquals("Ad-hoc operators size incorrect.", expected, m.getModuleData().getOperators().size());
	}
	
	public void testOpCreate() throws Exception {
		String source = StringUtils.getContents("./TestData/RegressionImages/SeeTest/SeeTest.stencil", true);
		Adapter adapter = Adapter.ADAPTER;

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
		Adapter adapter = Adapter.ADAPTER;
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
		Adapter adapter = Adapter.ADAPTER;
		Program program = ParseStencil.parse(source, adapter);
		assertEquals("Incorrectly identified defaults count.", 2, program.getLayer("Nodes").getDefaults().size()); 
		assertEquals("Incorrectly identified defaults count.", 2, program.getLayer("Edges").getDefaults().size()); 
	}
	
}
