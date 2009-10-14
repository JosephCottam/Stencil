/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.util.streams.txt;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.EOFException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.*;

import stencil.streams.Tuple;
import stencil.streams.TupleStream;
import stencil.util.BasicTuple;

/**
 * TupleStream implementation that converts a delimited text file into a tuple
 * stream.  Each line of the file is treated as a tuple.
 * @author jcottam
 *
 */
public final class DelimitedParser implements TupleStream {
	/**How far ahead can the parser read attempting to verify structure?*/
	public static final int READ_AHEAD_LIMIT = 1000;

	/**Reader to get tuples from*/
	private BufferedReader source;

	/**File the reader is reading from*/
	private String filename;

	/**Column labels*/
	private List<String> labels;

	/**Name of the stream*/
	private String name;

	/**Regular expression used to split incoming data.*/
	private final Pattern splitter;
	
	public DelimitedParser(String name, String labels, String delimiter) throws Exception {
		splitter = Pattern.compile(delimiter);
		this.name = name;
		setLabels(labels);
	}

	/**
	 * Open the filename given.  Upon opening, the validate method is called
	 * to ensure the file is consistent with the header description given.
	 *
	 * Unlike standard java filename resolution, this method will expand '~' to the user's home directory
	 *
	 * @param filename
	 */
	public boolean open(String filename) {return open(filename, true);}
	public boolean open(String filename, boolean hasHeader) {
		assert (filename != null && filename.trim() != "") : "Invalid filename supplied.  May not be null.  May not be empty.";
		boolean v =false;

		try {
			source = new BufferedReader(new FileReader(filename));
			v= validate(hasHeader);
			this.filename = filename;
			return v;
		} catch (Exception e) {throw new RuntimeException("Error opening " + filename + ".", e);}
	}


	/**Close the tree stream.  The next operation will no longer work.
	 * Has next will return false;
	 */
	public void close() throws Exception{
		if (source != null && source.ready()) {
			source.close();
			source = null;
		}
	}

	public void reset() throws Exception {
		close();
		open(filename);
	}

	/**Returns true if the stream is ready.  False if the stream is not
	 * ready (stream is null, returns not ready or throws an exception).
	 * This does not guarantees the next piece of input will validate, but
	 * it guarantees that something can be read.
	 */
	public boolean hasNext() {
		try {
			return (source != null && source.ready());
		} catch (Exception e) {
			return false;
		}
	}

	/**Identical to hasNext. (Not the case for all streams, but it is here.)*/
	public boolean ready() {return hasNext();}

	/**Get the next tuple from this file.  Stream must be open and ready and not at the end of the file.
	 * After reading the line, it will attempt to turn it into a tuple.  If the line does not
	 * split into the correct number of values AND ingoreErrorLines is true (the default value),
	 * it will try the next line until it finds one.  If ignoreErrorLines is false, it will throw
	 * an exception.
	 *
	 */
	public Tuple next() {
		if (source == null) {throw new NoSuchElementException("Stream " + name + " not currently opened.");}

		String line;

		try {
			line = source.readLine();
		} catch (EOFException eof) {
			throw new NoSuchElementException("Reached end of file: " + filename);
		} catch (Exception e) {
			throw new RuntimeException("Unknown error reading file.", e);
		}

		//TODO: Convert things by some type schema here.
		List values = Arrays.asList(splitter.split(line));
		if (values.size() != labels.size()) {throw new InvalidInputLineException("Could not treat line as full tuple: "+ line);}
		return new BasicTuple(name, labels, values);
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
	public boolean validate(boolean hasHeader) throws Exception
	{
		if (source ==null) {throw new Exception("Parse file cannot be validated before reader has been intialized.");}
		if (labels ==null) {throw new Exception("Parse file cannot be validated when there is no labels list.");}

		source.mark(READ_AHEAD_LIMIT);
		String line = source.readLine();
		String[] parts = splitter.split(line);
		List<String> header = Arrays.asList(parts);
		if (header.size() != labels.size()) {throw new InvalidHeaderException("Column count does not match the length of the labels list (expected " + labels.size() + " but found " + header.size() + ")");}

		if (!hasHeader) {
			source.reset();
			return true;
		}

		for (int i=0; i<header.size(); i++) {
			String fieldHeader = header.get(i).trim().toUpperCase();
			String label = labels.get(i).trim().toUpperCase();
			if (!fieldHeader.equals(label)) {
				source.reset();
				return false;
			}
		}

		return true;
	}

	public List<String> getLabels() {return labels;}
	public void setLabels(String value) {
		this.labels = Arrays.asList(splitter.split(value));
		for (int i=0; i< labels.size(); i++) {
			labels.set(i, labels.get(i).trim());
		}
	}
	public void setLabels(List<String> value) {this.labels = value;}

	public String getName() {return name;}

	/**Remove operation is not supported by the DelimitParser.
	 * @exception UnsupportedOperationException is always thrown.*/
	public void remove() {throw new UnsupportedOperationException("DelimitParser does not support remove operation.");}
}
