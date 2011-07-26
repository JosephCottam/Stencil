package stencil.unittests.tuple;

import junit.framework.TestCase;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;

public class TestSift extends TestCase {
	private static final PrototypedTuple unnested = new PrototypedArrayTuple(new String[]{"One", "Two", "Three","Four", "Five"}, new Object[]{1,2,3,4,5});
	private static final PrototypedTuple nested = new PrototypedArrayTuple(new String[]{"One.One", "One.Two", "Two.One","Two.Two", "Zero"}, new Object[]{1.1,1.2,2.1,2.2,0});

	public void testNoNesting()  {
		PrototypedTuple t2 = Tuples.sift(unnested , null);
		assertEquals(unnested .size(), t2.size());
		
		Tuple t3 = Tuples.sift(unnested , "NOT_USED");
		assertEquals(0, t3.size());
	}
	
	public void testNested()  {
		PrototypedTuple t2 = Tuples.sift(nested , null);
		assertEquals(1, t2.size());
		assertEquals("Zero", t2.prototype().get(0).name());
		
		PrototypedTuple t3 = Tuples.sift(nested , "One");
		assertEquals(2, t3.size());
		assertEquals("One", t3.prototype().get(0).name());
		
		assertFalse(t3.prototype().contains("One.One"));
	}

	
	
	public void testRestructure() {
		PrototypedTuple t2 = Tuples.restructure(nested, "One", "Two");
		assertEquals(3, t2.size());
		assertTrue(t2.prototype().contains("One"));
		assertTrue(t2.prototype().contains("Two"));
		assertTrue(t2.prototype().contains("Zero"));
		
		assertFalse(t2.prototype().contains("One.One"));
		
		assertTrue(t2.prototype().get(0).name().equals("Zero"));
		assertTrue(t2.prototype().get(1).name().equals("One"));
		assertTrue(t2.prototype().get(2).name().equals("Two"));
	}


}
