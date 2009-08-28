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
import stencil.streams.TupleStream;
import stencil.util.streams.txt.DelimitedParser;

public class FileSource extends StreamSource {
	public static final String NAME = "File";

	protected String filename = "";
	protected String header = "";
	protected String separator = "";
	protected boolean checkHeader = true;

	public FileSource(String name) {super(name);}

	public SourceEditor getEditor() {
		return new File(this);
	}

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
		DelimitedParser input = new DelimitedParser(name, header, separator);
		boolean result = input.open(filename);
		if (!result & checkHeader) {input.next();}//consume the header if it did not get consumed but did return and is marked as having a header
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
		b.append("CHECKHEADER: ");
		b.append(checkHeader);
		b.append("\n");
		return b.toString();
	}

	public void restore(BufferedReader input) throws IOException {
		String line = input.readLine();

		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				name = line.substring(line.indexOf(":") +2);
			} else if (line.startsWith("SEPARATOR")) {
				separator = line.substring(line.indexOf(":") +2);
			} else if (line.startsWith("HEADER")) {
				header = line.substring(line.indexOf(":")+2);
			} else if (line.startsWith("SOURCE")) {
				filename = line.substring(line.indexOf(":") +2);
			} else if (line.startsWith("CHECKHEADER")) {
				checkHeader = (line.substring(line.indexOf(":") +2).toUpperCase().equals("TRUE"));
			}
			input.mark(100);
			line = input.readLine();
		}
		input.reset();
	}

	public String getFilename() {return filename;}
	public void setFilename(String filename) {this.filename = filename;}

	public String getHeader() {return header;}
	public void setHeader(String header) {this.header = header;}

	public String getSeparator() {return separator;}
	public void setSeparator(String separator) {this.separator = separator;}

	public boolean isCheckHeader() {return checkHeader;}
	public void setCheckHeader(boolean checkHeader) {this.checkHeader = checkHeader;}
}
