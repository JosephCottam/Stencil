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
import stencil.explore.ui.components.sources.Database;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.tuple.TupleStream;
import stencil.util.streams.sql.QueryTuples;

public class DBSource extends StreamSource {
	public static final String NAME = "Database";

	private final String query;
	private final String connect;
	private final String header;
	private final String separator;
	private final String driver;

	public DBSource(String name) {this(name,"","","","","");}
	
	public DBSource(String name, String query, String connect, String header, String separator, String driver) {
		super(name);
		this.query = query;
		this.connect = connect;
		this.header = header;
		this.separator = separator;
		this.driver = driver;
	}

	public TupleStream getStream(Model context) throws Exception {
		return new QueryTuples(name, driver, connect, query, header, separator);
	}

	public SourceEditor getEditor() {return new Database(this);}

	public boolean isReady() {
		return query != null && !query.equals("") &&
			   connect != null && !connect.equals("") &&
			   header != null && !header.equals("") &&
			   separator != null && !separator.equals("") &&
			   driver != null && !driver.equals("");
	}

	public DBSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new DBSource(name, query, connect, header, separator, driver);
	}

	
	public String query() {return query;}
	public DBSource query(String query) {
		if (this.query.equals(query)) {return this;}
		return new DBSource(name, query, connect, header, separator, driver);
	}

	public String connect() {return connect;}
	public DBSource connect(String connect) {
		if (this.connect.equals(connect)) {return this;}
		return new DBSource(name, query, connect, header, separator, driver);
	}


	public String header() {return header;}
	public DBSource header(String header) {
		if (this.header.equals(header)) {return this;}
		return new DBSource(name, query, connect, header, separator, driver);
	}

	public String separator() {return separator;}
	public DBSource separator(String separator) {
		if (this.separator.equals(separator)) {return this;}
		return new DBSource(name, query, connect, header, separator, driver);
	}

	public String driver() {return driver;}
	public DBSource driver(String driver) {
		if (this.driver.equals(driver)) {return this;}
		return new DBSource(name, query, connect, header, separator, driver);
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
		b.append("CONNECT: ");
		b.append(connect);
		b.append("\n");
		b.append("QUERY: ");
		b.append(query);
		b.append("\n");
		b.append("DRIVER: ");
		b.append(driver);
		b.append("\n");
		return b.toString();
	}

	public DBSource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		DBSource result = this;
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
			} else if (line.startsWith("CONNECT")) {
				String connect = line.substring(line.indexOf(":") +2);
				result = result.connect(connect);
			} else if (line.startsWith("QUERY")) {
				String query = line.substring(line.indexOf(":") +2);
				result = result.query(query);
			} else if (line.startsWith("DRIVER")) {
				String driver = line.substring(line.indexOf(":") +2);
				result = result.driver(driver);
			}
			input.mark(100);
			line = input.readLine();
		}
		input.reset();
		return result;
	}
}

