package stencil.unittests.tuple;

import java.util.*;
import junit.framework.TestCase;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.SimpleFieldDef;
import stencil.tuple.prototype.TuplePrototype;
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
	
	public void testAppend() {
		TuplePrototype p1 = new TuplePrototype("One", "Two", "Three");
		TuplePrototype p2 = new TuplePrototype(Renderer.X,Renderer.Y,Renderer.Z);
		
		TuplePrototype p3 = TuplePrototypes.append(p1,p2);
		
		assertEquals("Expected name not found after merge.", "One", p3.get(0).name());
		assertEquals("Expected name not found after merge.", "Two", p3.get(1).name());
		assertEquals("Expected name not found after merge.", "Three", p3.get(2).name());
		assertEquals("Expected name not found after merge.", "X", p3.get(3).name());
		assertEquals("Expected name not found after merge.", "Y", p3.get(4).name());
		assertEquals("Expected name not found after merge.", "Z", p3.get(5).name());
		
		assertEquals(SimpleFieldDef.class, p3.get(0).getClass());
		assertEquals(SchemaFieldDef.class, p3.get(3).getClass());
	
		
	}
}
