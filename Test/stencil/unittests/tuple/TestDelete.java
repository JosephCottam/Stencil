package stencil.unittests.tuple;

import junit.framework.TestCase;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.PrototypedArrayTuple;

public class TestDelete extends TestCase {
	private static final PrototypedTuple PROTOTYPED  = new PrototypedArrayTuple(new String[]{"One", "Two", "Three","Four", "Five"}, new Object[]{1,2,3,4,5});
	private static final Tuple UNPROTOTYPED= ArrayTuple.from(1.1,1.2,2.1,2.2,0);

	public void testPrototyped()  {
		PrototypedTuple t2 = Tuples.delete(PROTOTYPED, 0);
		assertEquals("Did not remove an item on delete.", PROTOTYPED.size()-1, t2.size());
		assertEquals("Prototype not correctly changed on delete.", PROTOTYPED.prototype().size()-1, t2.prototype().size());
		assertFalse("Prototype not correctly changed on delete.", t2.prototype().contains("One"));
		assertEquals(PROTOTYPED.get(1), t2.get(0));
		
		t2 = Tuples.delete(PROTOTYPED, PROTOTYPED.size()-1);
		assertEquals("Did not remove an item on delete.", PROTOTYPED.size()-1, t2.size());		
		assertEquals("Prototype not correctly changed on delete.", PROTOTYPED.prototype().size()-1, t2.prototype().size());
		assertFalse("Prototype not correctly changed on delete.", t2.prototype().contains("Five"));
		assertEquals(PROTOTYPED.get(3), t2.get(3));
		

		t2 = Tuples.delete(PROTOTYPED, 2);
		assertEquals("Did not remove an item on delete.", PROTOTYPED.size()-1, t2.size());		
		assertEquals("Prototype not correctly changed on delete.", PROTOTYPED.prototype().size()-1, t2.prototype().size());
		assertFalse("Prototype not correctly changed on delete.", t2.prototype().contains("Three"));
		assertEquals(PROTOTYPED.get(0), t2.get(0));
		assertEquals(PROTOTYPED.get(4), t2.get(3));
	}
	
	public void testUnprototyped()  {
		Tuple t2 = Tuples.delete(UNPROTOTYPED, 0);
		assertEquals("Did not remove an item on delete.", PROTOTYPED.size()-1, t2.size());		
		assertEquals(UNPROTOTYPED.get(1), t2.get(0));
	
		t2 = Tuples.delete(UNPROTOTYPED, UNPROTOTYPED.size()-1);
		assertEquals("Did not remove an item on delete.", UNPROTOTYPED.size()-1, t2.size());		
		assertEquals(UNPROTOTYPED.get(3), t2.get(3));
		

		t2 = Tuples.delete(UNPROTOTYPED, 2);
		assertEquals("Did not remove an item on delete.", UNPROTOTYPED.size()-1, t2.size());
		assertEquals(UNPROTOTYPED.get(0), t2.get(0));
		assertEquals(UNPROTOTYPED.get(4), t2.get(3));
	}
	
	public void testMulti()  {
		Tuple t2 = Tuples.delete(UNPROTOTYPED, 1,2);
		assertEquals(UNPROTOTYPED.size()-2, t2.size());	//Pre-sorted indexes
		assertEquals(UNPROTOTYPED.get(0), t2.get(0));
		assertEquals(UNPROTOTYPED.get(3), t2.get(1));
		
		
		t2 = Tuples.delete(UNPROTOTYPED, 3,1,2);		//Unsorted indexes
		assertEquals(UNPROTOTYPED.size()-3, t2.size());
		assertEquals(UNPROTOTYPED.get(0), t2.get(0));
		assertEquals(UNPROTOTYPED.get(4), t2.get(1));
	}
}
