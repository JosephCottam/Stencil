package stencil.unittests.operator;

import junit.framework.TestCase;
import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.parser.tree.Specializer;
import stencil.unittests.operator.module.TestModuleCache;
import stencil.util.MultiPartName;

public class TestModules extends TestCase {
	protected Module module;
	protected static final String TEST_MODULE_NAME = "StringUtils";
	
	public void setUp() throws Exception {
		TestModuleCache.initCache();
		module = ModuleCache.registeredModules().get(TEST_MODULE_NAME);
	}
	
	public void testInstance() throws Exception {
		testOneInstance(new MultiPartName("Color::SetAlpha.Map"));
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
