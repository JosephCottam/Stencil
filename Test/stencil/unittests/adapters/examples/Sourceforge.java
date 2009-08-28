package stencil.unittests.adapters.examples;

import static junit.framework.Assert.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
 
public class Sourceforge extends ImageTest {
	public Sourceforge(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Sourceforge/",
									  "Sourceforge.stencil",
									  null,
									  null,
									  "Sourceforge.txt",
									  "Sourceforge.png", 
									  configs));
	}
	
	public void setUp() throws Exception {
		MemoryMXBean mb = ManagementFactory.getMemoryMXBean();
		MemoryUsage mu = mb.getHeapMemoryUsage();

		assertTrue("Not enough memory allocated (needs at least 512M)", mu.getMax() >= 530907136);

		super.setUp();
	}
}
