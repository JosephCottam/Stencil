package stencil.unittests.adapters.examples;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import static junit.framework.Assert.*;

public class Poverty extends ImageTest {
	public Poverty(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Poverty/",
									  "poverty.stencil",
									  null,
									  null,
									  "poverty.txt",
									  "poverty.png", configs));
	}
	
	public void setUp() throws Exception {
		MemoryMXBean mb = ManagementFactory.getMemoryMXBean();
		MemoryUsage mu = mb.getHeapMemoryUsage();
		assertTrue("Not enough memory allocated (needs at least 512M)", mu.getMax() >= 530907136);
		super.setUp();
	}
}
