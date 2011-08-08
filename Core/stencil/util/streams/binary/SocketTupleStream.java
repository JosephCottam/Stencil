package stencil.util.streams.binary;

import java.io.DataInputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;

import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.QueuedStream;

//TODO: Use nio instead of DataInputStream, then pass the stream in as the buffer to the binary reader.
public class SocketTupleStream implements TupleStream, QueuedStream.Queable {
	/**File channel contents are loaded from**/
	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream stream;
	
	/**Name of the stream**/
	private final String name;
	
	/**Encoding types for each field**/
	private char[] types;
	
	/**Number of fields in the tuple.**/
	private int tupleSize;
			
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
		types = new char[tupleSize];
		for (int i=0; i<tupleSize; i++) {
			types[i] = (char) stream.readByte();
		}
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
			Object[] values = new Object[tupleSize];
			for (int i=0;i<tupleSize;i++) {
				Object value;
				if (types[i] == 'i') {value = stream.readInt();}
				else if (types[i] == 'd') {value = stream.readDouble();}
				else if (types[i] == 'l') {value = stream.readLong();}
				else {
					int dataLength = stream.readInt();
					byte[] bytes = new byte[dataLength];		
					stream.readFully(bytes);
					value = new String(bytes);
				}
				values[i] = value;
			}
			lookahead = new SourcedTuple.Wrapper(name, new ArrayTuple(values));
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
