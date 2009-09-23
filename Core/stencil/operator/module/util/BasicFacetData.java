package stencil.operator.module.util;

import java.util.Arrays;
import java.util.List;
import java.util.Collection;

import stencil.operator.module.FacetData;
import stencil.operator.module.OperatorData.OpType;
import stencil.parser.tree.Atom;
import stencil.parser.tree.StencilString;

public class BasicFacetData implements FacetData {
	protected String name;
	protected OpType type;
	protected List<String> fields;

	public BasicFacetData(String name, OpType type, Collection<Atom> fields) {this(name, type, convertFields(fields));}
	public BasicFacetData(String name, String type, String...fields) {this(name, OpType.valueOf(type), fields);}
	public BasicFacetData(String name, OpType type, String...fields) {this(name, type, Arrays.asList(fields));}
	public BasicFacetData(String name, String type, List<String> fields) {this(name, OpType.valueOf(type), fields);}
	public BasicFacetData(String name, OpType type, List<String> fields) {
		this.name = name;
		this.type = type;
		this.fields = fields;
	}
	
	public String getName() {return name;}
	public OpType getFacetType() {return type;}
	public List<String> tupleFields() {return fields;}
	
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
