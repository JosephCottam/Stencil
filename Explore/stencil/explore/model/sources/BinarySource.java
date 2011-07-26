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
		super(name, -1);
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
