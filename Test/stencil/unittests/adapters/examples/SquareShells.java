package stencil.unittests.adapters.examples;

import static junit.framework.Assert.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
 
public class SquareShells extends ImageTest {
	public SquareShells(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Misc/",
									  "SquareShells.stencil",
									  null,
									  null,
									  "SquareShells.txt",
									  "SquareShells.png", 
									  configs));
	}
	
	public void setUp() throws Exception {
		MemoryMXBean mb = ManagementFactory.getMemoryMXBean();
		MemoryUsage mu = mb.getHeapMemoryUsage();

		assertTrue("Not enough memory allocated (needs at least 512M)", mu.getMax() >= 530907136);

		super.setUp();
	}
}
