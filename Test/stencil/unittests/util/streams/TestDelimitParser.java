package stencil.unittests.util.streams;

import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.util.streams.txt.*;
import junit.framework.TestCase;
import junit.framework.Assert;


public class TestDelimitParser extends TestCase {
	public static String coordFile = "./TestData/RegressionImages/Sourceforge/suppliments/clusterCoords_fragment.coord";
	public static String troveFile = "./TestData/RegressionImages/Sourceforge/project_troves.txt";
	public static String header_coordFile = "./TestData/RegressionImages/Sourceforge/suppliments/header_coord.txt";

	public void testOpen() throws Exception{
		DelimitedParser p = new DelimitedParser("CoordFile", "ID X Y", header_coordFile, "\\s+", true,1);
		Assert.assertTrue("Opened, but no hasNext", p.hasNext());
		SourcedTuple root = p.next();
		Tuple t = root.getValues();
		Assert.assertTrue("First tuple not as expected after open.", t.get("ID").equals("\"collective\"") && t.get("X").equals("95.852867") && t.get("Y").equals("67.091820"));

		
		p = new DelimitedParser("CoordFile", "ID X Y", header_coordFile, "\\s+", true,1);
		Assert.assertTrue("Opened, but no hasNext", p.hasNext());

		root = p.next();
		t = root.getValues();
		Assert.assertFalse("First tuple same as header after open with header.", 
							t.get("ID").equals("ID") && t.get("X").equals("X") && t.get("Y").equals("Y"));
	}
	
	
	public void testHasNext() throws Exception {
		DelimitedParser p = new DelimitedParser("CoordFile", "ID X Y", header_coordFile, "\\s+", true, 1);
		Assert.assertTrue("HasNext false after creation", p.hasNext());
		
		p.close();
		Assert.assertFalse("Hasnext true after close.", p.hasNext());
		
		int i=0;
		while (p.hasNext() && i< 1000) {p.next(); i++;}
		Assert.assertFalse("HasNext true after anticipated exhaustive iteration.", p.hasNext());
	}
	
	public void testNext() throws Exception {
		DelimitedParser p = new DelimitedParser("CoordFile", "ID X Y", header_coordFile, "\\s+", true,1);
		
		int i=0;
		while (p.hasNext() && i< 1000) {p.next(); i++;}
		Assert.assertTrue("Next iteration count exceeded.", i<1000);
		Assert.assertTrue("Next iteration count insufficient.", i>10);
	}
	
	public void testClose() throws Exception {
		DelimitedParser p = new DelimitedParser("CoordFile", "ID X Y", header_coordFile, "\\s+", true,1);
		Assert.assertTrue("Stream not ready after open.", p.hasNext());
		p.close();
		Assert.assertFalse("Stream did not close.", p.hasNext());
		
		try {
			p.next();
			Assert.fail("Next succeeded when object was closed.");
		} catch (java.util.NoSuchElementException e) {/*Exception expected.*/}
	}
	
	/**Check the amount of time the loading of a given amount of tuples takes.*/
	public void testTime() throws Exception {
		final long max = 1000; //1 second
		
		DelimitedParser p = new DelimitedParser("Troves", "ID|ATT", troveFile, "\\|", true,1);		
		final long start = System.currentTimeMillis();
		
		long fields=0;
		while (p.hasNext()) {
			Tuple t = p.next();
			fields = t.size() + fields;
		}
		final long end = System.currentTimeMillis();		
		final long elapse = end-start;
		
		System.out.printf("Took %1$d ms\n", elapse);

		Assert.assertTrue("Insufficient fields read.", fields > 1250000);//Diagnostic
		Assert.assertTrue("Load time exceed permitted max of (in milliseconds)" + max, max > elapse);
	}
}
