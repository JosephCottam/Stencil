package stencil.unittests.module.operator;

import stencil.adapters.Adapter;
import stencil.module.operator.*;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.MethodInvokeFailedException;
import stencil.module.operator.wrappers.SyntheticOperator;
import stencil.module.operator.wrappers.SyntheticOperator.NoMatchException;
import stencil.module.util.FacetData;
import stencil.parser.ParseStencil;
import stencil.parser.tree.StencilTree;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.unittests.module.TestModuleCache;
import junit.framework.TestCase;
import static stencil.parser.string.StencilParser.LIST_OPERATORS;

public class TestSyntheticOperator extends TestCase {
	public static final String fullOperatorSource = "operator full(X,Y,Z) -> (X,Y,Z) (X != NULL) => (X,Y,Z) : (Z,Y,X)";			
	public static final String basicOperatorSource = "operator basic(A,B,C,D,E,F,G) -> (Z,Y,X) (A != NULL) => (Z,Y,X): (A,E,G)";
	public static final Adapter ADAPTER = stencil.adapters.java2D.Adapter.ADAPTER;
	
	public void setUp() throws Exception {
		TestModuleCache.initCache();
	}
	
	public void testGenerate() throws Exception {
		StencilTree program = ParseStencil.programTree(fullOperatorSource, ADAPTER);
		StencilOperator operator = new SyntheticOperator("TestModule", program.find(LIST_OPERATORS).getChild(0));
		
		assertEquals("full", operator.getName());
		assertEquals(SyntheticOperator.class, operator.getClass());

		program = ParseStencil.programTree(basicOperatorSource, ADAPTER);
		operator = new SyntheticOperator("TestModule", program.find(LIST_OPERATORS).getChild(0));
		assertEquals("basic", operator.getName());
		assertEquals(SyntheticOperator.class, operator.getClass());
	}

	public void testMap() throws Exception {
		StencilTree program = ParseStencil.programTree(fullOperatorSource, ADAPTER);
		SyntheticOperator operator = new SyntheticOperator("TestModule", program.find(LIST_OPERATORS).getChild(0));
		FacetData facetData = operator.getOperatorData().getFacet(StencilOperator.MAP_FACET);
		TuplePrototype prototype = facetData.getPrototype();
		Invokeable map = operator.getFacet(StencilOperator.MAP_FACET);
		Tuple rv;
		
		boolean error = false;
		try {map.invoke(new Object[]{null, "2","3"});}
		catch (MethodInvokeFailedException ex) {
			if (ex.getCause().getCause() instanceof NoMatchException) {error = true;}
			else {throw ex;}
		}
		assertTrue("NoMatchException not thrown when expected.", error);

		rv = (Tuple) map.invoke(new Object[]{"0", "1", "2"});
		
		assertEquals(3, rv.size());
		assertEquals("0", rv.get(prototype.indexOf("Z")));
		assertEquals("1", rv.get(prototype.indexOf("Y")));
		assertEquals("2", rv.get(prototype.indexOf("X")));
	}

	public void testMapNulls() throws Exception {
		StencilTree program = ParseStencil.programTree(basicOperatorSource, ADAPTER);

		SyntheticOperator operator = new SyntheticOperator("TestModule", program.find(LIST_OPERATORS).getChild(0));
		Invokeable map = operator.getFacet(StencilOperator.MAP_FACET);

		boolean error = false;
		try {
			map.invoke(new Object[]{null, null, null,null,null,null,null});
		} catch (MethodInvokeFailedException ex) {
			if (ex.getCause().getCause() instanceof NoMatchException) {error = true;}
			else {throw ex;}
		}
		assertTrue("NoMatchException not thrown when expected.", error);
	}

	public void testGenerateFail() throws Exception {
		String source = "operator bad(X,Y) -> (X,Y,Z) (all) => (Z,Y,X): (X,Y)";
		boolean failed = false;
		try {
			ParseStencil.program(source, ADAPTER);
		} catch (Exception e) {failed = true;}
		finally {assertTrue("Exception not thrown when spec has argument mismatch in return.", failed);}

		//Unknown argument in filter
		source = "operator bad(X,Y,Z) -> (X,Y,Z) (A=~ \".*\") => (X,Y,Z) : (Z,Y,X)";
		failed = false;
		try {
			ParseStencil.program(source, ADAPTER);
		} catch (Exception e) {failed = true;}
		finally {assertTrue("Exception not thrown with unknown argument in filter.", failed);}
	}

	public void testMapfail() throws Exception {
		StencilTree program = ParseStencil.programTree(fullOperatorSource, ADAPTER);
		StencilOperator operator = new SyntheticOperator("TestModule", program.find(LIST_OPERATORS).getChild(0));
		Invokeable map = operator.getFacet(StencilOperator.MAP_FACET);
		
		boolean failed = false;
		try {
			map.invoke(new String[]{"1", "2","3", "4"});
		} catch (Exception e) {failed = true;}
		finally {assertTrue("Operator succeeded with too many arguments.", failed);}

		failed = false;
		try {
			map.invoke(new String[]{"1", "2"});
		} catch (Exception e) {failed = true;}
		finally {assertTrue("Operator succeeded with too few arguments.", failed);}
	}
}
