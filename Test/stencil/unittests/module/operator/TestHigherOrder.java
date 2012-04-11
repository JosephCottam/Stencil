package stencil.unittests.module.operator;

import stencil.adapters.java2D.Adapter;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.wrappers.SyntheticOperator;
import stencil.module.util.FacetData;
import stencil.parser.ParseStencil;
import stencil.parser.tree.*;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MultiResultTuple;
import stencil.unittests.StencilTestCase;
import static stencil.parser.string.StencilParser.*;

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

	
	private final String MAP_TEST = "stream S(v1, v2)\n layer L\n from S\n ID: Map(@Reform, \"map\", v1)\n X: Map(@Add1, \"map\", *)\n operator Reform (v) -> (v) default => v: Trim(v)";
	public void testSimpleMap() throws Exception {
		StencilTree p = ParseStencil.programTree(MAP_TEST, Adapter.ADAPTER);
		
		//TODO: Fix this searching...it is horribly fragile
		OperatorProxy sumMap = (OperatorProxy) p.findAllDescendants(OPERATOR_PROXY).get(2);	//Map operator (generic)
		OperatorProxy add1 = (OperatorProxy) p.findAllDescendants(OPERATOR_PROXY).get(1);	//add1 for map
		
		assertNotNull("Test not properly configured.", sumMap);
		assertNotNull("Map operator not instantiated.", sumMap.getOperator());
		assertTrue("Did not find Map operator when expected.  Instead, found: " + sumMap.getName(), sumMap.getName().contains("#Map_"));
		assertTrue("Did not find Add1 operator when expected.  Instead, found: " + add1.getName(), add1.getName().contains("#Add1"));
		//TODO: Test that the map is targeting add1

		FacetData fd = sumMap.getOperatorData().defaultFacet();
		Invokeable iMap = sumMap.getOperator().getFacet(fd.name());
		Invokeable iQuery = sumMap.getOperator().getFacet(fd.counterpart());
		
		assertNotNull("Facet on map not not found.", iMap);
		assertNotNull("Facet on map not not found.", iQuery);
		
		ArrayTuple numbers = ArrayTuple.from(1,2,3,4,5);
				
		assertEquals(mapTuple(2.0d,3.0d,4.0d,5.0d,6.0d), iMap.invoke(new Object[]{add1.getOperator(), "map", numbers}));
	}
	
	public void testSyntheticMap() throws Exception {
		StencilTree p = ParseStencil.programTree(MAP_TEST, Adapter.ADAPTER);

		OperatorProxy reformMap = (OperatorProxy) p.findAllDescendants(OPERATOR_PROXY).get(2);	//Map operator (generic)
		SyntheticOperator reform = new SyntheticOperator("test group", p.findDescendant(OPERATOR));
		assertNotNull("Test not properly configured.", reformMap);
		assertNotNull("Map operator not instantiated.", reformMap.getOperator());
		assertTrue("Did not find Map operator when expected.  Instead, found: " + reformMap.getName(), reformMap.getName().contains("#Map_"));
		assertTrue("Did not find Reform operator when expected.  Instead, found: " + reform.getName(), reform.getName().contains("Reform"));

		FacetData fd = reformMap.getOperatorData().defaultFacet();
		Invokeable iMap = reformMap.getOperator().getFacet(fd.name());
		Invokeable iQuery = reformMap.getOperator().getFacet(fd.counterpart());
		assertNotNull("Facet on map not not found.", iMap);
		assertNotNull("Facet on map not not found.", iQuery);
		
		ArrayTuple values = ArrayTuple.from("   A", "   1","   B","   2");
		assertEquals(mapTuple("A","1", "B", "2"), iMap.invoke(new Object[]{reform, "map", values}));
	}
	
	private MultiResultTuple mapTuple(Object... values) {
		Tuple[] ts = new Tuple[values.length];
		for (int i=0; i< values.length; i++) {ts[i] = new ArrayTuple(values[i]);}
		return new MultiResultTuple(ts);
	}
}
