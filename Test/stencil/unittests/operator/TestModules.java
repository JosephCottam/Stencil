package stencil.unittests.operator;

import junit.framework.TestCase;
import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.operator.module.util.*;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.unittests.operator.module.TestModuleCache;
import stencil.util.MultiPartName;

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
		
		StencilOperator l = m.instance(n.getName(), s);		
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
