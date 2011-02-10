package stencil.unittests.parser.tree;

import java.awt.Color;

import stencil.parser.tree.Const;
import stencil.parser.tree.StencilTreeAdapter;
import stencil.tuple.Tuple;
import stencil.types.color.ColorWrapper;
import static stencil.parser.string.StencilParser.*;
import junit.framework.TestCase;

public class TestStencilTree extends TestCase {
	private static final StencilTreeAdapter adaptor = new StencilTreeAdapter();
	
	public void testHashCode() {
		Object ref1 = adaptor.create(TUPLE_REF, "Ref");
		Object ref2 = adaptor.create(TUPLE_REF, "Ref");
		
		assertTrue("Hash codes did not match on same type & text", ref1.hashCode() == ref2.hashCode());
		
		ref2 = adaptor.create(TUPLE_REF, "Ref2");
		assertFalse("Hash codes matched on same type & different text", ref1.hashCode() == ref2.hashCode());
		
		Object v2 = adaptor.create(STRING, "Ref2");
		assertFalse("Hash codes matched on different type & same text", v2.hashCode() == ref2.hashCode());

		
		adaptor.addChild(ref1, adaptor.create(ID, "id"));
		ref2 = adaptor.dupTree(ref1);

		assertTrue("Hash codes did not match on duplicate trees", ref1.hashCode() == ref2.hashCode());

		adaptor.addChild(ref2, adaptor.create(NUMBER, "0"));
		assertFalse("Hash codes matched on different child", ref1.hashCode() == ref2.hashCode());		

		adaptor.addChild(ref1, adaptor.create(NUMBER, "0"));
		assertTrue("Hash codes did not match on matching (but not duplicated) trees", ref1.hashCode() == ref2.hashCode());
	}
		
	public void testConstString() {
		testConst("Test", new String("Test"));
		testConst("ELLIPSE", new String("ELLIPSE"));
	}
	
	public void testConstNumber() {
		testConst(2,2);
		testConst(Integer.valueOf(2), Integer.valueOf(2));
		testConst(Integer.valueOf(300000), Integer.valueOf(300000));	//Clear the internal caches
		testConst(2.5, 2.5);
		testConst(Double.valueOf(250923.1324),Double.valueOf(250923.1324));
	}
	
	
	public void testConstColor() {
		Color color1 = new Color(128,0,0);
		Color color2 = new Color(128,0,0);
		Tuple colorTuple1 = new ColorWrapper().toTuple(color1);
		Tuple colorTuple2 = new ColorWrapper().toTuple(color1);

		testConst(colorTuple1, colorTuple2);
		
		colorTuple2 = new ColorWrapper().toTuple(color2);
		testConst(colorTuple1, colorTuple2);
	}
	
	private void testConst(Object v1, Object v2) {
		Const c1 = (Const) adaptor.create(CONST, "CONST");
		Const c2 = (Const) adaptor.create(CONST, "CONST");
		Const c3 = (Const) adaptor.create(CONST, "CONST");
		Const c4 = (Const) adaptor.create(CONST, "CONST");

		assertEquals("Must supply v1 and v2 that are equal.", v1,v2);
		
		c1.setValue(v1);
		c2.setValue(v1);
		assertEquals("Not equal containing same object reference.", c1, c2);
		assertEquals("Not equal containing same object reference.", c2, c1);
		assertEquals("Not same hashcode on same object reference.", c1.hashCode(),c2.hashCode());
		
		c3.setValue(v2);
		assertEquals("Not equal containing same value, different instances.", c1, c3);
		assertEquals("Not equal containing same value, different instances.", c3, c1);
		assertEquals("Not same hashcode containing same value, different instances.", c1.hashCode(), c3.hashCode());		
		
		
		Object o = new Object();
		c4.setValue(o);
		assertFalse("Equal, when expected unequal.", c1.equals(c4));
		assertFalse("Equal, when expected unequal.", c4.equals(c1));
		
	}
}
