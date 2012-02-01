package stencil.util.streams.binary;

import java.io.DataInputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;

import stencil.interpreter.tree.Specializer;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Stream;
import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;

//TODO: Use nio instead of DataInputStream, then pass the stream in as the buffer to the binary reader.
@Description("Loads data written by the BinaryTupleStream.Writer through a (server) socket connection.")
@Stream(name="Socket", spec="[socket:0, queue: 50]")	//Socket 0 will bind to any free port.
public class SocketTupleStream implements TupleStream {
	public static final String SOCKET_KEY = "socket";
	
	/**File channel contents are loaded from**/
	private final ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream stream;
	
	/**Name of the stream**/
	private final String name;
	
	/**Encoding types for each field**/
	private char[] types;
	
	/**Number of fields in the tuple.**/
	private int tupleSize;
			
	private SourcedTuple lookahead;

	public SocketTupleStream(String streamName, TuplePrototype proto, Specializer spec) throws Exception {
		this(streamName, new ServerSocket(Converter.toInteger(spec.get(SOCKET_KEY))));
	}

	/**Establish a listener on given port as the indicated stream.
	 * Will actually complete the connection handshake on first call to hasNext or next.  
	 */
	public SocketTupleStream(String streamName, ServerSocket serverSocket) throws Exception {
		this.name = streamName;
		this.serverSocket = serverSocket == null ? new ServerSocket(0) : serverSocket;
	}
	
	public ServerSocket socket() {return serverSocket;}
	
	private void init() {
		try {
			socket = serverSocket.accept();
			stream = new DataInputStream(socket.getInputStream());
			tupleSize = stream.readInt();
			types = new char[tupleSize];
			for (int i=0; i<tupleSize; i++) {
				types[i] = (char) stream.readByte();
			}
		} catch (Exception e) {
			throw new RuntimeException("Error establishing socket connection." ,e);
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
		if (lookahead == null) {cache();}
		SourcedTuple val = lookahead;
		lookahead = null;
		return val;
	}
	
	private void cache() {
		if (socket == null) {init();}
		
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
			try {stop();
				throw e;}
			catch (Exception ex) {throw new RuntimeException("Error closing socket", ex);}
		}
	}

	public void stop() {
		try {socket.close();}
		catch (Exception e) {}
	}
	
	@Override
	public void remove() {throw new UnsupportedOperationException();}
}
