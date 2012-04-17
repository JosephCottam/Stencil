package stencil.util.streams.txt;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import stencil.tuple.stream.InvalidTupleException;

final class StrictChannel implements NextChannel {
	/**How far ahead can the parser read attempting to verify structure?*/
	public static final int READ_AHEAD_LIMIT = 1000;

	/**Column labels*/
	private final int tupleSize;

	/**Regular expression used to split incoming data.*/
	private final Pattern splitter;
	
	public StrictChannel(int tupleSize, Pattern splitter) throws Exception {
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
	@Override
	public String[] next(BufferedReader source) throws NoSuchElementException, RuntimeException {
		String line;

		try {if (!source.ready()) {return null;}}
		catch (IOException e) {throw new NoSuchElementException();}
				
		try {
			line = source.readLine();
		} catch (EOFException eof) {
			throw new NoSuchElementException("Reached end of file");
		} catch (Exception e) {
			throw new RuntimeException("Unknown error reading file.", e);
		}

		if (line == null) {throw new NoSuchElementException("Reached end of file.");}
		
		//TODO: Convert things by some type schema here.
		String[] values;
		if (tupleSize == 1) {values = new String[]{line};}
		else {values = splitter.split(line);}
		
		if (values.length != tupleSize) {
			throw new InvalidTupleException("Could not treat line as full tuple: '"+ line + "'");
		}
		return values;
	}
	
	/**Verifies that the file has a format compatible with the labels list given.
	 * File is considered 'compatible' if the file has the same number
	 * of elements in its first row as header items.  If the first row also
	 * matches the header, it will be consumed (and never returned as a tuple).
	 * In order for a header to be identified, the field labels must
	 * match the header labels passed.  Field label matching is case-insensitive and ignores surrounding white space.
	 *
 	 * If stream has not been initialized (via 'open') and if there are no labels
	 * specified, an exception is also thrown.
	 *
	 * If the first line length exceeds READ_AHEAD_LIMIT, the first line will be consumed,
	 * but never returned as a tuple.
	 *
	 * @return True if everything that is checked matches.
	 * @throws Exception Thrown when column count does not match or stream is not open or does not have a labels list.
	 */
	@Override
	public void validate(BufferedReader source) throws Exception, FileValidationException
	{
		if (source ==null) {throw new Exception("Parse file cannot be validated before reader has been intialized.");}
		if (tupleSize < 0) {throw new Exception("Invalid tuple size.");}

		source.mark(READ_AHEAD_LIMIT);
		while(!source.ready()) {Thread.sleep(100);}
		String line = source.readLine();
		
		String[] parts = splitter.split(line);
		List<String> header = Arrays.asList(parts);
		if (header.size() != tupleSize) {
			throw new FileValidationException(tupleSize, header.size());
		}
		source.reset();
	}

}
