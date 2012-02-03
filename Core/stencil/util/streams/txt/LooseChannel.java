package stencil.util.streams.txt;


import java.io.BufferedReader; 
import java.io.EOFException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.regex.*;

/**
 * TupleStream implementation that converts a delimited text file into a tuple
 * stream.  Each line of the file is treated as a tuple.
 * @author jcottam
 *
 */
final class LooseChannel implements NextChannel {
	/**Regular expression used to split incoming data.*/
	private final Pattern splitter;
	
	private final int tupleSize;
	
	public LooseChannel(int tupleSize, Pattern splitter) throws Exception {
		this.splitter = splitter;
		this.tupleSize = tupleSize;
	}

	/**Get the next tuple from this file.  Stream must be open and ready and not at the end of the file.
	 * After reading the line, it will attempt to turn it into a tuple.  If the line does not
	 * split into the correct number of values AND ingoreErrorLines is true (the default value),
	 * it will try the next line until it finds one.  If ignoreErrorLines is false, it will throw
	 * an exception.
	 *
	 */
	public String[] next(BufferedReader source) {
		String[] rv = null;
		while (rv == null) {rv= softNext(source);}
		return rv;
	}
	
	private String[] softNext(BufferedReader source) {
		String line;
		
		try {if (!source.ready()) {return null;}}
		catch (IOException e) {throw new NoSuchElementException();}

		try {
			line = source.readLine();
		} catch (EOFException eof) {
			throw new NoSuchElementException("Reached end of file.");
		} catch (Exception e) {
			throw new RuntimeException("Unknown error reading file.", e);
		}
		if (line == null) {throw new NoSuchElementException("Reached end of file.");}

		
		String[] values = splitter.split(line);
		if (values.length != tupleSize) {return null;}
		else {return values;}
	}

	/**Always returns true (part of being loose).*/
	public void validate(BufferedReader source) throws Exception {/*Does nothing.*/}
}
