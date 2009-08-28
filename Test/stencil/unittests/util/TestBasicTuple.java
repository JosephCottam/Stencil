package stencil.unittests.util;

import junit.framework.TestCase;
import java.util.List;
import java.util.Arrays;

import stencil.streams.Tuple;
import stencil.util.BasicTuple;

public class TestBasicTuple extends TestCase {
	private Tuple reference;
	
	public static final String source = "TestTuple";
	public static final List<String> names = Arrays.asList(new String[]{"StringField", "IntegerField", "DoubleField", "Field4", "ColorField"}); 
	public static final List<String> values = Arrays.asList(new String[]{"String", "2", "3.14", "Value4", "@color(100,10,10)"}); 

	public void setUp() {
		reference = new BasicTuple(source, names, values);
	}
		
	public void testGet()
	{
		assertEquals("String", reference.get("StringField"));
		assertEquals(String.class, reference.get("StringField", String.class).getClass());
		assertEquals("String", reference.get("StringField", String.class));

		assertEquals("2", reference.get("IntegerField"));
		assertEquals(Integer.class, reference.get("IntegerField", Integer.class).getClass());
		assertEquals(new Integer(2), reference.get("IntegerField", Integer.class));

		assertEquals("3.14", reference.get("DoubleField"));
		assertEquals(Double.class, reference.get("DoubleField", Double.class).getClass());
		assertEquals(new Double(3.14), reference.get("DoubleField", Double.class));

		assertEquals("Value4", reference.get("Field4"));
		assertTrue(reference.get("ColorField", java.awt.Color.class) instanceof java.awt.Color);
		assertEquals(values.get(4), reference.get("ColorField", java.awt.Color.class).toString());
	}
	
	public void testSingleton() {
		Tuple t = BasicTuple.singleton("MyValue");
		assertEquals("MyValue", t.get(Tuple.DEFAULT_KEY));
	}
}
