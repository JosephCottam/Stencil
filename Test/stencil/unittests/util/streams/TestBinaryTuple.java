package stencil.unittests.util.streams;

import java.io.File;

import stencil.tuple.Tuple;
import stencil.util.streams.BinaryTupleStream;
import stencil.util.streams.txt.DelimitedParser;
import junit.framework.TestCase;

public class TestBinaryTuple extends TestCase {
	public static String REGEXP_FILE = "./TestData/RegressionImages/Sourceforge/vx_cluster_0_8.6_min_cuts.coord";
	public static String BIN_TUPLES_FILE = "./TestData/RegressionImages/Sourceforge/vx_cluster_0_8.6_min_cuts.tuples";

	
	public void testEncodeDecode() throws Exception {
		//remove old output (if any)
		File f = new File(BIN_TUPLES_FILE);
		if (f.exists()) {f.delete();}

		//Push old stream through encoder
		DelimitedParser source = new DelimitedParser("CoordFile", "ID X Y", REGEXP_FILE, "\\s+", true,1);
		BinaryTupleStream.Writer writer = new BinaryTupleStream.Writer(source);
		writer.writeStream(BIN_TUPLES_FILE);
		source.close();

		DelimitedParser oldTuples = new DelimitedParser("CoordFile", "ID X Y", REGEXP_FILE, "\\s+", true,1);
		BinaryTupleStream.Reader newTuples = new BinaryTupleStream.Reader("CoordFile", BIN_TUPLES_FILE);
		
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
	
	public void testSpeed() throws Exception {
		File f = new File(BIN_TUPLES_FILE);
		if (!f.exists()) {testEncodeDecode();}

		DelimitedParser reSource = new DelimitedParser("CoordFile", "ID X Y", REGEXP_FILE, "\\s+", true,1);
		long reStart = System.currentTimeMillis();
		long reFields=0;
		while (reSource.hasNext()) {
			Tuple t = reSource.next();
			reFields += t.size();
		}
		long reEnd = System.currentTimeMillis();
		long reTime = reEnd-reStart;
		
		BinaryTupleStream.Reader binSource = new BinaryTupleStream.Reader("CoordFile", BIN_TUPLES_FILE);
		long binStart = System.currentTimeMillis();
		long binFields=0;
		while(binSource.hasNext()) {
			Tuple t = binSource.next();
			binFields += t.size();
		}
		long binEnd = System.currentTimeMillis();
		long binTime = binEnd-binStart;
		
		System.err.printf("\nBinary parsing is %1$f of regexp parsing (%2$d vs %3$d).\n", 100 * (binTime/(double) reTime), binTime, reTime);
		assertEquals(reFields, binFields);
		assertTrue("No advantage to binary.", binTime < reTime);
	}
}
