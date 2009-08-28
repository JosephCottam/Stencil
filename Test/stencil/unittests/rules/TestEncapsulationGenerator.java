package stencil.unittests.rules;

import stencil.parser.tree.Program;
import stencil.parser.string.ParseStencil;
import stencil.rules.EncapsulationGenerator;
import stencil.rules.ModuleCache;
import stencil.testUtilities.StringUtils;
import stencil.legend.wrappers.JythonLegend;
import stencil.legend.DynamicStencilLegend;
import junit.framework.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stencil.parser.tree.Python;
import stencil.parser.tree.Facet;


public class TestEncapsulationGenerator extends TestCase {

	public void setUp() throws Exception {
		TestModuleCache.initCache();
	}
	
	public void testGenerator() throws Exception {
		EncapsulationGenerator g = new EncapsulationGenerator(); 
		boolean foundStencils = false;
		List<String> errors = new ArrayList();
		
		Map<String, String> programs = StringUtils.allPrograms(StringUtils.STENCIL_CACHE_DIRECTORY);
		
		for (String name: programs.keySet()) {
			System.out.println("Reading " + name);
			String source = programs.get(name);
			foundStencils = true;
			
			Program program;
			try {program  = ParseStencil.testParse(source);}
			catch (Exception e) {errors.add(name); continue;}
			
			ModuleCache modules = new ModuleCache();
			
			Set<DynamicStencilLegend> legends = new HashSet<DynamicStencilLegend>();
			for (Python p: program.getPython()) {
				DynamicStencilLegend l = g.generate(p, modules.getAdHoc());
				legends.add(l);
			}				
			assertEquals("Incorrect number of encapsulations generated.", program.getPython().size(),legends.size());
			
			for (Python p: program.getPython()) {
				assertNotNull("Defined environment not found: " + p.getEnvironment(), g.getEnvironment(p.getEnvironment()));
				assertNotNull("Defined python group not found: " + p.getName(), findLegend(p.getName(), legends));
			}
			
			for (Python p: program.getPython()) {
				JythonLegend legend = findLegend(p.getName(), legends);
				
				for (Facet b: p.getFacets()) {
					assertNotNull("Could not find declared python block", legend.getFacet(b.getName()));
				}
			}
		}

		assertEquals("Errors found generating for file(s).", "[]", java.util.Arrays.deepToString(errors.toArray()));
		assertTrue("No stencils found to parase.", foundStencils);
	}
	
	private JythonLegend findLegend(String name, Set<DynamicStencilLegend> legends) {
		for (DynamicStencilLegend l: legends) {
			if (l.getName().equals(name)) {return (JythonLegend) l;}
		}
		return null;
	}
}
