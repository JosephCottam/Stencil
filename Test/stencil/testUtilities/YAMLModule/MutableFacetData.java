package stencil.testUtilities.YAMLModule;

import stencil.tuple.prototype.TuplePrototype;

public class MutableFacetData {
	private String name;
	private String type;
	private boolean function;
	private TuplePrototype prototype;
	
	public void setName(String name) {this.name = name;}
	public String getName() {return name;}

	public void setType(String type) {this.type = type;}	//TODO: Should this be OpType enum instead of a string?
	public String getType() {return type;}
	
	public boolean isFunction() {return function;}
	public void setFunction(boolean fun) {function = fun;}
	public void setPrototype(TuplePrototype prototype) {this.prototype = prototype;}
	public TuplePrototype getPrototype() {return prototype;}
}
