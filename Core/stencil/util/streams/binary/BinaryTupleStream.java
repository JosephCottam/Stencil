package stencil.util.streams.binary;

import stencil.explore.model.Model;
import stencil.explore.model.sources.StreamSource;
import stencil.explore.util.StencilIO;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;
import stencil.util.streams.QueuedStream;

import java.nio.*;
import java.nio.channels.*;
import java.io.*;
import java.util.*;

/**Takes stream source and constructs a file caching the stream values as binary.
 * Loading format is thus:
 *  
 *  File Prefix---
 *  int: # of fields per line (n)
 *  
 *  Each Entry---
 *  int: # of data bytes in entry (does not include this size int and does not include the break points)
 *  n ints: break points in entry for each field, field breaks are character positions in the decoded data
 *  Remaining bytes are entry data (interpreted as strings). 
 **/
public class BinaryTupleStream {
	private static final int INT_BYTES = 4;
	
	/**Means of producing a file that can be read by the Reader**/
	public static final class Writer {
		private final TupleStream source;
		
		public Writer(TupleStream source) {this.source = source;}
		
		public static byte[] makeHeader(Tuple sample) {
			assert sample != null;
			
			byte[] size = intBytes(sample.size());

			byte[] rslt = new byte[INT_BYTES];
			System.arraycopy(size, 0, rslt, 0, size.length);
			return rslt;
		}
		
		private static byte[] intBytes(int i ){
			 return ByteBuffer.allocate(INT_BYTES).putInt(i).array();
		}
		
		/**Get a byte array of the next line.
		 * Assumes that the value of Converter.toString(t.get(i)) will be sufficient to recover the data on reload
		 * **/
		public static byte[] asBinary(Tuple t) {
			final int[] lengths = new int[t.size()];
			ArrayList<Byte> linebytes = new ArrayList();
			int prior =0;
			for (int i=0; i< lengths.length; i++) {
				String v = Converter.toString(t.get(i));
				lengths[i] = v.length()+prior;
				prior = lengths[i];
				byte[] bytes = v.getBytes();				//TODO: Use an explicit encoder
				for (byte b: bytes) {
					linebytes.add(b);
				}
			}

			int total = INT_BYTES + (lengths.length * INT_BYTES) + linebytes.size();
			ByteBuffer buff = ByteBuffer.allocate(total);

			buff.put(intBytes(linebytes.size()));				//Write data length
			for (int len:lengths) {buff.put(intBytes(len));}	//Write splits
			for (byte b: linebytes) {buff.put(b);}				//Write data
			
			return buff.array();
		}

		/**Write all of the tuples in the stream to the file.
		 * @return the number of tuples written
		 * **/
		public void writeStream(String filename) throws Exception {
			FileOutputStream file = new FileOutputStream(filename);
			try {
				boolean doHeader = true;
				while(source.hasNext()) {
					SourcedTuple sourced= source.next();
					if (sourced == null) {continue;}
					Tuple t = sourced.getValues();
					if (doHeader) {
						byte[] header = makeHeader(t); doHeader=false;
						file.write(header);
					}
					byte[] nextline = asBinary(t);
					file.write(nextline);
				}
			} finally {file.close();}
		}
	}
	
	
	/**Stream source that can read a FastStream written by the above included Writer**/
	public static final class Reader implements TupleStream, QueuedStream.Queable {
		/**File channel contents are loaded from**/
		private final ByteChannel input;
		private final ByteBuffer mainBuffer;
		
		/**Name of the stream**/
		private final String name;
		
		/**Number of value fields per tuple**/
		private final int tupleSize;

		///Per-line buffers
		/**Buffer for loading the prefix.*/
		private final int[] offsets;
		
		public Reader(String streamName, String sourcefile) throws Exception {
			FileChannel input = new FileInputStream(sourcefile).getChannel();
			this.input = input;
			long size = input.size();
			mainBuffer = input.map(FileChannel.MapMode.READ_ONLY, 0, size);
			
			this.name = streamName;
			tupleSize = mainBuffer.getInt();
			
			offsets = new int[tupleSize];
		}

		
		@Override
		public boolean hasNext() {
			return input != null
					&& input.isOpen()
					&& mainBuffer.hasRemaining();
		}


		@Override
		public SourcedTuple next() {
			try {
				int dataLength = mainBuffer.getInt();
				for (int i=0; i< offsets.length; i++) {offsets[i] =mainBuffer.getInt();}
				byte[] bytes = new byte[dataLength];
				mainBuffer.get(bytes);

				return thaw(name, bytes, offsets);
			} catch (Exception e) {
				throw new RuntimeException("Error reading line from file.", e);
			}
		}

		public void close() throws Exception {input.close();}
		@Override
		public void remove() {throw new UnsupportedOperationException();}
	
	}
	
	public static final SourcedTuple thaw(String name, byte[] bytes, final int[] offsets) {
		String base = new String(bytes);
		
		Object[] values = new String[offsets.length];				//Split it up into values
		for (int i=0, prior=0; i< offsets.length; prior=offsets[i++]) {
			values[i] = base.substring(prior, offsets[i]);
		}

		Tuple contents = new ArrayTuple(values);
		SourcedTuple sourced = new SourcedTuple.Wrapper(name, contents);
		return sourced;
	}
	
	/**Intentionally left empty, use Reader or Writer instances instead.**/
	private BinaryTupleStream() {}
	
	
	/**Indicate a stencil an a stream, will load up the stream
	 * and create a new file of it that can be loaded as a FastStream.
	 * 
	 * The stream indicated must be finite
	 * TODO: Disentangle from the Explore application Model class
	 * @param args: stencil file, stream to load, file to save in
	 */
	public static void main(String[] args) throws Exception {
		String stencilFile = args[0];
		String targetStream = args[1];
		String targetFile = args[2];
		
		Model model = new Model();
		StencilIO.load(stencilFile, model);
		StreamSource ss = model.getSourcesMap().get(targetStream);
		TupleStream stream = ss.getStream(model);
		
		Writer w = new Writer(stream);
		w.writeStream(targetFile);
	}
}
