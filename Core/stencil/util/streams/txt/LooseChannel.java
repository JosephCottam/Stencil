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
import java.io.EOFException;
import java.util.Arrays;
import java.util.List;
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
	
	private List<String> labels;
	
	public LooseChannel(List<String> labels, String delimiter) throws Exception {
		splitter = Pattern.compile(delimiter);
		this.labels = labels;
	}

	/**Get the next tuple from this file.  Stream must be open and ready and not at the end of the file.
	 * After reading the line, it will attempt to turn it into a tuple.  If the line does not
	 * split into the correct number of values AND ingoreErrorLines is true (the default value),
	 * it will try the next line until it finds one.  If ignoreErrorLines is false, it will throw
	 * an exception.
	 *
	 */
	public List<Object> next(BufferedReader source) {
		List<Object> rv = null;
		while (rv == null) {rv= softNext(source);}
		return rv;
	}
	
	private List<Object> softNext(BufferedReader source) {
		String line;

		try {
			line = source.readLine();
		} catch (EOFException eof) {
			throw new NoSuchElementException("Reached end of file.");
		} catch (Exception e) {
			throw new RuntimeException("Unknown error reading file.", e);
		}
		if (line == null) {throw new NoSuchElementException("Reached end of file.");}

		
		List values = Arrays.asList(splitter.split(line));
		if (values.size() != labels.size()) {return null;}
		else {return values;}
	}

	/**Always returns true (part of being loose).*/
	public void validate(BufferedReader source) throws Exception, FileValidationException {/*Does nothing.*/}
}
