package stencil.unittests.util.streams;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import stencil.tuple.Tuple;
import stencil.util.streams.SocketTupleStream;
import stencil.util.streams.txt.DelimitedParser;
import junit.framework.TestCase;

import static stencil.unittests.util.streams.Util.*;

public class TestSocketStream extends TestCase {
	private static final class BinarySender implements Runnable {
		FileInputStream input;
		Socket output;
		ServerSocket endpoint;

		public BinarySender(String binaryFile, ServerSocket endpoint) throws UnknownHostException, IOException {
			input = new FileInputStream(binaryFile);
			output = new Socket();	
			output.bind(null);
			this.endpoint = endpoint;
		}
		
		public void run() {
			int b = 0;
			try {
				output.connect(endpoint.getLocalSocketAddress());
				while(b >= 0) {	 // -1 is end-of-stream
						b = input.read();
						output.getOutputStream().write(b);
				}
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
		
	public void testEncodeDecode() throws Exception {
		DelimitedParser source = coordStream();
		TestBinaryTuple.prepBinary(source, COORD_TUPLES_FILE);
		source.close();
		
		DelimitedParser oldTuples = coordStream();
		SocketTupleStream newTuples = new SocketTupleStream(oldTuples.getName(), null);
		
		BinarySender sender = new BinarySender(COORD_TUPLES_FILE, newTuples.socket());
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
					assertEquals("Uneqaul value on field " + field + " of tuple " + i, oldT.get(field), newT.get(field));
				}			
				i++;
			} catch (Exception e) {throw new Exception("Error examining tuple " + i, e);}
		}
		
		assertFalse("Old tuples not exhausted.", oldTuples.hasNext());
		assertFalse("New tuples not exhausted.", newTuples.hasNext());

		oldTuples.close();
		newTuples.close();
		
		senderThread.stop();
	}
}
