package stencil.unittests.rules;

import org.python.util.PythonInterpreter;

import stencil.adapters.piccoloDynamic.Adapter;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Program;
import stencil.parser.tree.Python;
import stencil.rules.*;
import stencil.streams.Tuple;
import junit.framework.TestCase;
import static stencil.parser.ParserConstants.MAIN_BLOCK_TAG;
import static stencil.parser.ParserConstants.INIT_BLOCK_TAG;

import java.util.Arrays;
import java.util.List;

public class TestJythonEncapsulation extends TestCase {
	
	private EncapsulationGenerator g;
	
	public void setUp() throws Exception {
		g = new EncapsulationGenerator();	
	}

	
	private Python getPython(String env, String facet, List<String> arguments, List<String> results, String body) throws Exception {
		final String mainFormat = "python %1$s facet %2$s %3$s {{%4$s}}"; 
		final String yieldsFormat = "(%1$s) -> (%2$s)";
		
		String yields ="";
		if (arguments != null) {
			StringBuilder args = new StringBuilder();
			for (String a:arguments) {args.append(a); args.append(",");}
			args.deleteCharAt(args.length()-1);
			
			StringBuilder rslts = new StringBuilder();
			for (String a:results) {rslts.append(a); rslts.append(",");}
			rslts.deleteCharAt(rslts.length()-1);
			yields = String.format(yieldsFormat, args, rslts);
		}
		
		String source = String.format(mainFormat, env, facet, yields, body);	
		Program p = ParseStencil.parse(source, Adapter.INSTANCE);
		return p.getPython().get(0);
	}

	
	public void testNumericValue() throws Exception {
		List<String> arguments = Arrays.asList(new String[]{"value"});
		List<String> results = Arrays.asList(new String[]{"rv"});
		
		JythonEncapsulation e;
		Tuple t;
		
		Python p=getPython("test1", MAIN_BLOCK_TAG, arguments, results, "rv = 1");
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
		e = new JythonEncapsulation(p,p.getFacets().get(0), g);
		
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
		new JythonEncapsulation(p1,p1.getFacets().get(0), g);
		
		Python p2 = getPython("test1", MAIN_BLOCK_TAG, arguments, results, "rv = Integer(value)");
		JythonEncapsulation e2 = new JythonEncapsulation(p2,p2.getFacets().get(0), g);
		
		assertSame("Environments not equal when expected", g.getEnvironment(p1.getEnvironment()), g.getEnvironment(p2.getEnvironment()));
		
		t = e2.invoke(100);
		assertEquals(Integer.class, t.get(results.get(0)).getClass());
		assertEquals(100, t.get(results.get(0)));
		assertTrue(t.get(results.get(0)).equals(100));
	}
	
	public void testInit() throws Exception {
		List<String> arguments = Arrays.asList(new String[]{"value"});
		List<String> results = Arrays.asList(new String[]{"rv"});
		

		
		Python p1 = getPython("test1", INIT_BLOCK_TAG, null, null, "sv=500");
		new JythonEncapsulation(p1,p1.getFacets().get(0), g);

		assertNotNull("Expected value not found after init block creation.", g.getEnvironment(p1.getEnvironment()).get("sv"));
		assertEquals("Invalid value after init declared", 500, g.getEnvironment(p1.getEnvironment()).get("sv").__tojava__(Object.class));


		Python p2 = getPython("test1", MAIN_BLOCK_TAG,  arguments, results,"rv=value");
		JythonEncapsulation e2 = new JythonEncapsulation(p2,p2.getFacets().get(0), g);

		PythonInterpreter env1 = g.getEnvironment(p1.getEnvironment());
		PythonInterpreter env2 = g.getEnvironment(p2.getEnvironment());

		assertEquals("Environments not equal when expected", p1.getEnvironment(), p2.getEnvironment());
		assertEquals("Environments not equal when expected", g.getEnvironment(p1.getEnvironment()), g.getEnvironment(p2.getEnvironment()));
		assertEquals("Environments not equal when expected", env2, env2);
		
		assertNotNull(env1.get("sv"));
		assertNotNull(env2.get("sv"));
		assertEquals("Invalid value after Map facet was declared (but not executed).", 500, env1.get("sv").__tojava__(Object.class));
		assertEquals("Invalid value after Map facet was declared (but not executed).", 500, env2.get("sv").__tojava__(Object.class));
		
		e2.invoke(1);
		assertEquals(1, env2.get("rv").__tojava__(Object.class));
		assertEquals("Invalid value after Map facet was declared (but not executed).", 500, env2.get("sv").__tojava__(Object.class));
	}
}
