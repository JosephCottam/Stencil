package stencil.explore.model.sources;


import java.io.BufferedReader;
import java.io.IOException;

import stencil.tuple.stream.TupleStream;
import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.SourceEditor;

public abstract class StreamSource implements Comparable<StreamSource> {
	public static String DEFAULT_SEPARATOR = ",";

	protected final String name;
	protected final int tupleSize;
	protected final boolean delay;

	protected StreamSource(String name, int size, boolean delay) {
		this.name = name;
		this.tupleSize = size;
		this.delay = delay;
		SourceCache.put(this);
	}

	/**Get an editor panel to be used to set properties on this source?
	 * The panel returned should be linked into the source properly so no
	 * further wiring is required.
	 *
	 * */
	public abstract SourceEditor getEditor();

	/**Are all relevant values set?*/
	public abstract boolean isReady();

	/**What is the name of the stream in this source?
	 * Name is the only property shared between all source types.
	 * */
	public String name() {return name;}
	public abstract StreamSource name(String name);

	public int size() {return tupleSize;}
	public StreamSource tupleSize(int size) {throw new UnsupportedOperationException();}
	
	public boolean delay() {return delay;}
	public abstract StreamSource delay(boolean delay);
	
	/**What type of stream is this?
	 * Looks for a static field called "NAME", if not
	 * found returns the class name.
	 *
	 * */
	public String getTypeName() {
		try {return (String) this.getClass().getField("NAME").get(null);}
		catch (Exception e) {return this.getClass().getName();}
	}

	/**Create a tuple stream based on the information of the stream source.**/
	public abstract TupleStream getStream(Model context) throws Exception;

	/**Restore a stream source from its own 'toString' output.
	 * TODO: Generalize with a reflection lookup of field names or setters (then you can remove the individual class methods...maybe learn about standard Java serialization?)
	 * */
	public abstract StreamSource restore(BufferedReader input) throws IOException;

	/**Stream sources are compared according to their names.  A null stream
	 * source is considered less than an instantiated one.
	 */
	public int compareTo(StreamSource o) {
		if (o == null) {return 1;}
		if (this == o) {return 0;}
		
		return o.name().compareTo(this.name());
	}
}
