package stencil.module.util;

import stencil.tuple.prototype.TuplePrototype;

public final class FacetData {
	public static enum MemoryUse {FUNCTION, READER, WRITER, OPAQUE} 
	
	private String name;
	private MemoryUse memory;
	private TuplePrototype prototype;
	private String target;
	private final String counterpart;
	
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
	
	public void setName(String name) {this.name = name;}
	public String getName() {return name;}
	
	public String getTarget() {return target!=null?target:name;}
		
	public void setMemory(String memUse) {memory = MemoryUse.valueOf(memUse);}
	public MemoryUse getMemUse() {return memory;}
	public void setMemUse(MemoryUse memUse) {memory = memUse;}
	
	public void setPrototype(TuplePrototype prototype) {this.prototype = prototype;}
	public TuplePrototype getPrototype() {return prototype;}
	
	public String counterpart() {return counterpart;}	
	
	public boolean mutative() {
		return memory == MemoryUse.WRITER || memory == MemoryUse.OPAQUE;
	}
}
