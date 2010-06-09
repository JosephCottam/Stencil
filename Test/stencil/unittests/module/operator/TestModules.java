package stencil.unittests.module.operator;

import junit.framework.TestCase;
import stencil.module.Module;
import stencil.module.ModuleCache;
import stencil.module.operator.StencilOperator;
import stencil.parser.tree.Specializer;
import stencil.parser.tree.util.MultiPartName;
import stencil.unittests.module.TestModuleCache;

public class TestModules extends TestCase {
	public void setUp() throws Exception {
		TestModuleCache.initCache();
	}
	
	public void testInstance() throws Exception {
		testOneInstance(new MultiPartName("ColorUtils::SetAlpha.Map"));
		testOneInstance(new MultiPartName("Numerics::Add"));
	}
	
	
	private void testOneInstance(MultiPartName n) throws Exception {
		ModuleCache mc = new ModuleCache();
		Module m = mc.getModule(n.getPrefix());
		assertNotNull("Module not found " + n.getPrefix(), m);
		
		Specializer s = m.getModuleData().getOperator(n.getName()).getDefaultSpecializer();
		assertNotNull(s);
		
		StencilOperator l = m.instance(n.getName(), s);		
		assertNotNull(l);
		
	}
	
}
