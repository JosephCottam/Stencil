package stencil.unittests.util.streams;

import java.io.File;

import stencil.tuple.Tuple;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.BinaryTupleStream;
import stencil.util.streams.QueuedStream;
import stencil.util.streams.txt.DelimitedParser;
import junit.framework.TestCase;

import static stencil.unittests.util.streams.Util.*;

public class TestBinaryTuple extends TestCase {
	
	public void testEncodeDecode() throws Exception {
		DelimitedParser source = coordStream();
		prepBinary(source, COORD_TUPLES_FILE);
		source.close();

		DelimitedParser oldTuples = coordStream();
		BinaryTupleStream.Reader newTuples = new BinaryTupleStream.Reader(oldTuples.getName(), COORD_TUPLES_FILE);
		
		//Walk tuple-by-tuple through both streams, all tuples should be equal
		int i=0;
		while(oldTuples.hasNext() && newTuples.hasNext()) {
			try {
				Tuple oldT = oldTuples.next().getValues();
				Tuple newT = newTuples.next().getValues();
				
				assertEquals("Unequal size on tuple " +i, oldT.size(), newT.size());
				for (int field=0; field<oldT.size(); field++) {
					assertEquals("Uneqaul value on field " + field + " of tuple " + i, oldT.get(field), newT.get(field));
				}			
				i++;
			} catch (Exception e) {throw new Exception("Error examining tuple " + i, e);}
		}
		
		assertFalse("Old tuples not exhausted.", oldTuples.hasNext());
		assertFalse("New tuples not exhausted.", newTuples.hasNext());

		oldTuples.close();
		newTuples.close();
	}

	
	public void testTrovesSpeed() throws Exception {
		DelimitedParser source = trovesStream();		
		prepBinary(source, TROVES_TUPLES_FILE);
		source.close();		
		source = trovesStream();
		BinaryTupleStream.Reader binSource = new BinaryTupleStream.Reader(source.getName(), TROVES_TUPLES_FILE);
		testSpeed("trove unqueued", source, binSource);
	}

	public void testTrovesSpeedQueued() throws Exception {
		final int QUEUE_SIZE = 300;
		
		DelimitedParser source = trovesStream();		
		prepBinary(source, TROVES_TUPLES_FILE);
		source.close();
		
		TupleStream re =  new QueuedStream(trovesStream(), QUEUE_SIZE);
		TupleStream bin = new QueuedStream(new BinaryTupleStream.Reader(source.getName(), TROVES_TUPLES_FILE), QUEUE_SIZE);
		testSpeed("trove queued", re, bin);
	}

	
	public void testCoordSpeed() throws Exception {
		DelimitedParser reSource = coordStream();
		prepBinary(reSource, COORD_TUPLES_FILE);
		reSource.close();
		
		reSource = coordStream();
		BinaryTupleStream.Reader binSource = new BinaryTupleStream.Reader(reSource.getName(), COORD_TUPLES_FILE);
		testSpeed("coord", reSource, binSource);
	}
	
	private void testSpeed(String tag, TupleStream reSource, TupleStream binSource){
		long reStart = System.currentTimeMillis();
		int reTuples=0;
		while (reSource.hasNext()) {
			reSource.next();
			reTuples++;
		}
		long reEnd = System.currentTimeMillis();
		long reTime = reEnd-reStart;
		
		long binStart = System.currentTimeMillis();
		int binTuples=0;
		while(binSource.hasNext()) {
			binSource.next();
			binTuples++;
		}
		long binEnd = System.currentTimeMillis();
		long binTime = binEnd-binStart;
		
		System.out.printf("\n%1$s: Binary parsing is %2$f of regexp parsing (%3$d ms vs %4$d ms over %5$s tuples).\n", tag, 100 * (binTime/(double) reTime), binTime, reTime, reTuples);
		assertEquals("Unequal tuple count.", reTuples, binTuples);
		assertTrue("No advantage to binary.", binTime < reTime);
	}
	
	public static void prepBinary(TupleStream source, String targetFile) throws Exception {
		//remove old output (if any)
		File f = new File(targetFile);
		if (f.exists()) {f.delete();}

		//Push old stream through encoder
		BinaryTupleStream.Writer writer = new BinaryTupleStream.Writer(source);
		writer.writeStream(targetFile);
	}
}
