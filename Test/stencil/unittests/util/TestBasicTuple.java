package stencil.unittests.util;

import junit.framework.TestCase;
import java.util.List;
import java.util.Arrays;

import stencil.parser.ParserConstants;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;

public class TestBasicTuple extends TestCase {
	private Tuple reference;
	
	public static final String SOURCE = "TestTuple";
	public static final List<String> names = Arrays.asList(new String[]{"StringField", "IntegerField", "DoubleField", "Field4", "ColorField", ParserConstants.SOURCE_FIELD}); 
	public static final List<Object> values = Arrays.asList(new Object[]{"String", 2, 3.14, "Value4", "@color(100,10,10)", SOURCE}); 
	public static final List<Class> types = Arrays.asList(new Class[]{String.class, Integer.class, Double.class, String.class, String.class, String.class});
	
	public void setUp() {
		reference = new PrototypedTuple(names, types, values);
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
