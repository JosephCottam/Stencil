package stencil.unittests.util.streams;

import stencil.tuple.Tuple;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;
import stencil.unittests.StencilTestCase;
import stencil.util.streams.binary.SocketTupleSender;
import stencil.util.streams.binary.SocketTupleStream;
import stencil.util.streams.txt.DelimitedParser;

import static stencil.unittests.util.streams.Util.*;

public class TestSocketStream extends StencilTestCase {
	@SuppressWarnings("deprecation")
	public void testEncodeDecode() throws Exception {
		DelimitedParser oldTuples = coordStream();
		SocketTupleStream newTuples = new SocketTupleStream(oldTuples.getName(), null);
		
		TupleStream stream = coordStream();
		SocketTupleSender sender = new SocketTupleSender(stream, COORD_TYPES.toCharArray(), newTuples.socket().getLocalSocketAddress());
		Thread senderThread = new Thread(sender);
		senderThread.start();
		newTuples.init();
		
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
		
		
		
		assertFalse("New tuples not exhausted.", newTuples.hasNext());
		assertFalse("Old tuples not exhausted.", oldTuples.hasNext() && oldTuples.next() != null);

		oldTuples.close();
		newTuples.close();
		
		senderThread.stop();
	}
}
