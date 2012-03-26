package stencil.unittests.adapters.java2D.guides;

import java.util.ArrayList;
import java.util.List;

import stencil.adapters.java2D.render.guides.*;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;

public class TestDensity extends stencil.unittests.StencilTestCase {
	public void testBinCounts() {
		List<Tuple> tuples = makeTuples(10); 
		int[] counts = Density.binCounts(tuples, 0, 1);
		
		for (int i=0; i<counts.length; i++) {
			assertEquals("Unexpected bin quantity in bin " + i, 1, counts[i]);
		}
	}
	
	private static List<Tuple> makeTuples(int size) {
		List<Tuple> tuples = new ArrayList(); 
		for (int i=0; i<10; i++) {tuples.add(new ArrayTuple(i));}
		return tuples;
	}
}
