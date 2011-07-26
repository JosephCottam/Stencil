package stencil.unittests.util.streams;

import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.util.streams.txt.*;
import junit.framework.TestCase;
import junit.framework.Assert;

import static stencil.unittests.util.streams.Util.*;


public class TestDelimitParser extends TestCase {
	public void testOpen() throws Exception{
		DelimitedParser p = coordHeaderStream();
		Assert.assertTrue("Opened, but no hasNext", p.hasNext());
		SourcedTuple root = p.next();
		Tuple t = root.getValues();
		Assert.assertTrue("First tuple not as expected after open.", 
				t.get(0).equals("\"collective\"") 
				&& t.get(1).equals("95.852867") 
				&& t.get(2).equals("67.091820"));

		
		p = coordStream();
		Assert.assertTrue("Opened, but no hasNext", p.hasNext());

		root = p.next();
		t = root.getValues();
		Assert.assertFalse("First tuple same as header after open with header.", 
							t.get(0).equals("ID") 
							&& t.get(1).equals("X") 
							&& t.get(2).equals("Y"));
	}
	
	
	public void testHasNext() throws Exception {
		DelimitedParser p = coordStream();
		Assert.assertTrue("HasNext false after creation", p.hasNext());
		
		p.close();
		Assert.assertFalse("Hasnext true after close.", p.hasNext());
		
		int i=0;
		while (p.hasNext() && i< 1000) {p.next(); i++;}
		Assert.assertFalse("HasNext true after anticipated exhaustive iteration.", p.hasNext());
	}
	
	public void testNext() throws Exception {
		DelimitedParser p = coordHeaderStream();
		
		int i=0;
		while (p.hasNext() && i< 1000) {p.next(); i++;}
		Assert.assertTrue("Next iteration count exceeded.", i<1000);
		Assert.assertTrue("Next iteration count insufficient.", i>10);
	}
	
	public void testClose() throws Exception {
		DelimitedParser p = coordStream();
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
		final long max = 2500; //2.5 second
		
		DelimitedParser p = trovesStream();		
		final long start = System.currentTimeMillis();
		
		long fields=0;
		while (p.hasNext()) {
			Tuple t = p.next();
			if (t!=null) {fields = t.size() + fields;}
		}
		final long end = System.currentTimeMillis();		
		final long elapse = end-start;

		Assert.assertTrue("Insufficient fields read.", fields > 1250000);//Diagnostic
		Assert.assertTrue(String.format("Load time of %1$s ms exceed permitted max of %2$s ms", elapse, max), max > elapse);
	}
}
