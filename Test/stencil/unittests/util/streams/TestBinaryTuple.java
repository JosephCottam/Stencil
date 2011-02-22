package stencil.unittests.util.streams;

import java.io.File;

import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;
import stencil.util.streams.BinaryTupleStream;
import stencil.util.streams.txt.DelimitedParser;
import junit.framework.TestCase;

public class TestBinaryTuple extends TestCase {
	public static final String COORD_FILE = "./TestData/RegressionImages/Sourceforge/vx_cluster_0_8.6_min_cuts.coord";
	public static final String COORD_TUPLES_FILE = "./TestData/RegressionImages/Sourceforge/vx_cluster_0_8.6_min_cuts.tuples";

	public static final String TROVES_FILE = "./TestData/RegressionImages/Sourceforge/project_troves.txt";
	public static final String TROVES_TUPLES_FILE = "./TestData/RegressionImages/Sourceforge/project_troves.tuples";

	
	public void testEncodeDecode() throws Exception {
		DelimitedParser source = new DelimitedParser("CoordFile", "ID X Y", COORD_FILE, "\\s+", true,1);
		prepBinary(source, COORD_TUPLES_FILE);
		source.close();

		DelimitedParser oldTuples = new DelimitedParser("CoordFile", "ID X Y", COORD_FILE, "\\s+", true,1);
		BinaryTupleStream.Reader newTuples = new BinaryTupleStream.Reader("CoordFile", COORD_TUPLES_FILE);
		
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
		DelimitedParser source = new DelimitedParser("Troves", "ID|ATT", TROVES_FILE, "\\|", true,1);		
		prepBinary(source, TROVES_TUPLES_FILE);
		source.close();
		
		source = new DelimitedParser("Troves", "ID|ATT", TROVES_FILE, "\\|", true,1);
		BinaryTupleStream.Reader binSource = new BinaryTupleStream.Reader("Troves", TROVES_TUPLES_FILE);
		testSpeed(source, binSource);
	}

	public void testCoordSpeed() throws Exception {
		DelimitedParser reSource = new DelimitedParser("CoordFile", "ID X Y", COORD_FILE, "\\s+", true,1);
		prepBinary(reSource, COORD_TUPLES_FILE);
		reSource.close();
		
		reSource = new DelimitedParser("CoordFile", "ID X Y", COORD_FILE, "\\s+", true,1);
		BinaryTupleStream.Reader binSource = new BinaryTupleStream.Reader("CoordFile", COORD_TUPLES_FILE);
		testSpeed(reSource, binSource);
	}
	
	private void testSpeed(DelimitedParser reSource, BinaryTupleStream.Reader binSource){
		long reStart = System.currentTimeMillis();
		long reFields=0;
		while (reSource.hasNext()) {
			Tuple t = reSource.next();
			reFields += t.size();
		}
		long reEnd = System.currentTimeMillis();
		long reTime = reEnd-reStart;
		
		long binStart = System.currentTimeMillis();
		long binFields=0;
		int count=0;
		while(binSource.hasNext()) {
			Tuple t = binSource.next();
			binFields += t.size();
			count++;
		}
		long binEnd = System.currentTimeMillis();
		long binTime = binEnd-binStart;
		
		System.out.printf("\nBinary parsing is %1$f of regexp parsing (%2$d ms vs %3$d ms over %4$s tuples).\n", 100 * (binTime/(double) reTime), binTime, reTime, count);
		assertEquals(reFields, binFields);
		assertTrue("No advantage to binary.", binTime < reTime);
	}
	
	private void prepBinary(TupleStream source, String targetFile) throws Exception {
		//remove old output (if any)
		File f = new File(targetFile);
		if (f.exists()) {f.delete();}

		//Push old stream through encoder
		BinaryTupleStream.Writer writer = new BinaryTupleStream.Writer(source);
		writer.writeStream(targetFile);
	}
}
