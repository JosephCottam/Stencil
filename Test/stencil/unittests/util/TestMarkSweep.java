package stencil.unittests.util;

import junit.framework.*;
import stencil.util.collections.MarkSweepSet;

public class TestMarkSweep extends TestCase {

	public void testSweep() {
		MarkSweepSet set = new MarkSweepSet();

		for (int i=0; i< 100; i++) {set.add(i);}
		assertEquals(100, set.size());
		set.sweep();
		assertEquals(100, set.size());

		for (int i=0; i< 25; i++) {set.contains(i);}
		assertEquals(100, set.size());
		set.sweep();
		assertEquals(25, set.size());

		set.sweep();
		assertEquals(0, set.size());
	}
}
