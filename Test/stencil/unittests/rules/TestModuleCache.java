package stencil.unittests.rules;

import java.io.FileInputStream;
import java.util.Properties;

import junit.framework.TestCase;
import stencil.legend.StencilLegend;
import stencil.rules.ModuleCache;
import stencil.rules.MethodInstanceException;
import stencil.parser.ParserConstants;

public class TestModuleCache extends TestCase {
	

	public void setUp() {ModuleCache.clear();}
	public static void initCache() throws Exception {
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream("./TestData/Stencil.properties"));		
		ModuleCache.registerModules(props);
	}
	
	public void testInit() throws Exception {
		assertEquals("Module cache not empty when expected.", 0, ModuleCache.registeredModules().size());
		
		initCache();		
		assertFalse("Module cache empty when not expected.", 0==ModuleCache.registeredModules().size());
		assertEquals("Current modules: " + ModuleCache.registeredModules().keySet(), 8, ModuleCache.registeredModules().size());
	}

	public void testClear() throws Exception {
		initCache();
		
		ModuleCache m = new ModuleCache();
		StencilLegend l= null;

		try {l=m.instance("NoMethod", ParserConstants.SIMPLE_SPECIALIZER);}
		catch (MethodInstanceException e) {/*Exception expected, tested below.*/}
		assertNull("Method found when not expected.",l);

		try {m.instance("Concatenate", ParserConstants.SIMPLE_SPECIALIZER);}
		catch (MethodInstanceException e) {fail("Method not found when expected.");}
		catch (Exception e) {fail("Unexpected error looking for method.");}
	}
}
