package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.Sequence;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.util.streams.numbers.SequenceStream;

public class SequenceSource extends StreamSource {
	public static final String NAME = "SequenceNumbers";
	private final double start;
	private final double increment;
	private final double stop;
	
	public SequenceSource(String name) {this(name, 0,1, Double.MAX_VALUE, false);}
	
	public SequenceSource(String name, double start, double increment, double stop, boolean delay) {
		super(name,1, delay);
		this.start= start;
		this.increment = increment;
		this.stop = stop;
	}

	
	@Override
	public SourceEditor getEditor() {return new Sequence(this);}

	@Override
	public SequenceStream getStream(Model context) throws Exception {
		return new SequenceStream(name, start, increment, stop);
	}

	public String header() {return "VALUE";}

	@Override
	public boolean isReady() {return true;}

	@Override
	public SequenceSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new SequenceSource(name, start, increment, stop, delay);
	}

	public double start() {return start;}
	public SequenceSource start(double start) {
		if (this.start == start) {return this;}
		return new SequenceSource(name, start, increment, stop, delay);
	}

	public double increment() {return increment;}
	public SequenceSource increment(double increment) {
		if (this.increment == increment) {return this;}
		return new SequenceSource(name, start, increment, stop, delay);
	}
	
	public double stop() {return stop;}
	public SequenceSource stop(double stop) {
		if (this.stop == stop) {return this;}
		return new SequenceSource(name, start, increment, stop, delay);
	}
	
	public SequenceSource delay(boolean delay) {
		if (delay == this.delay) {return this;}
		return new SequenceSource(name, start, increment, stop, delay);
	}

	
	@Override
	public SequenceSource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		SequenceSource result = this;
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("STOP")) {
				double stop = Double.parseDouble(line.substring(line.indexOf(":") +2));
				result = result.stop(stop);
			} else if (line.startsWith("START")) {
				double start = Double.parseDouble(line.substring(line.indexOf(":") +2));
				result = result.start(start);
			} else if (line.startsWith("INCREMENT")) {
				double increment = Double.parseDouble(line.substring(line.indexOf(":") +2));
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
		b.append("STOP: ");
		b.append(stop);
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
