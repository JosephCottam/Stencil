package stencil.unittests.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.adapters.java2D.data.glyphs.Pie;
import stencil.streams.InvalidNameException;
import stencil.streams.MutableTuple;
import stencil.streams.Tuple;
import stencil.types.Converter;
import stencil.util.BasicTuple;
import stencil.util.Tuples;
import junit.framework.Assert;
import junit.framework.TestCase;


public class TestTuples extends TestCase {
	private static class MutableMapTuple implements MutableTuple {
		private Map map;
		
		public MutableMapTuple(Map source) {this.map = source;}
		
		public Object get(String name) throws InvalidNameException {return map.get(name);}

		public Object get(String name, Class<?> type)
				throws IllegalArgumentException, InvalidNameException {
			return Converter.convert(get(name), type);
		}

		public List<String> getFields() {return new ArrayList(map.keySet());}

		public boolean hasField(String name) {return map.containsKey(name);}

		public boolean isDefault(String name, Object value) {return false;}

		public void set(String field, Object value) {map.put(field, value);}
	};
	
	
	public void testTransfer() throws Exception {
		Tuple reference = new BasicTuple(TestBasicTuple.source, TestBasicTuple.names, TestBasicTuple.values);
		HashMap map = new HashMap();
		map.put("One", "One");
		map.put("Two", 2);
		map.put("Three", 3.0d);
		map.put("Four", "four");
		map.put("Five", "FIVE");
		MutableTuple target = new MutableMapTuple(map);
		
		Tuples.transfer(reference, target, false);

		for (String field:reference.getFields()) {
			assertEquals(field + " did not match in transfer", reference.get(field), target.get(field));
		}
	}

	public void testConvert() throws Exception {
		testOneConversion("1", Integer.class, new Integer(1));
		testOneConversion("42395", Integer.class, new Integer(42395));
		testOneConversion("1.0", Float.class, new Float(1));
		testOneConversion("1324.2452", Float.class, new Float(1324.2452));
		
		testOneConversion(new Integer(1), String.class, "1");
		testOneConversion(new Integer(984567), String.class, "984567");

		//To/From Stencil tree types
		testOneConversion(stencil.parser.tree.Atom.Literal.instance(3), Integer.class, new Integer(3));
		testOneConversion(stencil.parser.tree.Atom.Literal.instance("test"), String.class, "test");
		testOneConversion(stencil.parser.tree.Atom.Literal.instance("3"), Integer.class, new Integer(3));
		
		
		//Sigils
		testOneConversion("@color(12,34,56,7)", java.awt.Color.class, new java.awt.Color(12,34,56,7));
		testOneConversion("@color(12,34,56)", java.awt.Color.class, new java.awt.Color(12,34,56));
		testOneConversion("@color(RED)", java.awt.Color.class, java.awt.Color.red);
	}
	
	private void testOneConversion(Object source, Class target, Object reference) {
		Object result = Converter.convert(source, target);
		assertTrue(String.format("Class did not match after conversion (expected %1$s, recieved %2$s).", target.getName(), result.getClass().getName()),
						target.isAssignableFrom(result.getClass()));
		
		assertEquals("Mismatch after conversion.", result, reference);	
	}

	public void testToString() {
		Tuple reference = new BasicTuple(TestBasicTuple.source, TestBasicTuple.names, TestBasicTuple.values);
		String value = Tuples.toString(reference);
		Assert.assertTrue(value.contains("String"));
		Assert.assertTrue(value.contains("StringField"));
		Assert.assertTrue(value.contains("2"));
		Assert.assertTrue(value.contains("IntegerField"));
		Assert.assertTrue(value.contains("3.14"));
		Assert.assertTrue(value.contains("DoubleField"));
		Assert.assertTrue(value.contains("Field4"));
		Assert.assertTrue(value.contains("Value4"));
	}

}
