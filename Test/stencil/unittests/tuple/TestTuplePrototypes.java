package stencil.unittests.tuple;

import java.util.*;
import junit.framework.TestCase;
import stencil.tuple.prototype.TuplePrototypes;

public class TestTuplePrototypes extends TestCase {
	public void testDefaultNames() {
		int expected = 5;
		String[] names = TuplePrototypes.defaultNames(expected, "Prefix");
		assertEquals("Incorrect number of names created.", expected,names.length);
		
		Set<String> seen = new HashSet();
		for (String name: names) {
			assertFalse("Duplicate name: " + name, seen.contains(name));
			seen.add(name);
		}
	}
	
	public void testDefaultTypes() {
		int expected =13;
		Class[] types = TuplePrototypes.defaultTypes(expected);
		assertEquals("Incorrect number of types created.", types.length, expected);
		for (Class c: types) {assertEquals("Default type not as expected.", Object.class, c);}
	}
}
