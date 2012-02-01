package stencil.interpreter.tree;

import stencil.tuple.prototype.TuplePrototype;

/**External stream declaration**/
public class StreamDec {
	private final String name;
	private final TuplePrototype prototype;
	private final String type;
	private final Specializer spec;

	public StreamDec(String name, TuplePrototype proto, String type, Specializer spec) {
		this.name = name;
		this.prototype = proto;
		this.type = type;
		this.spec = spec;
	}
	
	public String name() {return name;}
	public String type() {return type;}
	public TuplePrototype prototype() {return prototype;}
	public Specializer specializer() {return spec;}
}
