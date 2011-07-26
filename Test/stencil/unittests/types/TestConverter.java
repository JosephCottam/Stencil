package stencil.unittests.types;

import java.awt.Color; 


import stencil.tuple.instances.*;
import stencil.types.Converter;
import stencil.unittests.StencilTestCase;

public class TestConverter extends StencilTestCase {

	public void testColor() {
		stencil.types.color.ColorCache c = new stencil.types.color.ColorCache();
		
		assertEquals(c.toTuple(Color.RED), Converter.convert("@Color{255,0,0}", Color.class));
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
		assertEquals("@Color{255,0,0}", Converter.toString(Color.RED));
	}
	
	public void testToTuple() {
		assertTrue(Converter.toTuple("Hello") instanceof Singleton);
		assertEquals(Ints.class, Converter.toTuple(new int[]{1,2,3}).getClass());
		assertEquals(Doubles.class, Converter.toTuple(new double[]{1.0,2.0,3.0}).getClass());
		assertEquals(NumericSingleton.class, Converter.toTuple(1.0).getClass());
		assertEquals(NumericSingleton.class, Converter.toTuple(1).getClass());
	}
	
}
