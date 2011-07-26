package stencil.unittests.module.operator;

import junit.framework.TestCase;
import stencil.module.Module;
import stencil.module.ModuleCache;
import stencil.module.operator.StencilOperator;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;
import stencil.unittests.module.TestModuleCache;

public class TestModules extends TestCase {
	public void setUp() throws Exception {
		TestModuleCache.initCache();
	}
	
	public void testInstance() throws Exception {
		testOneInstance(new MultiPartName("ColorUtils", "SetAlpha"));
		testOneInstance(new MultiPartName("Numerics", "Add"));
	}
	
	
	private void testOneInstance(MultiPartName n) throws Exception {
		ModuleCache mc = new ModuleCache();
		Module m = mc.getModule(n.prefix());
		assertNotNull("Module not found " + n.prefix(), m);
		
		Specializer s = m.getModuleData().getOperator(n.name()).getDefaultSpecializer();
		assertNotNull(s);
		
		StencilOperator l = m.instance(n.name(), null, s);		
		assertNotNull(l);
		
	}
	
}
