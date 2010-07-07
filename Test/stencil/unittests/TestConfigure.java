package stencil.unittests;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import junit.framework.TestCase;
import stencil.types.Converter;
import stencil.util.collections.PropertyUtils;

public class TestConfigure extends TestCase {
	public static String propertiesFile = "./TestData/Stencil.properties";
	
	private Properties props;
	
	public void setUp() throws Exception {
		stencil.Configure.loadProperties(propertiesFile);		
		props.loadFromXML(new FileInputStream(propertiesFile));
	}
	
	public void testWrapperRegister() {
		Collection wrappers = new HashSet(Converter.getWrappers().values());
		Collection expected = PropertyUtils.filter(props, Converter.WRAPPER_KEY);
		assertTrue("No wrappers expected.", expected.size() >1);
		assertEquals("Wrapper count not as expected.", expected.size(), wrappers.size());
	}

}
