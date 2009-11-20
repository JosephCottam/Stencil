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
import stencil.explore.ui.components.sources.File;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.tuple.TupleStream;
import stencil.util.streams.txt.DelimitedParser;

public final class FileSource extends StreamSource {
	public static final String NAME = "File";

	private final String filename;
	private final String header;
	private final String separator;
	private final int skip;
	private final boolean strict;
	
	public FileSource(String name) {this(name, "","","", 0, true);}
	public FileSource(String name,String filename, String header, String separator, int skip, boolean strict) {
		super(name);
		this.filename = filename;
		this.header = header;
		this.separator = separator;
		this.skip = skip;
		this.strict = strict;
	}

	public SourceEditor getEditor() {return new File(this);}

	public boolean isReady() {
		return filename != null && !filename.equals("") &&
			   header != null && !header.equals("") &&
			   separator != null;
	}

	/**Returns a stream ready to provide input.
	 *
	 * If the checkHeader flag is set, it will consume the first line in
	 * an attempt to verify that the header matches the specified header.
	 *
	 * Header matching rules are specified in the DelimitedParser.
	 */
	public TupleStream getStream(Model context) throws Exception {
		DelimitedParser input = new DelimitedParser(name, header, filename, separator, strict, skip);
		return input;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("STREAM: ");
		b.append(NAME);
		b.append("\n");
		b.append("NAME: ");
		b.append(name);
		b.append("\n");
		b.append("HEADER: ");
		b.append(header);
		b.append("\n");
		b.append("SEPARATOR: ");
		b.append(separator);
		b.append("\n");
		b.append("SOURCE: ");
		b.append(filename);
		b.append("\n");
		b.append("SKIP: ");
		b.append(skip);
		b.append("\n");
		b.append("STRICT: ");
		b.append(strict);
		b.append("\n");
		return b.toString();
	}

	public FileSource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		FileSource result = this;
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("SEPARATOR")) {
				String separator = line.substring(line.indexOf(":") +2);
				result = result.separator(separator);
			} else if (line.startsWith("HEADER")) {
				String header = line.substring(line.indexOf(":")+2);
				result = result.header(header);
			} else if (line.startsWith("SOURCE")) {
				String filename = line.substring(line.indexOf(":") +2);
				result = result.filename(filename);
			} else if (line.startsWith("SKIP")) {
				int skip = Integer.parseInt(line.substring(line.indexOf(":") +2));
				result = result.skip(skip);
			} else if (line.startsWith("STRICT")) {
				boolean strict = (line.substring(line.indexOf(":") +2).toUpperCase().equals("TRUE"));
				result = result.strict(strict);
			}
			input.mark(100);
			line = input.readLine();
		}
		input.reset();
		return result;
	}

	public FileSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new FileSource(name, filename, header, separator, skip, strict);
	}
	
	public String filename() {return filename;}
	public FileSource filename(String filename) {
		if (this.filename.equals(filename)) {return this;}
		return new FileSource(name, filename, header, separator, skip, strict);
	}

	public String header() {return header;}
	public FileSource header(String header) {
		if (this.header.equals(header)) {return this;}
		return new FileSource(name, filename, header, separator, skip, strict);
	}

	public String separator() {return separator;}
	public FileSource separator(String separator) {
		if (this.separator.equals(separator)) {return this;}
		return new FileSource(name, filename, header, separator, skip, strict);
	}

	public int skip() {return skip;}
	public FileSource skip(int skip) {
		if (this.skip == skip) {return this;}
		return new FileSource(name, filename, header, separator, skip, strict);
	}
	
	public boolean strict() {return strict;}
	public FileSource strict(boolean strict) {
		if (this.strict == strict) {return this;}
		return new FileSource(name, filename, header, separator, skip, strict);
	}
}
