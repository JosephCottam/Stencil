package stencil.util.streams.binary;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;
import stencil.util.streams.txt.DelimitedParser;

/**Given a tuple stream, sends that tuple stream across a socket in the binary
 * format.  Main method allows a file to be sent directly.
 * @author jcottam
 *
 */
public class SocketTupleSender implements Runnable {
	final TupleStream source;
	final Socket output;
	final SocketAddress endpoint;
	final char[] types;

	public SocketTupleSender(TupleStream source, char[] types, SocketAddress endpoint) throws UnknownHostException, IOException {
		this.source = source;
		output = new Socket();	
		output.bind(null);
		this.endpoint = endpoint;
		this.types = types;
	}
	
	@Override
	public void run() {
		boolean sendHeader = true;
		try {	
			output.connect(endpoint);
			OutputStream out =output.getOutputStream(); 
			while(source.hasNext()) {
				SourcedTuple sourced= source.next();
				if (sourced == null) {continue;}
				
				Tuple t = sourced.getValues();
				
				byte[] bytes;
				if (sendHeader) {
					bytes = BinaryTupleStream.Writer.makeHeader(types);
					out.write(bytes);
					sendHeader = false;
				}
				bytes = BinaryTupleStream.Writer.asBinary(t, types);
				out.write(bytes);
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws Exception {
		final String endAddr = args[0];
		final String filename = args[1];
		final String format = args[2];
		final char[] types = args[3].toCharArray();
		final TupleStream stream;
		
		final String[] parts = endAddr.split(":");
		
		SocketAddress endpoint = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
		
		if (format.equals("binary")) {
			stream = new BinaryTupleStream.Reader("none", filename);
		} else {
			final String delimiter = args[3];
			final String fields = args[4];
			stream = new DelimitedParser("none", filename, delimiter, Converter.toInteger(fields), true, 0);
		}
		
		SocketTupleSender sender = new SocketTupleSender(stream, types, endpoint);
		sender.run();
		
	}
	
	
}
