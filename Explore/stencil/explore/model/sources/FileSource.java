package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.File;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.txt.DelimitedParser;

public final class FileSource extends StreamSource {
	public static final String NAME = "File";

	private final String filename;
	private final String separator;
	private final int skip;
	private final boolean strict;
	
	public FileSource(String name) {this(name, 0,"","", 0, true, false);}
	public FileSource(String name, int size, String filename, String separator, int skip, boolean strict, boolean delay) {
		super(name, size, delay);
		this.filename = filename;
		this.separator = separator;
		this.skip = skip;
		this.strict = strict;
	}

	public SourceEditor getEditor() {return new File(this);}

	public boolean isReady() {
		return filename != null && !filename.equals("") &&
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
		DelimitedParser input = new DelimitedParser(name, filename, separator, tupleSize, strict, skip);
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
		b.append("TUPLE_SIZE: ");
		b.append(tupleSize);
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
			} else if (line.startsWith("TUPLE_SIZE")) {
				String size = line.substring(line.indexOf(":")+2);
				result = result.tupleSize(Integer.parseInt(size));
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
		return new FileSource(name, tupleSize, filename, separator, skip, strict, delay);
	}
	
	public String filename() {return filename;}
	public FileSource filename(String filename) {
		if (this.filename.equals(filename)) {return this;}
		return new FileSource(name, tupleSize, filename, separator, skip, strict, delay);
	}

	public FileSource tupleSize(int size) {
		if (size == this.tupleSize) {return this;}
		return new FileSource(name, size, filename, separator, skip, strict, delay);
	}

	public String separator() {return separator;}
	public FileSource separator(String separator) {
		if (this.separator.equals(separator)) {return this;}
		return new FileSource(name, tupleSize, filename, separator, skip, strict, delay);
	}

	public int skip() {return skip;}
	public FileSource skip(int skip) {
		if (this.skip == skip) {return this;}
		return new FileSource(name, tupleSize, filename, separator, skip, strict, delay);
	}
	
	public boolean strict() {return strict;}
	public FileSource strict(boolean strict) {
		if (this.strict == strict) {return this;}
		return new FileSource(name, tupleSize, filename, separator, skip, strict, delay);
	}

	public FileSource delay(boolean delay) {
		if (delay == this.delay) {return this;}
		return new FileSource(name, tupleSize, filename, separator, skip, strict, delay);
	}

}


