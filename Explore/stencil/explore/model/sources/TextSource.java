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
package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;


import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.explore.ui.components.sources.Text;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;


public final class TextSource extends StreamSource {
	/**Stream source from a string.*/
	public final static class TextStream implements TupleStream {
		private final String separator;
		private final String name;
		private final String[] labels;
		private final String[] rows;
		private int index =0;
		private boolean closed = false;

		public TextStream(String name, String header, String separator, String text) {
			this.separator = separator;
			this.name = name;

			labels = header.split(separator);
			rows = text.split("\n");
		}

		public void close() throws Exception {closed = true;}

		public Tuple next() {
			if (!hasNext()) {throw new RuntimeException("Cannot call next when hasNext is false.");}
			String[] values = rows[index].split(separator);
			Tuple rv = new PrototypedTuple(name, labels, values);
			index++;
			return rv;
		}

		public void reset() throws Exception {
			index =0;
			closed = false;
		}

		public boolean hasNext() {return !closed && index < rows.length;}
		public boolean ready() {return hasNext();}

		public void remove() {
			throw new UnsupportedOperationException("Cannot remove elements from a TextStream.");
		}

	}

	private final String header;
	private final String separator;
	private final String text;

	public TextSource(String name) {this(name, null, null, null);}
	public TextSource(String name, String header, String separator, String text) {
		super(name);
		this.header = header;
		this.separator = separator;
		this.text = text;
	}

	public SourceEditor getEditor() {
		return new Text(this);
	}

	public TextSource name(String name) {
		if(this.name.equals(name)) {return this;}
		return new TextSource(name, header, separator, text);		
	}
	
	public String header() {return header;}
	public TextSource header(String header) {
		if(this.header.equals(header)) {return this;}
		return new TextSource(name, header, separator, text);
	}

	public String separator() {return separator;}
	public TextSource separator(String separator) {
		if(this.separator.equals(separator)) {return this;}
		return new TextSource(name, header, separator, text);
	}

	public String text() {return text;}
	public TextSource text(String text) {
		if(this.text.equals(text)) {return this;}
		return new TextSource(name, header, separator, text);
	}

	public TupleStream getStream(Model context) throws Exception {return new TextStream(name, header, separator, text);}

	public boolean isReady() {
		return header !=null && separator != null && text != null &&
			!header.equals("") && !separator.equals("") && !text.equals("");
	}

	@Override
	public TextSource restore(BufferedReader input) throws IOException {
		throw new UnsupportedOperationException("Cannot restore a text stream.");
	}

}
