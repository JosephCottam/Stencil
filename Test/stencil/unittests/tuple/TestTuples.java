package stencil.unittests.tuple;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.instances.Singleton;
import stencil.tuple.prototype.TupleFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.unittests.StencilTestCase;
import stencil.unittests.util.TestBasicTuple;
import junit.framework.Assert;


public class TestTuples extends StencilTestCase {
	public void testConvert() throws Exception {
		testOneConversion("1", Integer.class, new Integer(1));
		testOneConversion("42395", Integer.class, new Integer(42395));
		testOneConversion("1.0", Float.class, new Float(1));
		testOneConversion("1324.2452", Float.class, new Float(1324.2452));
		
		testOneConversion(new Integer(1), String.class, "1");
		testOneConversion(new Integer(984567), String.class, "984567");

		//To/From Stencil tree types
		testOneConversion(stencil.parser.tree.Const.instance(3), Integer.class, new Integer(3));
		testOneConversion(stencil.parser.tree.Const.instance("test"), String.class, "test");
		testOneConversion(stencil.parser.tree.Const.instance("3"), Integer.class, new Integer(3));
		
		
		//Sigils
		testOneConversion("@Color{12,34,56,7}", java.awt.Color.class, new java.awt.Color(12,34,56,7));
		testOneConversion("@Color{12,34,56}", java.awt.Color.class, new java.awt.Color(12,34,56));
		testOneConversion("@Color{RED}", java.awt.Color.class, java.awt.Color.red);
	}
	
	private void testOneConversion(Object source, Class target, Object reference) {
		Object result = Converter.convert(source, target);
		assertTrue(String.format("Class did not match after conversion (expected %1$s, recieved %2$s).", target.getName(), result.getClass().getName()),
						target.isAssignableFrom(result.getClass()));
		
		assertEquals("Mismatch after conversion.", result, reference);	
	}

	public void testToString() {
		Tuple reference = new PrototypedArrayTuple(TestBasicTuple.names, TestBasicTuple.types, TestBasicTuple.values);
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
	
	public void testReduce() {
		TuplePrototype<TupleFieldDef> proto = new TuplePrototype("one", "two", "three", "four");
		PrototypedTuple<TupleFieldDef> t = new PrototypedArrayTuple(new String[]{"zero", "one", "one-point-five", "two","five"}, new Object[]{0,1,1.5,2,5});
		
		PrototypedTuple<TupleFieldDef> t2 = Tuples.reduce(proto, t);
		assertFalse(Tuples.equals(t, t2));
		for (TupleFieldDef def: t2.prototype()) {
			String name = def.name();
			assertTrue("Found illegal field after reduce: " + name, proto.contains(name));
		}
	}
	
	public void testEquals() {
		Tuple t1 = Singleton.from("V1", 1);
		Tuple t2 = Singleton.from("V1", 1);
		Tuple t3 = Singleton.from("V2", 2);
		Tuple t4 = Singleton.from("V2", 3);
		Tuple t5 = Singleton.from(5);
		Tuple t6 = Singleton.from(1);
		Tuple t7 = new ArrayTuple(new String[]{"one", "two", "three"});
		
		assertTrue(Tuples.equals(t1,t1));
		assertTrue(Tuples.equals(t3,t3));
		assertTrue(Tuples.equals(t1,t2));
		assertTrue(Tuples.equals(t2,t1));
		assertTrue(Tuples.equals(t1,t6));
		assertTrue(Tuples.equals(t6,t1));
		
		assertEquals("Hashcode does not match for 'equal' tuples: t1/t2", t1.hashCode(), t2.hashCode());
		assertEquals("Hashcode does not match for 'equal' tuples: t1/t6", t1.hashCode(), t6.hashCode());

		assertFalse(Tuples.equals(t1,t3));
		assertFalse(Tuples.equals(t3,t4));
		assertFalse(Tuples.equals(t3,t4));
		assertFalse(Tuples.equals(t4,t5));
		assertFalse(Tuples.equals(t1,t5));
		assertFalse(Tuples.equals(t1,t7));
		assertFalse(Tuples.equals(t7,t1));
	}
	
	public void testMerge() {
		PrototypedTuple t1 = Singleton.from("V1", 1);
		PrototypedTuple t2 = Singleton.from("V2", 2);
		PrototypedTuple t3 = Singleton.from("V3", 3);
		PrototypedTuple t4 = Singleton.from("V4", 4);
		
		assertSame(Tuples.merge(t1,t1), t1);
		assertSame(Tuples.merge(t1,null), t1);
		assertSame(Tuples.merge(null, t1), t1);
		
		boolean failed=false;
		try {Tuples.merge(null, null);}
		catch (Exception e) {failed=true;}
		finally {assertTrue("Failure on null/null merge not encountered.", failed);}
		
		
		PrototypedTuple m1 = Tuples.merge(t1, t2);
		assertEquals("Unexpected size after merge", 2, m1.size());
		assertEquals(1, m1.get("V1"));
		assertEquals(2, m1.get("V2"));
		
		PrototypedTuple m2 = Tuples.merge(m1, t3);
		assertEquals("Unexpected size after merge", 3, m2.size());
		assertEquals(1, m2.get("V1"));
		assertEquals(2, m2.get("V2"));
		assertEquals(3, m2.get("V3"));
		
		PrototypedTuple m3 = Tuples.merge(m2, t4);
		assertEquals("Unexpected size after merge", 4, m3.size());
		assertEquals(1, m3.get("V1"));
		assertEquals(2, m3.get("V2"));
		assertEquals(3, m3.get("V3"));
		assertEquals(4, m3.get("V4"));
		
		PrototypedTuple m4 = Tuples.merge(m2, m3);
		assertEquals("Unexpected size after merge", 4, m4.size());
		assertEquals(1, m4.get("V1"));
		assertEquals(2, m4.get("V2"));
		assertEquals(3, m4.get("V3"));
		assertEquals(4, m4.get("V4"));		
		
		
		//Check ordering constraint
		PrototypedTuple m5 = Tuples.mergeAll(t4,t3,t2,t1);
		assertEquals(0, m5.prototype().indexOf("V4"));
		assertEquals(1, m5.prototype().indexOf("V3"));
		assertEquals(2, m5.prototype().indexOf("V2"));
		assertEquals(3, m5.prototype().indexOf("V1"));
		
	}
}
