package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.Sequence;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.util.streams.numbers.SequenceStream;

public class SequenceSource extends StreamSource {
	public static final String NAME = "SequenceNumbers";
	private final long length;
	private final long increment;
	private final long start;
	
	public SequenceSource(String name) {this(name, 0,1, Integer.MIN_VALUE);}
	
	public SequenceSource(String name, long start, long increment, long length) {
		super(name);
		this.start= start;
		this.increment = increment;
		this.length = length;
	}

	
	@Override
	public SourceEditor getEditor() {return new Sequence(this);}

	@Override
	public SequenceStream getStream(Model context) throws Exception {
		return new SequenceStream(name, start, increment, length);
	}

	@Override
	public String header() {return "VALUE";}

	@Override
	public boolean isReady() {return true;}

	@Override
	public SequenceSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new SequenceSource(name, start, increment, length);
	}

	public long start() {return start;}
	public SequenceSource start(long start) {
		if (this.start == start) {return this;}
		return new SequenceSource(name, start, increment, length);
	}

	public long increment() {return increment;}
	public SequenceSource increment(long increment) {
		if (this.increment == increment) {return this;}
		return new SequenceSource(name, start, increment, length);
	}
	
	public long length() {return length;}
	public SequenceSource length(long length) {
		if (this.length == length) {return this;}
		return new SequenceSource(name, start, increment, length);
	}
	
	@Override
	public SequenceSource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		SequenceSource result = this;
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("LENGTH")) {
				long length = Long.parseLong(line.substring(line.indexOf(":") +2));
				result = result.length(length);
			} else if (line.startsWith("START")) {
				long start = Integer.parseInt(line.substring(line.indexOf(":") +2));
				result = result.start(start);
			} else if (line.startsWith("INCREMENT")) {
				long increment = Integer.parseInt(line.substring(line.indexOf(":") +2));
				result = result.increment(increment);
			}
			input.mark(100);
			line = input.readLine();
		}
		input.reset();
		return result;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("STREAM: ");
		b.append(NAME);
		b.append("\n");
		b.append("NAME: ");
		b.append(name);
		b.append("\n");
		b.append("LENGTH: ");
		b.append(length);
		b.append("\n");
		b.append("INCREMENT: ");
		b.append(increment);
		b.append("\n");
		b.append("START: ");
		b.append(start);
		b.append("\n");
		return b.toString();

	}

}
