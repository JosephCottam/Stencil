package stencil.util.streams.txt;


import java.util.zip.*;
import java.io.*;
import java.util.NoSuchElementException;
import java.util.regex.*;

import stencil.WorkingDir;
import stencil.interpreter.tree.Specializer;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Stream;
import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.InvalidTupleException;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;

import static java.lang.String.format;

/**
 * TupleStream implementation that converts a delimited text file into a tuple
 * stream.  Each line of the file is treated as a tuple.
 * 
 * Also handles standard in --  Use [file:"StdIn"]
 * If you do not set Queue to -1, behavior consumption of all input is not guaranteed.
 * 
 * @author jcottam
 *
 */
@Description("For parsing regular-expression delmited, line-oriented text sources (files and standard in).  Defaults configuration is comma separated with a header.")
@Stream(name="Text", spec="[file: \"\", sep: \"\\\\s*,\\\\s*\", strict: TRUE, skip: 1, queue: 50, trim: false]")
public final class DelimitedParser implements TupleStream {
	public static final String FILE_KEY = "file";
	public static final String SEP_KEY = "sep";
	public static final String STRICT_KEY = "strict";
	public static final String SKIP_KEY = "skip";
	public static final String STD_IN = "StdIn";
	
	/**Reader to get tuples from*/
	private BufferedReader source;

	/**File the reader is reading from*/
	private final String filename;

	/**Name of the stream*/
	private final String name;

	/**Regular expression used to split incoming data.*/
	private final Pattern splitter;

	private final int skip;
	
	private final NextChannel channel;

	/**File streams are only not ready when they are done.*/
	private boolean filestream;
	
	public DelimitedParser(String name, TuplePrototype proto, Specializer spec) throws Exception {
		this(name,
			Converter.toString(spec.get(FILE_KEY)), 
			Converter.toString(spec.get(SEP_KEY)), 
			proto.size(), 
			Converter.toBoolean(spec.get(STRICT_KEY)), 
			Converter.toInteger(spec.get(SKIP_KEY)));
	}

	public DelimitedParser(String name, String filename, String delimiter, int tupleSize, boolean strict, int skip) throws Exception {		
		splitter = Pattern.compile(delimiter);
		
		this.name = name;
		if (STD_IN.equals(filename)) {
			this.filename = filename;
		} else {
			this.filename = WorkingDir.resolve(filename != null ? filename.trim() : null);
		}
		this.skip = skip;
		
		if (strict) {this.channel = new StrictChannel(tupleSize, splitter);}
		else {this.channel = new LooseChannel(tupleSize, splitter);}

		open();
	}
	
	/**Makes the stream ready to be read from.
	 *
	 * Unlike standard java filename resolution, this method will expand '~' to the user's home directory
	 *
	 * @param filename
	 */
	private void open() {
		assert (filename != null && !filename.equals("")) : "Invalid filename supplied.  May not be null.  May not be empty.";
		filestream = true;
		try {
			InputStreamReader isr;
			if (filename.equals(STD_IN)) {
				filestream = false;
				//Input stream reader that WILL NOT close the underlying stream...Standard In
				isr = new InputStreamReader(System.in) {
					@Override
					public void close() {}
				};
			} else if (filename.endsWith("gz")) {
				InputStream is = new GZIPInputStream(new FileInputStream(filename));
				isr = new InputStreamReader(is);				
			} else if (filename.endsWith("zip")) {
				ZipFile zf = new ZipFile(filename);
				ZipEntry ze = zf.entries().nextElement();
				isr = new InputStreamReader(zf.getInputStream(ze));				
			} else {
				isr = new FileReader(filename);
			}
			source = new BufferedReader(isr);
			
			int skip = this.skip;
			try {while (skip-- > 0) {source.readLine();}}
			catch (Exception e) {throw new RuntimeException("Error trying to skip indicated lines.", e);}
			
			channel.validate(source);
		} catch (Exception e) {throw new RuntimeException("Error opening " + filename + ".", e);}
	}
	
	@Override
	public SourcedTuple next() {
		if (source == null) {throw new NoSuchElementException(format("Stream %1$s closed.", name));}
		
		Object[] values;
		try {values = channel.next(source);}
		catch (NoSuchElementException e) {
			stop();
			return null;
		}
		catch (InvalidTupleException ex) {throw ex;}
		catch (Exception e) {throw new RuntimeException ("Unexpected error reading " + filename, e);}
	
		if (values == null) {
			if (filestream) {
				stop(); 
				return null;
			}
			else {return null;}
		} else {return new SourcedTuple.Wrapper(name, new ArrayTuple(values));}
	}

	/**Close the tree stream.  The next operation will no longer work.
	 * Has next will return false;
	 */
	@Override
	public void stop() {
		try {
			if (source != null) {
				source.close();
			}
		} catch (Exception e) {}
		finally {source = null;}
	}

	/**Returns true if the stream has been opened
	 * and there are more tuples on it.  False if there
	 * are no more tuples (guaranteed to never be more).
	 */
	@Override
	public boolean hasNext() {return source != null;}

	public String getName() {return name;}
	
	/**Remove operation is not supported by the DelimitParser.
	 * @exception UnsupportedOperationException is always thrown.*/
	@Override
	public void remove() {throw new UnsupportedOperationException("DelimitParser does not support remove operation.");}
}
