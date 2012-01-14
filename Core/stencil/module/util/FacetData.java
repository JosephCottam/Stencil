package stencil.module.util;

import stencil.tuple.prototype.TuplePrototype;

public final class FacetData {
	public static enum MemoryUse {FUNCTION, READER, WRITER, OPAQUE} 
	
	private final String name;
	private final MemoryUse memory;
	private final TuplePrototype prototype;
	private final String target;
	private final String counterpart;
	
	/**Copy everything but the memory useage from the passed fd.  Use the new memory usage.**/
	public FacetData(FacetData fd, MemoryUse memory) {this(fd.name, fd.target, fd.counterpart, memory, fd.prototype);}

	public FacetData(String name, MemoryUse memory, String... fields) {this(name, name, name, memory, new TuplePrototype(fields));}
	public FacetData(String name, String counterpart, MemoryUse memory, String... fields) {this(name, name, counterpart, memory, new TuplePrototype(fields));}
	public FacetData(String name, MemoryUse memory, TuplePrototype prototype) {this(name, name, name, memory, prototype);}
	public FacetData(String name, String target, String counterpart, MemoryUse memory, TuplePrototype prototype) {
		this.name = name;
		this.target = target;
		this.counterpart = counterpart;
		this.memory = memory;
		this.prototype = prototype;
	}
	
	public FacetData(FacetData source) {
		this.name = source.name;
		this.target = source.target;
		this.counterpart = source.counterpart;
		this.memory = source.memory;
		this.prototype = source.prototype;
	}
	
	public String name() {return name;}
	public String target() {return target!=null?target:name;}
	public MemoryUse memUse() {return memory;}
	public TuplePrototype prototype() {return prototype;}
	public String counterpart() {return counterpart;}	
	
	public boolean mutative() {
		return memory == MemoryUse.WRITER || memory == MemoryUse.OPAQUE;
	}
}
