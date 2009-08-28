package stencil.unittests.legend;

import junit.framework.TestCase;
import stencil.parser.tree.Specializer;
import stencil.rules.ModuleCache;
import stencil.streams.Tuple;
import stencil.unittests.rules.TestModuleCache;
import stencil.util.MultiPartName;
import stencil.legend.StencilLegend;
import stencil.legend.module.*;
import stencil.legend.module.util.*;

import java.lang.reflect.*;


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
		
		Specializer s = m.getModuleData().getOperatorData(n.getName()).getDefaultSpecializer();
		assertNotNull(s);
		
		StencilLegend l = m.instance(n.getName(), s);		
		assertNotNull(l);
		
	}
	
	
	public void testIsFacet() throws Exception {
		@SuppressWarnings("unused")
		class Test {
			public Tuple t1() {return null;}
			public Object t2() {return null;}
		}
		
		Method m1= Test.class.getMethod("t1");
		Method m2= Test.class.getMethod("t2");
		
		assertTrue(Modules.isFacet(m1));
		assertFalse(Modules.isFacet(m2));
	}
	
	
}
