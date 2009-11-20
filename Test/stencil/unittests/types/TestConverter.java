package stencil.unittests.types;

import java.awt.Color; 


import stencil.types.Converter;
import junit.framework.TestCase;

public class TestConverter extends TestCase {

	public void testColor() {
		stencil.types.color.Color c = new stencil.types.color.Color();
		
		assertEquals(c.toTuple(Color.RED), Converter.convert("@color(1.0,0,0)", Color.class));
		assertEquals(c.toTuple(Color.RED), Converter.convert("@color(255,0,0)", Color.class));
	}
	
	public void testInteger() {
		assertEquals((Integer) 1, Converter.toInteger("1"));
		assertEquals((Integer) 100, Converter.toInteger(100));
		assertEquals((Integer) 89234, Converter.toInteger("89234"));
	}
	
	public void testDouble() {
		assertEquals(42835.24354d, Converter.toDouble(42835.24354));
		assertEquals(1.0d, Converter.toDouble(1));
		assertEquals(1.0d, Converter.toDouble("1"));
		assertEquals(3.4343, Converter.toDouble("3.4343"));
	}
	
	public void testString() {
		assertEquals("hello", Converter.toString("hello"));
		assertEquals("1", Converter.toString(1));
		assertEquals("203", Converter.toString(203));
		assertEquals("1.0", Converter.toString(1.0));
		assertEquals("@color(255,0,0)", Converter.toString(Color.RED));
	}
	
}
