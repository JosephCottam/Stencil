package stencil.unittests.operator;

import stencil.operator.module.ModuleCache;
import stencil.operator.wrappers.EncapsulationGenerator;
import stencil.operator.wrappers.JythonEncapsulation;
import stencil.parser.tree.Python;
import stencil.tuple.Tuple;
import junit.framework.TestCase;
import static stencil.parser.ParserConstants.MAIN_BLOCK_TAG;
import static stencil.parser.ParserConstants.INIT_BLOCK_TAG;
import static stencil.unittests.operator.TestEncapsulationGenerator.getPython;

import java.util.Arrays;
import java.util.List;

public class TestJythonEncapsulation extends TestCase {
	
	private EncapsulationGenerator g;
	ModuleCache modules = new ModuleCache();
	
	public void setUp() throws Exception {
		g = new EncapsulationGenerator();	
	}
	
	public void testNumericValue() throws Exception {
		List<String> arguments = Arrays.asList(new String[]{"value"});
		List<String> results = Arrays.asList(new String[]{"rv"});
		
		JythonEncapsulation e;
		Tuple t;
		
		Python p = getPython("test1", MAIN_BLOCK_TAG, arguments, results, "rv = 1");
		e = new JythonEncapsulation(p, p.getFacets().get(0), g);
		assertNotNull("Encapsulation name cannot be null", e.getName());
		t = e.invoke(100);
		assertEquals(1, t.get(results.get(0)));
	}
		
	public void testStringValue() throws Exception {
		List<String> arguments = Arrays.asList(new String[]{"value"});
		List<String> results = Arrays.asList(new String[]{"rv"});
		
		JythonEncapsulation e;
		Tuple t;

		Python p = getPython("test1", "tag", arguments, results, "rv = '1'");
		e = new JythonEncapsulation(p,p.getFacets().get(0), g);
		assertNotNull("Encapsulation name cannot be null", e.getName());
		t = e.invoke(100);
		assertEquals("1", t.get(results.get(0)));
		assertFalse(t.get(results.get(0)).equals(1));
	}
	
	public void testPassThroughValue() throws Exception {
		List<String> arguments = Arrays.asList(new String[]{"value"});
		List<String> results = Arrays.asList(new String[]{"rv"});
		
		JythonEncapsulation e;
		Tuple t;

		Python p = getPython("test1", MAIN_BLOCK_TAG, arguments, results,"rv = value");
		e = new JythonEncapsulation(p,p.getFacets().get(0), g);
		t = e.invoke(100);
		assertEquals(100, t.get(results.get(0)));
		assertTrue(t.get(results.get(0)).equals(100));
	}

	public void testMultiValue() throws Exception {
		List<String> arguments = Arrays.asList(new String[]{"value1", "value2", "value3"});
		List<String> results = Arrays.asList(new String[]{"rv1","rv2"});
		JythonEncapsulation e;
		Tuple t;

		Python p = getPython("test1", MAIN_BLOCK_TAG,  arguments, results, "rv1 = value1 + value2\nrv2=value2+value3");
		e = new JythonEncapsulation(p,p.getFacets().get(0), g);

		assertNotNull("Encapsulation name cannot be null", e.getName());
		t = e.invoke(1,2,3);
		assertEquals(3, t.get(results.get(0)));
		assertEquals(5, t.get(results.get(1)));
	}

	public void testColor() throws Exception {
		List<String> arguments = Arrays.asList(new String[]{"value"});
		List<String> results = Arrays.asList(new String[]{"rv"});
		
		JythonEncapsulation e;
		Tuple t;

		Python p = getPython("test1", INIT_BLOCK_TAG, null, null, "from java import awt");
		g.generate(p, modules.getAdHoc());
		
		
		p = getPython("test1", MAIN_BLOCK_TAG,  arguments, results,"rv = awt.Color(value,value,value)");
		e = new JythonEncapsulation(p,p.getFacets().get(0), g);
		t = e.invoke(100);
		assertEquals(java.awt.Color.class, t.get(results.get(0)).getClass());
		assertTrue(new java.awt.Color(100,100,100).equals(t.get(results.get(0))));
	}
	
	public void testInitBlockImport() throws Exception {
		List<String> arguments = Arrays.asList(new String[]{"value"});
		List<String> results = Arrays.asList(new String[]{"rv"});
		Tuple t;

		Python p1 = getPython("test1", INIT_BLOCK_TAG, null, null, "from java.lang import Integer");
		g.generate(p1, modules.getAdHoc());
		
		Python p2 = getPython("test1", MAIN_BLOCK_TAG, arguments, results, "rv = Integer(value)");
		JythonEncapsulation e2 = new JythonEncapsulation(p2,p2.getFacets().get(0), g);
		
		assertSame("Environments not equal when expected", g.getEnvironment(p1.getEnvironment()), g.getEnvironment(p2.getEnvironment()));
		
		t = e2.invoke(100);
		assertEquals(Integer.class, t.get(results.get(0)).getClass());
		assertEquals(100, t.get(results.get(0)));
		assertTrue(t.get(results.get(0)).equals(100));
	}
}
