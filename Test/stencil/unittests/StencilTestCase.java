package stencil.unittests;

import stencil.WorkingDir;
import junit.framework.TestCase;

public abstract class StencilTestCase extends TestCase {
	public static final String DEFAULT_PROPERTIES_FILE = "./TestData/Stencil.properties";

	public void setUp() throws Exception {		
		super.setUp();
		WorkingDir.set(System.getProperty("user.dir"));
		stencil.Configure.loadProperties(DEFAULT_PROPERTIES_FILE);
	}
}
