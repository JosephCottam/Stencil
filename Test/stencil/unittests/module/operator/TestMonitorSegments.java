package stencil.unittests.module.operator;

import junit.framework.TestCase;
import stencil.interpreter.guide.SampleSeed;
import stencil.modules.stencilUtil.MonitorSegments;
import stencil.modules.stencilUtil.MonitorSegments.Segment;

public class TestMonitorSegments extends TestCase {
	private static int[] count = new int[]{1,2,3,4,5,6,7,8,9,10,11,12};
	private static int[] evens = new int[]{2,4,6,8,10,12,14,16,18,20};
	private static int[] quads = new int[]{4,8,12,16,20,24,28,32,36,40};

	private static double[] doubles = new double[]{1,1.5,2,2.5,3,3.5,4,4.5};
	private static int[] mixed = new int[]{1,2,3,4,10,11,12,13,14,15,16,17,18,25,30,31,32};
	
	public void testSegments() {
		MonitorSegments gapOp = new MonitorSegments(null, 0);
		for (int v: count) {gapOp.map(v);}
		SampleSeed<Segment> seed = gapOp.getSeed();
		for (int i=0;i< seed.size(); i++) {
			Segment s = seed.get(i);
			assertEquals("Incorrect range start.", (double) count[i], s.start);
			assertEquals("Incorrect range end.", (double) count[i], s.end);
		}

		gapOp = new MonitorSegments(null, 1);
		for (int v: count) {gapOp.map(v);}
		seed = gapOp.getSeed();
		assertEquals(1, seed.size());
		assertEquals("Incorrect range start.", (double) count[0], seed.get(0).start);
		assertEquals("Incorrect range end.", (double) count[count.length-1], seed.get(0).end);


		gapOp = new MonitorSegments(null, 2);
		for (int v: mixed) {gapOp.map(v);}
		seed = gapOp.getSeed();
		assertEquals(4, seed.size());

		assertEquals("Incorrect range start.", 1d, seed.get(0).start);
		assertEquals("Incorrect range end.", 4d, seed.get(0).end);
		assertEquals("Incorrect range start.", 10d, seed.get(1).start);
		assertEquals("Incorrect range end.", 18d, seed.get(1).end);
		assertEquals("Incorrect range start.", 25d, seed.get(2).start);
		assertEquals("Incorrect range end.", 25d, seed.get(2).end);
		assertEquals("Incorrect range start.", 30d, seed.get(3).start);
		assertEquals("Incorrect range end.", 32d, seed.get(3).end);

		
	}
	
	public void testMarginZero() {
		MonitorSegments gapOp = new MonitorSegments(null, 0);
		for (int v: evens) {gapOp.map(v);}
		assertEquals("Incorrect sub-range: " + gapOp.getSeed(), evens.length, gapOp.getSeed().size());
		
		gapOp = new MonitorSegments(null, 0);
		for (int v: count) {gapOp.map(v);}
		assertEquals("Incorrect sub-range: " + gapOp.getSeed(), count.length, gapOp.getSeed().size());
		
		gapOp = new MonitorSegments(null, 0);
		for (double v: doubles) {gapOp.map(v);}
		assertEquals("Incorrect sub-range: " + gapOp.getSeed(), doubles.length, gapOp.getSeed().size());
	}
	

	public void testMarginOne() {
		MonitorSegments gapOp = new MonitorSegments(null, 1);
		for (int v: evens) {gapOp.map(v);}
		assertEquals("Incorrect sub-range: " + gapOp.getSeed(), evens.length, gapOp.getSeed().size());
		
		gapOp = new MonitorSegments(null, 1);
		for (int v: count) {gapOp.map(v);}
		assertEquals("Incorrect sub-range: " + gapOp.getSeed(), 1, gapOp.getSeed().size());
		
		gapOp = new MonitorSegments(null, 1);
		for (double v: doubles) {gapOp.map(v);}
		assertEquals("Incorrect sub-range: " + gapOp.getSeed(), 1, gapOp.getSeed().size());
	}

	public void testMarginFive() {
		MonitorSegments gapOp = new MonitorSegments(null, 5);
		for (int v: evens) {gapOp.map(v);}
		assertEquals("Incorrect sub-range: " + gapOp.getSeed(), 1, gapOp.getSeed().size());
		
		gapOp = new MonitorSegments(null, 5);
		for (int v: quads) {gapOp.map(v);}
		assertEquals("Incorrect sub-range: " + gapOp.getSeed(), 1, gapOp.getSeed().size());
		
		gapOp = new MonitorSegments(null, 5);
		for (double v: mixed) {gapOp.map(v);}
		assertEquals("Incorrect sub-ranges:" + gapOp.getSeed(), 3, gapOp.getSeed().size());
	}

	
	public void testMerging() {
		int[] unordered = new int[]{1,2,20,21,10,11,15,5,6,4,3};
		
		MonitorSegments gapOp = new MonitorSegments(null, 1);
		for (int v: unordered) {gapOp.map(v);}
		assertEquals("Incorrect sub-ranges: " + gapOp.getSeed(), 4, gapOp.getSeed().size());
	}
	
}
