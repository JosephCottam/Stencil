package stencil.unittests.module.operator;

import stencil.module.ModuleCache;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.wrappers.EncapsulationGenerator;
import stencil.module.operator.wrappers.JythonEncapsulation;
import stencil.module.operator.wrappers.JythonOperator;
import stencil.parser.ParseStencil;
import stencil.parser.tree.Program;
import stencil.testUtilities.StringUtils;
import stencil.unittests.module.TestModuleCache;
import stencil.adapters.java2D.Adapter;
import static stencil.parser.ParserConstants.INIT_FACET;
import static stencil.parser.ParserConstants.MAP_FACET;
import junit.framework.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.python.util.PythonInterpreter;

import stencil.parser.tree.Python;
import stencil.parser.tree.PythonFacet;


public class TestEncapsulationGenerator extends TestCase {
	
	public static Python getPython(String env, String facet, List<String> arguments, List<String> results, String body) throws Exception {
		final String mainFormat = "python %1$s facet %2$s %3$s {%4$s}"; 
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
		return p.getPythons().get(0);
	}
	
	public void setUp() throws Exception {
		TestModuleCache.initCache();
	}
	
	public void testGenerator() throws Exception {
		EncapsulationGenerator g = new EncapsulationGenerator(); 
		boolean foundStencils = false;
		List<String> errors = new ArrayList();
		
		Map<String, String> programs = StringUtils.allPrograms(StringUtils.STENCIL_CACHE_DIRECTORY);
		
		for (String name: programs.keySet()) {
//			System.out.println("Reading " + name);
			String source = programs.get(name);
			foundStencils = true;
			
			Program program;
			try {program  = ParseStencil.parse(source, stencil.adapters.java2D.Adapter.INSTANCE);}
			catch (Exception e) {errors.add(name); continue;}
			
			ModuleCache modules = new ModuleCache();
			
			Set<StencilOperator> legends = new HashSet<StencilOperator>();
			for (Python p: program.getPythons()) {
				StencilOperator l = g.generate(p, modules.getAdHoc());
				legends.add(l);
			}				
			assertEquals("Incorrect number of encapsulations generated.", program.getPythons().size(),legends.size());
			
			for (Python p: program.getPythons()) {
				assertNotNull("Defined environment not found: " + p.getEnvironment(), g.getEnvironment(p.getEnvironment()));
				assertNotNull("Defined python group not found: " + p.getName(), findOperator(p.getName(), legends));
			}
			
			for (Python p: program.getPythons()) {
				JythonOperator legend = findOperator(p.getName(), legends);
				
				for (PythonFacet b: p.getFacets()) {
					if (b.getName().equals(INIT_FACET)) {continue;}//Init blocks are not transfered, just executed.
					assertNotNull("Could not find declared python block", legend.getFacet(b.getName()));
				}
			}
		}
		String err = String.format("Errors found generating for %1$s file(s).", errors.size());
		assertEquals(err, "[]", java.util.Arrays.deepToString(errors.toArray()));
		assertTrue("No stencils found to parase.", foundStencils);
	}
	
	public void testInit() throws Exception {
		EncapsulationGenerator g = new EncapsulationGenerator(); 
		ModuleCache modules = new ModuleCache();
		
		List<String> arguments = Arrays.asList(new String[]{"value"});
		List<String> results = Arrays.asList(new String[]{"rv"});
		
		Python p1 = getPython("test1", INIT_FACET, null, null, "sv=500");
		g.generate(p1, modules.getAdHoc());

		assertNotNull("Expected value not found after init block creation.", g.getEnvironment(p1.getEnvironment()).get("sv"));
		assertEquals("Invalid value after init declared", 500, g.getEnvironment(p1.getEnvironment()).get("sv").__tojava__(Object.class));


		Python p2 = getPython("test1", MAP_FACET,  arguments, results,"rv=value");
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
	
	private JythonOperator findOperator(String name, Set<StencilOperator> operators) {
		for (StencilOperator op: operators) {
			if (op.getName().equals(name)) {return (JythonOperator) op;}
		}
		return null;
	}
}
