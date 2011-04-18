package stencil.util.streams.binary;

import java.io.DataInputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;

import stencil.tuple.SourcedTuple;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.QueuedStream;

public class SocketTupleStream implements TupleStream, QueuedStream.Queable {
	/**File channel contents are loaded from**/
	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream stream;
	
	/**Name of the stream**/
	private final String name;
	
	/**Number of value fields per tuple**/
	private int tupleSize;

	///Per-line buffers
	/**Buffer for loading the prefix.*/
	private int[] offsets;
	
	private SourcedTuple lookahead;

	/**Establish a connection on the given port as the 
	 * indicated stream.  Will return with no connection made.  
	 * init must still be called before hasNext or next.
	 */
	public SocketTupleStream(String streamName, ServerSocket serverSocket) throws Exception {
		this.name = streamName;
		this.serverSocket = serverSocket == null ? new ServerSocket(0) : serverSocket;
	}

	
	public ServerSocket socket() {return serverSocket;}
	
	public void init() throws Exception {
		socket = serverSocket.accept();
		stream = new DataInputStream(socket.getInputStream());
		tupleSize = stream.readInt();
		offsets = new int[tupleSize];
	}
	
	@Override
	public boolean hasNext() {
		if (lookahead != null) {return true;}
		cache();
		return lookahead != null;
	}

	@Override
	public SourcedTuple next() {
		SourcedTuple val = lookahead;
		lookahead = null;
		return val;
	}
	
	private void cache() {
		try {
			int dataLength = stream.readInt();
			for (int i=0; i<offsets.length; i++) {offsets[i] = stream.readInt();}
			byte[] bytes = new byte[dataLength];
			stream.readFully(bytes);
			
			lookahead = BinaryTupleStream.thaw(name, bytes, offsets);
		} catch (EOFException e) {
			return;
		} catch (Exception e) {
			try {close();
				throw e;}
			catch (Exception ex) {throw new RuntimeException("Error closing socket", ex);}
		}
	}

	public void close() throws Exception {socket.close();}
	
	@Override
	public void remove() {throw new UnsupportedOperationException();}
}
