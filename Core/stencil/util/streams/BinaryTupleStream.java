package stencil.util.streams;

import stencil.explore.model.Model;
import stencil.explore.model.sources.StreamSource;
import stencil.explore.util.StencilIO;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;
import stencil.tuple.instances.ArrayTuple;
import stencil.types.Converter;

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
		
		public byte[] makeHeader(int tupleSize) {
			assert tupleSize >0;
			
			byte[] size = intBytes(tupleSize);

			byte[] rslt = new byte[INT_BYTES];
			System.arraycopy(size, 0, rslt, 0, size.length);
			return rslt;
		}
		
		private byte[] intBytes(int i ){
			 return ByteBuffer.allocate(INT_BYTES).putInt(i).array();
		}
		
		/**Get a byte array of the next line.
		 * Assumes that the value of Converter.toString(t.get(i)) will be sufficient to recover the data on reload
		 * **/
		public byte[] tupleLine(Tuple t) {
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

		/**Write all of the tuples in the stream to the file.**/
		public void writeStream(String filename) throws Exception {
			FileOutputStream file = new FileOutputStream(filename);
			
			try {
				boolean doHeader = true;
				while(source.hasNext()) {
					Tuple t = source.next().getValues();
					if (doHeader) {
						byte[] header = makeHeader(t.size()); doHeader=false;
						file.write(header);
					}
					byte[] nextline = tupleLine(t);
					file.write(nextline);
				}
			} finally {file.close();}
		}
		
	}
	
	
	/**Stream source that can read a FastStream written by the above included Writer**/
	public static final class Reader implements TupleStream {
		/**File channel contents are loaded from**/
		private FileChannel input;
		private ByteBuffer mainBuffer;
		
		/**Name of the stream**/
		private final String name;
		
		/**Number of value fields per tuple**/
		private final int tupleSize;

		private final long size;
		
		///Per-line buffers
		/**Buffer for loading the prefix.*/
		private final int[] offsets;
		
		public Reader(String streamName, String sourcefile) throws Exception {
			input = new FileInputStream(sourcefile).getChannel();
			size = input.size();
			mainBuffer = input.map(FileChannel.MapMode.READ_ONLY, 0, size);
			
			this.name = streamName;
			tupleSize = mainBuffer.getInt();
			
			offsets = new int[tupleSize];
		}
		
		@Override
		public boolean ready() {return hasNext();}

		@Override
		public boolean hasNext() {
			return input != null
					&& input.isOpen()
					&& mainBuffer.position() < size;
		}


		@Override
		public SourcedTuple next() {
			try {
				int dataLength = mainBuffer.getInt();

				for (int i=0; i< tupleSize; i++) {offsets[i] =mainBuffer.getInt();}
				
				byte[] bytes = new byte[dataLength];
				mainBuffer.get(bytes);
				String base = new String(bytes);
				
				Object[] values = new String[tupleSize];				//Split it up into values
				for (int i=0, prior=0; i< tupleSize; prior=offsets[i++]) {
					values[i] = base.substring(prior, offsets[i]);
				}

				Tuple contents = new ArrayTuple(values);
				SourcedTuple sourced = new SourcedTuple.Wrapper(name, contents);
				return sourced;
			} catch (Exception e) {
				throw new RuntimeException("Error reading line from file.", e);
			}
		}

		public void close() throws Exception {input.close(); input=null;}
		@Override
		public void remove() {throw new UnsupportedOperationException();}
	
	}
	
	
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
