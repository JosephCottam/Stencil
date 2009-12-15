package stencil.unittests.tuple;

import java.util.HashMap;

import stencil.tuple.MapTuple;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.MutableTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.unittests.util.TestBasicTuple;
import junit.framework.Assert;
import junit.framework.TestCase;


public class TestTuples extends TestCase {
	public void testTransfer() throws Exception {
		Tuple reference = new PrototypedTuple(TestBasicTuple.source, TestBasicTuple.names, TestBasicTuple.types, TestBasicTuple.values);
		HashMap map = new HashMap();
		map.put("One", "One");
		map.put("Two", 2);
		map.put("Three", 3.0d);
		map.put("Four", "four");
		map.put("Five", "FIVE");
		MutableTuple target = new MapTuple(map);
		
		Tuples.transfer(reference, target);

		for (String field: TuplePrototypes.getNames(reference.getPrototype())) {
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
		Tuple reference = new PrototypedTuple(TestBasicTuple.source, TestBasicTuple.names, TestBasicTuple.types, TestBasicTuple.values);
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
	
	public void testMerge() {
		Tuple t1 = PrototypedTuple.singleton("V1", 1);
		Tuple t2 = PrototypedTuple.singleton("V2", 2);
		Tuple t3 = PrototypedTuple.singleton("V3", 3);
		Tuple t4 = PrototypedTuple.singleton("V4", 4);
		
		assertSame(Tuples.merge(t1,t1), t1);
		assertSame(Tuples.merge(t1,null), t1);
		assertSame(Tuples.merge(null, null), t1);
		
		boolean failed=false;
		try {Tuples.merge(null, null);}
		catch (Exception e) {failed=true;}
		finally {assertTrue("Failure on null/null merge not encountered.", failed);}
		
		
		Tuple m1 = Tuples.merge(t1, t2);
		assertEquals("Unexpected size after merge", 2, m1.size());
		assertEquals(1, m1.get("V1"));
		assertEquals(2, m1.get("V2"));
		
		Tuple m2 = Tuples.merge(m1, t3);
		assertEquals("Unexpected size after merge", 3, m2.size());
		assertEquals(1, m2.get("V1"));
		assertEquals(2, m2.get("V2"));
		assertEquals(3, m2.get("V3"));
		
		Tuple m3 = Tuples.merge(m2, t4);
		assertEquals("Unexpected size after merge", 4, m3.size());
		assertEquals(1, m3.get("V1"));
		assertEquals(2, m3.get("V2"));
		assertEquals(3, m3.get("V3"));
		assertEquals(4, m3.get("V4"));
		
		Tuple m4 = Tuples.merge(m2, m3);
		assertEquals("Unexpected size after merge", 4, m4.size());
		assertEquals(1, m4.get("V1"));
		assertEquals(2, m4.get("V2"));
		assertEquals(3, m4.get("V3"));
		assertEquals(4, m4.get("V4"));		
	}

}
