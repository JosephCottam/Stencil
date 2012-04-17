package stencil.unittests;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import stencil.types.Converter;
import stencil.util.collections.PropertyUtils;

public class TestConfigure extends StencilTestCase {
	private Properties props = new Properties();

	@Override
	public void setUp() throws Exception {
		super.setUp();
		props.loadFromXML(new FileInputStream(DEFAULT_PROPERTIES_FILE));
	}
	
	public void testWrapperRegister() {
		Collection wrappers = new HashSet(Converter.getWrappers().values());
		Collection expected = PropertyUtils.filter(props, Converter.WRAPPER_KEY);
		assertTrue("No wrappers expected.", expected.size() >1);
		assertEquals("Wrapper count not as expected.", expected.size(), wrappers.size());
	}

}
