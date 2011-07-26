package stencil.unittests.module.operator;

import stencil.adapters.java2D.Adapter;
import stencil.module.operator.util.Invokeable;
import stencil.parser.ParseStencil;
import stencil.parser.tree.*;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MapMergeTuple;
import stencil.unittests.StencilTestCase;
import static stencil.parser.string.StencilParser.*;
import static stencil.module.operator.StencilOperator.*;

public class TestHigherOrder extends StencilTestCase {

	public void testSplit() throws Exception {
//		fail("Not tested");
		
		//Old test code from average (when split was not higher-order)
//		public void testSplitRange() throws Exception {
//			StencilOperator meaner = average.instance("Mean", makeContext("Mean", "Split", "[fields:1, ordered:\"#true\"]"), ParserConstants.EMPTY_SPECIALIZER);		
//			Invokeable map = meaner.getFacet(StencilOperator.MAP_FACET);
//			Invokeable query = meaner.getFacet(StencilOperator.QUERY_FACET);
//
//			String[] splits = new String[]{"One", "Two", "Three"};
//			
//			for (String split: splits) {
//				int count =0;
//				double sum =0;
//				for (int i=0; i<100; i++) {
//					sum = sum+i;
//					count++;
//					map.invoke(new Object[]{split, i});
//					assertEquals(sum/count, query.invoke(new Object[]{split}));			
//
//				}
//			}
//		}
//		
	}

	public void testRange() throws Exception {
//		fail("Not tested");
	}

	
	private final String MAP_TEST = "stream S(v1, v2)\n layer L\n from S\n ID: Map(@Reform, v1)\n X: Map(@Add1, *)\n operator Reform (v) -> (v) default => v: Trim(v)";
	public void testSimpleMap() throws Exception {
		StencilTree p = ParseStencil.programTree(MAP_TEST, Adapter.ADAPTER);
		
		OperatorProxy sumMap = (OperatorProxy) p.findAllDescendants(OPERATOR_PROXY).get(2);	//Map for add1
		assertNotNull("Test not properly configured.", sumMap);
		assertNotNull("Map operator not instantiated.", sumMap.getOperator());
		assertTrue(sumMap.getName().contains("#Map_"));
		//TODO: Test that the map is targeting add1

		Invokeable iMap = sumMap.getOperator().getFacet(MAP_FACET);
		Invokeable iQuery = sumMap.getOperator().getFacet(QUERY_FACET);
		
		assertNotNull("Facet on map not not found.", iMap);
		assertNotNull("Facet on map not not found.", iQuery);
		
		ArrayTuple numbers = ArrayTuple.from(1,2,3,4,5);
		assertEquals(mapTuple(2.0d,3.0d,4.0d,5.0d,6.0d), iMap.invoke(new Object[]{numbers}));
	}
	
	public void testSyntheticMap() throws Exception {
		StencilTree p = ParseStencil.programTree(MAP_TEST, Adapter.ADAPTER);

		OperatorProxy reformMap = (OperatorProxy) p.findAllDescendants(OPERATOR_PROXY).get(1);	//Map for reform
		assertNotNull("Test not properly configured.", reformMap);
		assertNotNull("Map operator not instantiated.", reformMap.getOperator());
		assertTrue(reformMap.getName().contains("#Map_"));

		Invokeable iMap = reformMap.getOperator().getFacet(MAP_FACET);
		Invokeable iQuery = reformMap.getOperator().getFacet(QUERY_FACET);
		assertNotNull("Facet on map not not found.", iMap);
		assertNotNull("Facet on map not not found.", iQuery);
		
		ArrayTuple values = ArrayTuple.from("   A", "   1","   B","   2");
		assertEquals(mapTuple("A","1", "B", "2"), iMap.invoke(new Object[]{values}));
	}
	
	private MapMergeTuple mapTuple(Object... values) {
		Tuple[] ts = new Tuple[values.length];
		for (int i=0; i< values.length; i++) {ts[i] = new ArrayTuple(values[i]);}
		return new MapMergeTuple(ts);
	}
}
