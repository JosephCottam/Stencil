package stencil.unittests.parser.string;

import java.io.FileInputStream;
import java.util.Properties;

import org.antlr.runtime.tree.Tree;

import stencil.adapters.java2D.Adapter;
import stencil.interpreter.tree.MultiPartName;
import stencil.module.ModuleCache;
import stencil.module.operator.StencilOperator;
import stencil.parser.ParseStencil;
import stencil.parser.string.*;
import stencil.parser.string.util.Utilities;
import stencil.parser.string.validators.TargetMatchesPack;
import stencil.parser.tree.*;
import stencil.util.FileUtils;
import static stencil.parser.string.StencilParser.*;
import junit.framework.TestCase;

public class TestFragments extends TestCase {
	private interface Test {
		public boolean check(StencilTree t);
		public String getMessage();
	}

	private StencilTree init(String source) throws Exception {
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream("./TestData/Stencil.properties"));
		stencil.Configure.loadProperties(props);

		return ParseStencil.checkParse(source);
	}
	
	public void testImport() throws Exception {
		Tree t = init("import JUNG\n");

		ModuleCache modules = Imports.apply(t);
		
		assertNotNull("Import unsuccessful; could not find module by name.", modules.getModule("JUNG"));
		assertNotNull("Could not find imported operator's module.", modules.findModuleForOperator(new MultiPartName("", "BalloonLayout")));		

		
		t = init("import JUNG : JG");

		modules = Imports.apply(t);
		
		assertNotNull("Proxy name import unsuccessful; could not find  module by prefix.", modules.getModule("JG"));
		
		assertNotNull("Could not find prefixed operator's module.", modules.findModuleForOperator(new MultiPartName("JG", "BalloonLayout")));
	}
	
	public void testAdHocPrime() throws Exception {
		String source = FileUtils.readFile("./TestData/RegressionImages/SeeTest/SeeTest.stencil");
		StencilTree p = init(source);
		Adapter adapter = Adapter.ADAPTER;

		p = LiftStreamPrototypes.apply(p);		//Create prototype definitions for internally defined streams
		p = SeparateRules.apply(p);	//Group the operator chains
		p = EnsureOrders.apply(p);		//Ensure the proper order blocks

		//Do module imports
		ModuleCache modules = Imports.apply(p);
		
		p = OperatorCustomArgs.apply(p);			//Parse custom argument blocks
		p = Predicate_Expand.apply(p);			//Convert filters to standard rule chains
		p = SpecializerDeconstant.apply(p);		//Remove references to constants in specializers
		p = DefaultSpecializers.apply(p, modules, adapter); 			//Add default specializers where required
		p = DefaultPack.apply(p);				//Add default packs where required
		p = OperatorToOpTemplate.apply(p);		//Converting all operator defs to template/ref pairs
		p = OperatorExpandTemplates.apply(p);		//Remove all operator references
		p = OperatorExplicit.apply(p);				//Remove anonymous operator references; replaced with named instances and regular references
		p = OperatorExtendFacets.apply(p);  		//Expand operatorDefs to include query and stateID

		p = ElementToLayer.apply(p);					//Convert "element" statements into layers
		
		int expected = p.find(LIST_OPERATORS).getChildCount() + p.find(LIST_LAYERS).getChildCount();
		p = AdHocOperators.apply(p, modules, adapter);	//Create ad-hoc operators 		
		int found = p.find(LIST_OPERATORS).getChildCount();
		
		
		assertEquals("Ad-hoc operators size incorrect.", expected, found);
	}
	
	public void testOpCreate() throws Exception {
		String source = FileUtils.readFile("./TestData/RegressionImages/SeeTest/SeeTest.stencil");
		Adapter adapter = Adapter.ADAPTER;

		StencilTree program = ParseStencil.programTree(source, adapter);
		Test test = new Test() {

			@Override
			public boolean check(StencilTree t) {
				if (t.getType() == StencilParser.FUNCTION) {
					try {
						StencilOperator op = Utilities.findOperator(t.find(OP_NAME));
						return op != null;
					}catch (Exception e) {
						return false;
					}
				}
				return true;
			}
			
			@Override
			public String getMessage() {return "Null operator after prime.";}
			
		};
		
		traverse(program, test);
	}
	
	private void traverse(StencilTree t, Test test) {
		assertTrue(test.getMessage(), test.check(t));
		for (int i=0; i< t.getChildCount(); i++) {traverse(t.getChild(i), test);}
	}
	
	public void testSpecializer() throws Exception {
		String source = FileUtils.readFile("./TestData/RegressionImages/SeeTest/SeeTest.stencil");
		Adapter adapter = Adapter.ADAPTER;
		StencilTree program = ParseStencil.programTree(source, adapter);
		Test test = new Test() {

			@Override
			public boolean check(StencilTree t) {
				if (t.getType() == StencilParser.FUNCTION) {
					return t.find(SPECIALIZER) != null &&
						   t.find(SPECIALIZER).getToken().getType() != StencilParser.DEFAULT;
				}
				return true;
			}

			@Override
			public String getMessage() {return "Default or Null specializer after specializer pass.";}
			
		};
		traverse(program, test);
	}
	
	public void testLiftLayerConstants() throws Exception {
		String source = FileUtils.readFile("./TestData/RegressionImages/Misc/dynamicCircular.stencil");
		StencilTree program = ParseStencil.programTree(source,  Adapter.ADAPTER);
		assertEquals("Incorrectly identified defaults count on nodes.", 4, program.find(LIST_LAYERS).find(LAYER, "Nodes").find(RULES_DEFAULTS).findAllDescendants(TUPLE_FIELD).size());
		assertEquals("Incorrectly identified defaults count on edges.", 2, program.find(LIST_LAYERS).find(LAYER, "Edges").find(RULES_DEFAULTS).findAllDescendants(TUPLE_FIELD).size());
	}

	public void testTargetPackMismatch() throws Exception {
		String source = FileUtils.readFile("./TestData/RegressionImages/Misc/TargetPackMismatch.stencil.bad");
		try {ParseStencil.program(source, Adapter.ADAPTER);}
		catch (TargetMatchesPack.TargetPackMismatchException e) {/**Ignored**/}		
	}
	
	
	
}
