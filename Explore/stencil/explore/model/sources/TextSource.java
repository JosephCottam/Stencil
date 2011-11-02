package stencil.explore.model.sources;
 
import java.io.BufferedReader;
import java.io.IOException;


import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.explore.ui.components.sources.Text;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.stream.TupleStream;


public final class TextSource extends StreamSource {
	/**Stream source from a string.*/
	public final static class TextStream implements TupleStream {
		private final String separator;
		private final String name;
		private final String[] rows;
		private final int tupleSize;
		private int index =0;
		private boolean closed = false;

		public TextStream(String name, int tupleSize, String separator, String text) {
			this.separator = separator;
			this.name = name;
			this.tupleSize = tupleSize;
			
			rows = text.split("\n");
		}

		public void close() throws Exception {closed = true;}

		public SourcedTuple next() {
			if (!hasNext()) {throw new RuntimeException("Cannot call next when hasNext is false.");}
			String[] values = rows[index].split(separator);
			if (values.length != tupleSize) {throw new RuntimeException(String.format("Line %1$s has unexpected number of values (expected %1$s, found %2$s).", index, tupleSize, values.length));}

			Tuple rv = new ArrayTuple(values);
			index++;
			return new SourcedTuple.Wrapper(name, rv);
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

	private final String separator;
	private final String text;

	public TextSource(String name) {this(name, 0, null, null, false);}
	public TextSource(String name, int size, String separator, String text, boolean delay) {
		super(name, size, delay);
		this.separator = separator;
		this.text = text;
	}

	public SourceEditor getEditor() {
		return new Text(this);
	}

	public TextSource name(String name) {
		if(this.name.equals(name)) {return this;}
		return new TextSource(name, tupleSize, separator, text, delay);		
	}
	
	public TextSource tupleSize(int size) {
		if (this.tupleSize == size) {return this;}
		return new TextSource(name, size, separator, text, delay);
	}

	public String separator() {return separator;}
	public TextSource separator(String separator) {
		if(this.separator.equals(separator)) {return this;}
		return new TextSource(name, tupleSize, separator, text, delay);
	}

	public String text() {return text;}
	public TextSource text(String text) {
		if(this.text.equals(text)) {return this;}
		return new TextSource(name, tupleSize, separator, text, delay);
	}
	
	public TextSource delay(boolean delay) {
		if (delay == this.delay) {return this;}
		return new TextSource(name, tupleSize, separator, text, delay);
	}


	public TupleStream getStream(Model context) throws Exception {return new TextStream(name, tupleSize, separator, text);}

	public boolean isReady() {
		return separator != null && text != null
			&& !separator.equals("") && !text.equals("");
	}

	@Override
	public TextSource restore(BufferedReader input) throws IOException {
		throw new UnsupportedOperationException("Cannot restore a text stream.");
	}

}
