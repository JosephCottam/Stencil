package stencil.unittests.adapters.java2D.columns;

import stencil.adapters.java2D.columnStore.column.BooleanColumn;
import stencil.adapters.java2D.columnStore.column.Column;
import junit.framework.TestCase;

public class TestBooleanColumn extends TestCase {
	public void testExtend() {
		Column c = new BooleanColumn(false);
		
		Object[] vals = new Boolean[]{true,true,true};
		int[] targets = new int[]{0,1,2};
		c = c.update(vals, targets, targets.length);
		
		assertEquals("Error in initial update.", 3, c.size());
		
		vals = new Boolean[]{false, true};
		targets = new int[]{3,4};
		c = c.update(vals, targets, targets.length);
		assertEquals("Error in true-ending update.", 5, c.size());
		
		vals = new Boolean[]{true, false};
		targets = new int[]{5,6};
		c = c.update(vals, targets, targets.length);
		assertEquals("Error in false-ending update.", 7, c.size());

	
		vals = new Boolean[]{false, false, false};
		targets = new int[]{7,8,9};
		c = c.update(vals, targets, targets.length);
		assertEquals("Error in muli-false, false-ending update.", 10, c.size());
	}
	
	public void testReplace() {
		Column c = new BooleanColumn(false);
		Boolean[] bits = new Boolean[]{true,true,true,true,false, false, true,true, false, true, false, true};
		c = c.replaceAll(bits);
		
		assertEquals("Wrong size after replace.", bits.length, c.size());
		
		for (int i=0; i<bits.length; i++) {
			assertEquals("Error at bit " + i, bits[i], c.get(i));
		}
	}
	
	public void testDefaultValue() {
		Column c = new BooleanColumn(true);
		assertEquals(true, c.getDefaultValue());
		c.replaceAll(new Boolean[]{false, false, false, true});
		assertEquals(true, c.getDefaultValue());
		
		
		c = new BooleanColumn(false);
		assertEquals(false, c.getDefaultValue());
		c.replaceAll(new Boolean[]{false, false, false, true});
		assertEquals(false, c.getDefaultValue());
	}
}
