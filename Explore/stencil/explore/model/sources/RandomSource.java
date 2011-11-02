package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.Random;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.util.streams.numbers.RandomStream;

public class RandomSource extends StreamSource {
	public static final String NAME = "RandomNumbers";
	private final long length;
	
	public RandomSource(String name) {this(name, 1, Integer.MIN_VALUE, false);}
	
	public RandomSource(String name, int size, long length, boolean delay) {
		super(name, size, delay);
		this.length = length;
	}

	
	@Override
	public SourceEditor getEditor() {return new Random(this);}

	@Override
	public RandomStream getStream(Model context) throws Exception {
		return new RandomStream(name, tupleSize, length);
	}

	public String header() {
		try {
			return asHeader(getStream(null).getFields());
		} catch (Exception e) {
			return null;	//Temporary condition
		}
	}
	private String asHeader(List<String> fields) {
		StringBuilder b =new StringBuilder();
		for (String s: fields) {
			b.append(s);
			b.append(",");
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}

	@Override
	public boolean isReady() {return true;}

	@Override
	public RandomSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new RandomSource(name, tupleSize, length, delay);
	}

	public int size() {return tupleSize;}
	public RandomSource tupleSize(int size) {
		if (this.tupleSize == size) {return this;}
		return new RandomSource(name, size, length, delay);
	}
	
	public long length() {return length;}
	public RandomSource length(long length) {
		if (this.length == length) {return this;}
		return new RandomSource(name, tupleSize, length, delay);
	}
	
	public RandomSource delay(boolean delay) {
		if (this.delay == delay) {return this;}
		return new RandomSource(name, tupleSize, length, delay);
	}

	
	@Override
	public RandomSource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		RandomSource result = this;
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("LENGTH")) {
				long length = Long.parseLong(line.substring(line.indexOf(":") +2));
				result = result.length(length);
			} else if (line.startsWith("TUPLE_SIZE")) {
				int size = Integer.parseInt(line.substring(line.indexOf(":") +2));
				result = result.tupleSize(size);
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
		b.append("TUPLE_SIZE: ");
		b.append(tupleSize);
		b.append("\n");
		return b.toString();

	}

}
