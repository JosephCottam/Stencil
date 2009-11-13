package stencil.unittests.adapters.examples;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.*;

import java.awt.image.BufferedImage;

public abstract class ImageTest {
	private TestRecord record;
	File testPNG;
	File deltaPNG;
	File testTXT;
	
	public ImageTest(TestRecord record) {
		this.record =record;
		
		testPNG = new File(record.getTestPNG());
		deltaPNG = new File(record.getDeltaPNG());
		testTXT = new File(record.getTestTXT());
	}
	
	public void setUp() throws Exception {
		stencil.explore.PropertyManager.exploreConfig = "./TestData/Explore.properties";
		stencil.explore.PropertyManager.stencilConfig = "./TestData/Stencil.properties";
	}

	public void testPNG() throws Exception {
		if (testPNG.exists()) {testPNG.delete();}
		if (deltaPNG.exists()) {deltaPNG.delete();}

		stencil.explore.ui.Batch.batchInvoke(record.getImageCommand());
		
		diffImage(record.getPNG(), record.getTestPNG(), record.getDeltaPNG());
	}
	
	public void testTXT() throws Exception {
		if (testTXT.exists()) {testTXT.delete();}

		stencil.explore.ui.Batch.batchInvoke(record.getTextCommand());
		
		diffString(record.getTXT(), record.getTestTXT(), 0, "Comparing TXT files");
	}
	
	public void diffString(String original, String testResult, int skip, String message) throws Exception {
		File f1 = new File(original);
		File f2 = new File(testResult);

		assertTrue("Original file could not be found: " + original, f1.exists());
		assertTrue("Test result file could not be found: " + testResult, f2.exists());

		BufferedReader file1 = new BufferedReader(new FileReader(f1));
		BufferedReader file2 = new BufferedReader(new FileReader(f2));

		int counter=1;
		while (skip >0) {
			file1.readLine();
			file2.readLine();
			skip--;
			counter++;
		}

		List<String> differences = new ArrayList<String>();
		while (file1.ready() && file2.ready()) {
			String line1 = file1.readLine();
			String line2 = file2.readLine();
			if (!line1.equals(line2)) {differences.add(Integer.toString(counter));}
			counter++;
		}
		if (file1.ready() && !file2.ready()) {differences.add("Original has more lines than test result");}
		if (file2.ready() && !file1.ready()) {differences.add("Test result has more lines than original");}
		
		if (differences.size()>0) {fail("Differences on lines "+ Arrays.deepToString(differences.toArray()));}

	}

	public void diffImage(String original, String testResult, String deltaFile) throws Exception {
		BufferedImage o,r;
		try {o = ImageIO.read(new File(original));} catch (Exception e) {throw new RuntimeException("Error opening reference image.", e);}
		try {r = ImageIO.read(new File(testResult));} catch (Exception e) {throw new RuntimeException("Error opening results image.", e);}

		assertEquals("Widths did not match.", o.getWidth(), r.getWidth());
		assertEquals("Heights did not match.", o.getHeight(), r.getHeight());
		boolean[][] delta = new boolean[r.getWidth()][r.getHeight()];
		long deltaCount = 0;
		for (int x =0; x< o.getWidth(); x++) {
			for (int y=0; y< o.getHeight(); y++) {
				delta[x][y] = (o.getRGB(x, y) != r.getRGB(x, y));
				deltaCount += delta[x][y]?1:0;
			}
		}

		if (deltaCount >0) {
			//Output delta image...
			BufferedImage b = new BufferedImage(o.getWidth(), o.getHeight(), BufferedImage.TYPE_INT_RGB);
			for (int x=0; x<b.getWidth(); x++) {
				for (int y=0; y<b.getHeight(); y++) {
					b.setRGB(x, y, delta[x][y]?java.awt.Color.BLACK.getRGB():java.awt.Color.WHITE.getRGB());
				}
			}
			b.flush();
			System.out.println("Writing to " + deltaFile);
			ImageIO.write(b, "png", new java.io.File(deltaFile));
			
			
			double deltaPercent = deltaCount/((double) o.getWidth()*o.getHeight());
			double tolerance = .03;
			if (deltaPercent > tolerance) {
				fail(String.format("Difference exceed percent tolarance (actual: %1$f.3, toleraance: %2$f).", deltaPercent, tolerance));
			}
		}
	}
 
	
	public TestRecord getTestRecord() {return record;}
}
