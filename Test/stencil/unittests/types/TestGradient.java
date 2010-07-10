package stencil.unittests.types;

import java.awt.Color;

import junit.framework.TestCase;
import stencil.types.gradient.*;

public class TestGradient extends TestCase {
	private static final GradientUtils.Gradients parser = new GradientUtils.Gradients(null);
	
	public void testParse() {
		GradientTuple t = parser.argumentParser("Red -> Blue");
		
		assertEquals(t.get(0), t);
		assertEquals(t.get(1), Color.red);
		assertEquals(t.get(2), Color.blue);
	}
}
