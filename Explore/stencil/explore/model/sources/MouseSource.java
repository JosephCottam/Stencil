package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.Mouse;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.tuple.prototype.TupleFieldDef;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.ui.MouseStream;

public class MouseSource extends StreamSource {
	public static final String NAME = "Mouse";

	public MouseSource(String name) {super(name, -1);}
	public SourceEditor getEditor() {return new Mouse(name);}
	public boolean isReady() {return true;}
	public TupleStream getStream(Model context) {
		return new stencil.util.streams.ui.MouseStream(context.getStencilPanel());
	}
	
	public MouseSource name(String name) {
		if(this.name.equals(name)) {return this;}
		return new MouseSource(name);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("STREAM: ");
		b.append(NAME);
		b.append("\n");
		b.append("NAME: ");
		b.append(name);
		b.append("\n");
		b.append("FREQUENCY: ");
		b.append(MouseStream.frequency);
		b.append("\n");
		return b.toString();
	}

	public MouseSource restore(BufferedReader input) throws IOException {
		String line =input.readLine();
		MouseSource result = this;
		while (line != null && !line.trim().equals("")) {
			input.mark(100);
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("FREQUENCY:")) {
				int freq = Integer.parseInt(line.split(": ")[1]);
				MouseStream.frequency = freq;
				break;
			}
			line = input.readLine();
		}
		input.reset();
		return result;
	}

	/**Constructs a header from the system mouse description and the default separator.**/
	public String header() {
		StringBuilder b = new StringBuilder();

		for (TupleFieldDef n: MouseStream.PROTOTYPE) {
			b.append(n.name());
			b.append(StreamSource.DEFAULT_SEPARATOR);
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}

}
