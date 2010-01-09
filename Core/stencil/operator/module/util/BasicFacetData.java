package stencil.operator.module.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.Map;

import stencil.operator.module.FacetData;
import stencil.operator.module.OperatorData.OpType;
import stencil.parser.tree.Atom;
import stencil.parser.tree.StencilString;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;

public class BasicFacetData implements FacetData {
	protected final String name;
	protected final OpType type;
	protected final TuplePrototype prototype;
	protected final Map<String, String> attributes;

	public BasicFacetData(String name, OpType type, Collection<Atom> fields) {this(name, type, convertFields(fields));}
	public BasicFacetData(String name, String type, String...fields) {this(name, OpType.valueOf(type), fields);}
	public BasicFacetData(String name, OpType type, String...fields) {this(name, type, Arrays.asList(fields));}
	public BasicFacetData(String name, String type, List<String> fields) {this(name, OpType.valueOf(type), fields);}
	public BasicFacetData(String name, String type, TuplePrototype prototype) {this(name, OpType.valueOf(type), prototype);}
	public BasicFacetData(String name, OpType type, List<String> fields) {
		this.name = name;
		this.type = type;
		prototype = new SimplePrototype(fields);	//TODO: Actually get the types from somewhere and use them, instead of just giving up on all types!
		attributes = new HashMap();
	}
	public BasicFacetData(String name, OpType type, TuplePrototype prototype) {
		this.name = name;
		this.type = type;
		this.prototype = prototype;
		attributes = new HashMap();
	}
	
	public String getName() {return name;}
	public OpType getFacetType() {return type;}
	public TuplePrototype getPrototype() {return prototype;}
	public String getAttribute(String key) {return attributes.get(key);}
	public void addAttribute(String key, String value) {attributes.put(key, value);}
	
	private static String[] convertFields(Collection<Atom> fields) {
		String[] names = new String[fields.size()];
		int i =0;
		for (Atom a: fields) {
			if (!(a instanceof StencilString)) {throw new IllegalArgumentException("Non-string used to set default prototype.");} 
			StencilString s = (StencilString) a;
			names[i] = s.getString();
			i++;
		}
		return names;
	}
}
