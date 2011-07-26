package stencil.unittests.util.streams;

import stencil.tuple.Tuple;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.binary.SocketTupleSender;
import stencil.util.streams.binary.SocketTupleStream;
import stencil.util.streams.txt.DelimitedParser;
import junit.framework.TestCase;

import static stencil.unittests.util.streams.Util.*;

public class TestSocketStream extends TestCase {
	//TODO: Add a time limit...as is it can busy-loop the tester indefinitely
	
//	public void testEncodeDecode() throws Exception {
//		DelimitedParser oldTuples = coordStream();
//		SocketTupleStream newTuples = new SocketTupleStream(oldTuples.getName(), null);
//		
//		TupleStream stream = coordStream();
//		SocketTupleSender sender = new SocketTupleSender(stream, newTuples.socket().getLocalSocketAddress());
//		Thread senderThread = new Thread(sender);
//		senderThread.start();
//		newTuples.init();
//		
//		//Walk tuple-by-tuple through both streams, all tuples should be equal
//		int i=0;
//		while(oldTuples.hasNext() && newTuples.hasNext()) {
//			try {
//				Tuple oldT = oldTuples.next().getValues();
//				Tuple newT = newTuples.next().getValues();
//				
//				assertEquals("Unequal size on tuple " +i, oldT.size(), newT.size());
//				for (int field=0; field<oldT.size(); field++) {
//					assertEquals("Uneqaul value on field " + field + " of tuple " + i, oldT.get(field), newT.get(field));
//				}			
//				i++;
//			} catch (Exception e) {throw new Exception("Error examining tuple " + i, e);}
//		}
//		
//		
//		
//		assertFalse("Old tuples not exhausted.", oldTuples.hasNext());
//		assertFalse("New tuples not exhausted.", newTuples.hasNext());
//
//		oldTuples.close();
//		newTuples.close();
//		
//		senderThread.stop();
//	}
}
