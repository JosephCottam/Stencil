package stencil.unittests.types;

import java.awt.Color;

import stencil.unittests.StencilTestCase;
import stencil.types.color.ColorCache;
import stencil.types.gradient.*;
import static stencil.types.gradient.GradientTuple.*;
public class TestGradient extends StencilTestCase {
	private static final GradientUtils.Gradient parser = new GradientUtils.Gradient(null);
	
	public void testParse() {
		GradientTuple t = parser.argumentParser("Red -> Blue");
		
		assertEquals(t, t.get(SELF));
		assertEquals(Color.red, t.get(START));
		assertEquals(Color.blue, t.get(END));

		GradientTuple t2 = parser.argumentParser("1,2,3-> 4,5,6:10:abs:cyclic");
		assertEquals(t2, t2.get(SELF));
		assertEquals(ColorCache.get(new Color(1,2,3)), t2.get(START));
		assertEquals(ColorCache.get(new Color(4,5,6)), t2.get(END));
		assertEquals(10d, t2.get(LENGTH));
		assertTrue((Boolean) t2.get(ABSOLUTE));
		assertTrue((Boolean) t2.get(ABSOLUTE));

	}
}
