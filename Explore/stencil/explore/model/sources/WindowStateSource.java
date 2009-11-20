package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.explore.ui.components.sources.WindowState;
import stencil.tuple.TupleStream;
import stencil.util.streams.ui.ComponentEventStream;

public class WindowStateSource extends StreamSource {
	public static final String NAME = "Window State";
	public static final String HEADER;
	static {
		StringBuilder b = new StringBuilder();
		for (String s: ComponentEventStream.FIELDS) {
			b.append(s);
			b.append(",");
		}
		b.deleteCharAt(b.length()-1);
		HEADER = b.toString();
	}

	private final boolean onChange;
	
	public WindowStateSource(String name) {this(name, false);}
	public WindowStateSource(String name, boolean onChange) {
		super(name);
		this.onChange = onChange;
	}
	
	public SourceEditor getEditor() {return new WindowState(this);}
	public String header() {return HEADER;}

	public WindowStateSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new WindowStateSource(name, onChange);
	}

	public boolean onChange() {return onChange;}
	public WindowStateSource onChange(boolean onChange) {
		if (this.onChange == onChange) {return this;}
		return new WindowStateSource(name, onChange);
	}
	
	
	public TupleStream getStream(Model context) throws Exception {
		return new ComponentEventStream(name, context.getStencilPanel(), onChange);
	}

	public boolean isReady() {return true;}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("STREAM: ");
		b.append(NAME);
		b.append("\n");
		b.append("NAME: ");
		b.append(name);
		b.append("\n");
		b.append("ON_CHANGE: ");
		b.append(onChange);
		b.append("\n");
		return b.toString();
	}
	
	@Override
	public WindowStateSource restore(BufferedReader input) throws IOException {
		String line =input.readLine();
		WindowStateSource result = this;
		while (line != null && !line.trim().equals("")) {
			input.mark(100);
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("ON_CHANGE:")) {
				String part = line.split(": ")[1];
				boolean onChange = part.toUpperCase().equals("TRUE");
				result= result.onChange(onChange);
			}
			line = input.readLine();
		}
		input.reset();
		return result;
	}

}
