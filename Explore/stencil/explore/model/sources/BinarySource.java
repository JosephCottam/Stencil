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
import stencil.explore.ui.components.sources.Binary;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.binary.BinaryTupleStream;

public final class BinarySource extends StreamSource {
	public static final String NAME = "Binary";

	private final String filename;
	
	public BinarySource(String name) {this(name, "");}
	public BinarySource(String name,String filename) {
		super(name);
		this.filename = filename;
	}

	public SourceEditor getEditor() {return new Binary(this);}

	public boolean isReady() {
		return filename != null && !filename.equals("");
	}

	/**Returns a stream ready to provide input.
	 *
	 * If the checkHeader flag is set, it will consume the first line in
	 * an attempt to verify that the header matches the specified header.
	 *
	 * Header matching rules are specified in the DelimitedParser.
	 */
	public TupleStream getStream(Model context) throws Exception {
		return new BinaryTupleStream.Reader(name, filename);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("STREAM: ");
		b.append(NAME);
		b.append("\n");
		b.append("NAME: ");
		b.append(name);
		b.append("\n");
		b.append("SOURCE: ");
		b.append(filename);
		b.append("\n");
		return b.toString();
	}

	public BinarySource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		BinarySource result = this;
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("SOURCE")) {
				String filename = line.substring(line.indexOf(":") +2);
				result = result.filename(filename);
			}
			input.mark(100);
			line = input.readLine();
		}
		input.reset();
		return result;
	}

	public BinarySource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new BinarySource(name, filename);
	}
	
	public String filename() {return filename;}
	public BinarySource filename(String filename) {
		if (this.filename.equals(filename)) {return this;}
		return new BinarySource(name, filename);
	}
}
