package stencil.unittests.adapters.examples;

import static junit.framework.Assert.*;

import java.io.File;

import javax.imageio.*;


import java.awt.image.BufferedImage;

public abstract class ImageTest {
	private TestRecord record;
	File testPNG;
	File deltaPNG;
	
	public ImageTest(TestRecord record) {
		this.record =record;
		
		testPNG = new File(record.getTestPNG());
		deltaPNG = new File(record.getDeltaPNG());
	}
	
	public void setUp() throws Exception {
		/*Ensure the configuration*/
		stencil.explore.PropertyManager.exploreConfig = "./TestData/Explore.properties";
		stencil.explore.PropertyManager.stencilConfig = "./TestData/Stencil.properties";
	}

	public static String resultSpace(String testID) {
		String dir = "./testResults/";
		try {dir = System.getProperty("testDir");}
		catch(Exception e) {/*Ignore, keep the default*/}
		dir = dir + File.separator + "images" +  File.separator + testID + File.separator;
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}
		try {
			System.out.println("Returning : " + f.getCanonicalPath());
			return f.getCanonicalPath();}
		catch (Exception e) {throw new Error("Error stetting up test: " + testID, e);}
	}
	
	public void testPNG() throws Exception {
		setUp();
		if (testPNG.exists()) {testPNG.delete();}
		if (deltaPNG.exists()) {deltaPNG.delete();}
		
		String imgCommand = record.getImageCommand();
		System.out.println("Running: " + imgCommand + "\n");
		stencil.explore.ui.Batch.batchInvoke(imgCommand);
		
		diffImage(record.getPNG(), record.getTestPNG(), record.getDeltaPNG());
	}

	public void diffImage(String original, String testResult, String deltaFile) throws Exception {
		BufferedImage o,r;

		File f1 = new File(original);
		File f2 = new File(testResult);
		assertTrue("Original file could not be found: " + original, f1.exists());
		assertTrue("Test result file could not be found: " + testResult, f2.exists());

		
		try {o = ImageIO.read(f1);} catch (Exception e) {throw new RuntimeException("Error opening reference image.", e);}
		try {r = ImageIO.read(f2);} catch (Exception e) {throw new RuntimeException("Error opening results image.", e);}

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
				fail(String.format("Image difference exceed percent tolarance (actual: %1$f3, toleraance: %2$f).", deltaPercent * 100, tolerance*100));
			}
		}
	}
 
	
	public TestRecord getTestRecord() {return record;}

	public static abstract class Probablistc extends ImageTest {
		protected final double permissibleVariance;
		
		/**
		 * @param record
		 * @param permissibleVariance -- How much are sizes permitted to vary (as a percent deviation from original)?
		 */
		public Probablistc(double permissibleVariance, TestRecord record) {
			super (record);
			this.permissibleVariance = permissibleVariance;
		}
		
		public void diffImage(String original, String testResult, String deltaFile) throws Exception {
			BufferedImage o,r;

			File f1 = new File(original);
			File f2 = new File(testResult);
			assertTrue("Original file could not be found: " + original, f1.exists());
			assertTrue("Test result file could not be found: " + testResult, f2.exists());

			
			try {o = ImageIO.read(f1);} catch (Exception e) {throw new RuntimeException("Error opening reference image.", e);}
			try {r = ImageIO.read(f2);} catch (Exception e) {throw new RuntimeException("Error opening results image.", e);}

			double maxWidth = r.getWidth() * (1+permissibleVariance);
			double minWidth = r.getWidth() * (1-permissibleVariance);
			double maxHeight = r.getHeight() * (1+permissibleVariance);
			double minHeight = r.getHeight() * (1-permissibleVariance);

			
			assertTrue("Widths not in tolerance.", o.getWidth() > minWidth && o.getWidth() < maxWidth);
			assertTrue("Heights not in tolerance.", o.getHeight() > minHeight && o.getHeight() < maxHeight);
		}

	}
}
