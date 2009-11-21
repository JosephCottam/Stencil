package stencil.unittests.util;

import junit.framework.TestCase;
import java.util.List;
import java.util.Arrays;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;

public class TestBasicTuple extends TestCase {
	private Tuple reference;
	
	public static final String source = "TestTuple";
	public static final List<String> names = Arrays.asList(new String[]{"StringField", "IntegerField", "DoubleField", "Field4", "ColorField"}); 
	public static final List<Object> values = Arrays.asList(new Object[]{"String", 2, 3.14, "Value4", "@color(100,10,10)"}); 

	public void setUp() {
		reference = new PrototypedTuple(source, names, values);
	}
		
	public void testGet()
	{
		assertEquals("String", reference.get("StringField"));
		assertEquals(2, reference.get("IntegerField"));
		assertEquals(3.14, reference.get("DoubleField"));
		assertEquals("Value4", reference.get("Field4"));
	}
	
	public void testSingleton() {
		Tuple t = PrototypedTuple.singleton("MyValue");
		assertEquals("MyValue", t.get(Tuple.DEFAULT_KEY));
	}
}
