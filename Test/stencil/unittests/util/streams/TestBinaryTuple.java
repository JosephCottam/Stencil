package stencil.unittests.util.streams;

import java.io.File;

import stencil.tuple.Tuple;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;
import stencil.unittests.StencilTestCase;
import stencil.util.streams.QueuedStream;
import stencil.util.streams.binary.BinaryTupleStream;
import stencil.util.streams.txt.DelimitedParser;

import static stencil.unittests.util.streams.Util.*;

public class TestBinaryTuple extends StencilTestCase {
	

	public void testTrovesSpeedQueued() throws Exception {
		final int QUEUE_SIZE = 300;
		
		DelimitedParser source = trovesStream();		
		prepBinary(source, TROVES_TUPLES_FILE, TROVES_TYPES);
		source.stop();
		
		//QueuedStream.THREAD = false;
		TupleStream re =  new QueuedStream(trovesStream(), QUEUE_SIZE, true);
		TupleStream bin = new QueuedStream(new BinaryTupleStream.Reader(source.getName(), TROVES_TUPLES_FILE), QUEUE_SIZE, true);
		testSpeed("trove queued", re, bin);
	}

	
	public void testEncodeDecode() throws Exception {
		DelimitedParser source = trovesStream();
		prepBinary(source, TROVES_TUPLES_FILE, TROVES_TYPES);
		source.stop();

		DelimitedParser oldTuples = trovesStream();
		BinaryTupleStream.Reader newTuples = new BinaryTupleStream.Reader(oldTuples.getName(), TROVES_TUPLES_FILE);
		
		//Walk tuple-by-tuple through both streams, all tuples should be equal
		int i=0;
		while(oldTuples.hasNext() && newTuples.hasNext()) {
			try {
				Tuple oldT = oldTuples.next().getValues();
				Tuple newT = newTuples.next().getValues();
				
				assertEquals("Unequal size on tuple " +i, oldT.size(), newT.size());
				for (int field=0; field<oldT.size(); field++) {
					assertEquals("Uneqaul value on field " + field + " of tuple " + i, Converter.convert(oldT.get(field), newT.get(field).getClass()), newT.get(field));
				}			
				i++;
			} catch (Exception e) {throw new Exception("Error examining tuple " + i, e);}
		}
		
		assertTrue("Old tuples not exhausted.", !oldTuples.hasNext() || oldTuples.next() == null);
		assertTrue("New tuples not exhausted.", !newTuples.hasNext() || newTuples.next() == null);

		oldTuples.stop();
		newTuples.stop();
	}

	
	public void testTrovesSpeed() throws Exception {
		DelimitedParser source = trovesStream();		
		prepBinary(source, TROVES_TUPLES_FILE, TROVES_TYPES);
		source.stop();		
		source = trovesStream();
		BinaryTupleStream.Reader binSource = new BinaryTupleStream.Reader(source.getName(), TROVES_TUPLES_FILE);
		testSpeed("trove unqueued", source, binSource);
	}
	
	public void testCoordSpeed() throws Exception {
		DelimitedParser reSource = coordStream();
		prepBinary(reSource, COORD_TUPLES_FILE, COORD_TYPES);
		reSource.stop();
		
		reSource = coordStream();
		BinaryTupleStream.Reader binSource = new BinaryTupleStream.Reader(reSource.getName(), COORD_TUPLES_FILE);
		testSpeed("coord", reSource, binSource);
	}
	
	private void testSpeed(String tag, TupleStream reSource, TupleStream binSource){
		long reStart = System.currentTimeMillis();
		int reTuples=0;
		while (reSource.hasNext()) {
			Tuple t = reSource.next();
			if (t!= null) {reTuples++;}
		}
		long reEnd = System.currentTimeMillis();
		long reTime = reEnd-reStart;
		
		long binStart = System.currentTimeMillis();
		int binTuples=0;
		while(binSource.hasNext()) {
			Tuple t = binSource.next();
			if (t!=null) {binTuples++;}
		}
		long binEnd = System.currentTimeMillis();
		long binTime = binEnd-binStart;
		
		assertEquals("Unequal tuple count.", reTuples, binTuples);
		assertTrue("No advantage to binary.", binTime < reTime);
	}
	
	public static void prepBinary(TupleStream source, String targetFile, String types) throws Exception {
		//remove old output (if any)
		File f = new File(targetFile);
		if (f.exists()) {f.delete();}

		//Push old stream through encoder
		BinaryTupleStream.Writer writer = new BinaryTupleStream.Writer(source);
		writer.writeStream(targetFile, types.toCharArray());
	}
}
