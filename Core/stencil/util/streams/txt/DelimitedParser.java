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


import java .io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.*;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;

import static java.lang.String.format;

/**
 * TupleStream implementation that converts a delimited text file into a tuple
 * stream.  Each line of the file is treated as a tuple.
 * @author jcottam
 *
 */
public final class DelimitedParser implements TupleStream {
	/**Reader to get tuples from*/
	private BufferedReader source;

	/**File the reader is reading from*/
	private final String filename;

	/**Column labels*/
	private List<String> labels;

	/**Name of the stream*/
	private String name;

	/**Regular expression used to split incoming data.*/
	private final Pattern splitter;

	private final int skip;
	
	private final NextChannel channel;
	
	/**Stored tuple, returned before anything new will be read.
	 * TODO: THIS IS NOT THREAD SAFE!!!!
	 * */
	private Tuple tupleCache;
	
	public DelimitedParser(String name, String labels, String filename, String delimiter, boolean strict, int skip) throws Exception {
		splitter = Pattern.compile(delimiter);
		this.name = name;
		this.filename = filename;
		this.skip = skip;
		setLabels(labels);
		if (strict) {this.channel = new StrictChannel(this.labels, delimiter);}
		else {this.channel = new LooseChannel(this.labels, delimiter);}
		open();
	}

	/**Makes the stream ready to be read from.
	 *
	 * Unlike standard java filename resolution, this method will expand '~' to the user's home directory
	 *
	 * @param filename
	 */
	private void open() {
		assert (filename != null && filename.trim() != "") : "Invalid filename supplied.  May not be null.  May not be empty.";

		try {
			source = new BufferedReader(new FileReader(filename));
			int skip = this.skip;
			try {while (skip-- > 0) {source.readLine();}}
			catch (Exception e) {throw new RuntimeException("Error trying to skip indicated lines.", e);}
			
			channel.validate(source);
		} catch (Exception e) {throw new RuntimeException("Error opening " + filename + ".", e);}
	}

	public Tuple next() {
		Tuple t;
		if (source == null) {throw new NoSuchElementException(format("Stream %1$s closed.", name));}
		if (tupleCache != null) {
			t= tupleCache;
			tupleCache = null;
		}else {
			List<Object> values; 
			try {values = channel.next(source);}
			catch (NoSuchElementException e) {throw new NoSuchElementException("Reached end of file: " + filename);}
			catch (Exception e) {throw new RuntimeException ("Unexpected error reading " + filename, e);}
			
			t = new PrototypedTuple(name, labels, values);
		}

		return t;
	}

	/**Close the tree stream.  The next operation will no longer work.
	 * Has next will return false;
	 */
	public void close() throws Exception{
		if (source != null && source.ready()) {
			tupleCache = null;
			source.close();
			source = null;
		}
	}

	/**Returns true if the stream has been opened
	 * and there are more tuples on it.  False if there
	 * are no more tuples (guaranteed to never be more).
	 */
	public boolean hasNext() {
		if (source == null ) {return false;}
		if (tupleCache != null) {return true;}

		try {tupleCache = next();}
		catch (Exception e) {return false;}
		
		assert tupleCache != null : "Null value cache after supposedly successful next.";		
		return true;
	}

	
	/**Is a tuple immediately available?*/
	public boolean ready() {
		hasNext();	
		return tupleCache != null;
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
