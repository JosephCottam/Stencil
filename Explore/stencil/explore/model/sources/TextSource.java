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
import stencil.streams.Tuple;
import stencil.streams.TupleStream;
import stencil.util.BasicTuple;


public class TextSource extends StreamSource {
	public static final class TextStream implements TupleStream {
		private boolean closed = false;
		private String separator;
		private String name;
		private String[] labels;
		private String[] rows;
		private int index =0;

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
			Tuple rv = new BasicTuple(name, labels, values);
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

	protected String header;
	protected String separator;
	protected String text;

	public TextSource(String name) {super(name);}

	@Override
	public SourceEditor getEditor() {
		return new Text(this);
	}

	public String getHeader() {return header;}
	public void setHeader(String header) {this.header = header;}

	public String getSeparator() {return separator;}
	public void setSeparator(String separator) {this.separator = separator;}

	public String getText() {return text;}
	public void setText(String text) {this.text = text;}

	public TupleStream getStream(Model context) throws Exception {return new TextStream(name, header, separator, text);}

	public boolean isReady() {
		return header !=null && separator != null && text != null &&
			!header.equals("") && !separator.equals("") && !text.equals("");
	}

	@Override
	public void restore(BufferedReader input) throws IOException {
		throw new UnsupportedOperationException("Cannot restore a text stream.");
	}

}
