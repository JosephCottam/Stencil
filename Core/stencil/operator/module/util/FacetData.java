package stencil.operator.module.util;

import java.util.Arrays;
import java.util.List;

import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;

public final class FacetData {
	private String name;
	private String type;
	private boolean function;
	private TuplePrototype prototype;
	private String target;
	private String roles;

	public FacetData() {}
	
	public FacetData(String name, String type, boolean function, String... fields) {
		this(name, type, function, Arrays.asList(fields));
	}
	
	public FacetData(String name, String type, boolean function, List<String> fields) {
		this(name, type, function, new SimplePrototype(fields));
	}
	
	public FacetData(String name, String type, boolean function, TuplePrototype prototype) {
		this.name = name;
		this.function = function;
		this.prototype = prototype;
		setType(type);
	}
	
	public FacetData(FacetData source) {
		this.name = source.name;
		this.function = source.function;
		this.prototype = source.prototype;
		setType(source.type);
	}
	
	public void setName(String name) {this.name = name;}
	public String getName() {return name;}

	public void setType(String type) {this.type = type.toUpperCase();}
	public String getType() {return type;}
	
	public void setTarget(String target) {this.target = target;}
	public String getTarget() {return target!=null?target:name;}
	
	public String getRoles() {return roles;}
	public void setRoles(String roles) {this.roles = roles;}
	public boolean hasRole(String role) {return roles.indexOf(role) >=0;}
	
	public boolean isFunction() {return function;}
	public void setFunction(boolean fun) {function = fun;}
	public void setPrototype(TuplePrototype prototype) {this.prototype = prototype;}
	public TuplePrototype getPrototype() {return prototype;}
}
