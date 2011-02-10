package stencil.interpreter.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.parser.tree.StencilTree;
import stencil.util.collections.ArrayUtil;
import static stencil.parser.string.StencilParser.SPECIALIZER;
import static stencil.parser.ParserConstants.POSITIONAL_ARG;

public final class Specializer {
	private final String[] keys;
	private final Object[] vals;
	private final StencilTree source;
	
	public Specializer() {keys = null; vals=null; source=null;}
	public Specializer(String[] keys, Object[] vals) {this(keys, vals, null);}
	Specializer(String[] keys, Object[] vals, StencilTree source) {
		this.keys = keys;
		this.vals = vals;
		this.source = source;
	}
	
	public int size() {return keys.length;}
	public Object get(String key) { 
		int idx=ArrayUtil.indexOf(key, keys);
		return idx<0 ? null : vals[idx];
	}
	
	public boolean containsKey(String key) {return ArrayUtil.indexOf(key, keys)>=0;}
	public Iterable<String> keySet() {return Arrays.asList(keys);}
	public boolean isEmpty() {return vals.length==0;}
	
	public StencilTree getSource() {return source;}
	
	/**Blend the update with the defaults.  
	 * The update values take precedence over the default values.*/
	public static Specializer blend(StencilTree updates, StencilTree defaults) {
		assert updates.getType() == SPECIALIZER;
		assert defaults.getType() == SPECIALIZER;
	
		Specializer updatez = Freezer.specializer(updates);
		Specializer defaultz = Freezer.specializer(defaults);
		
		List<String> keys =new ArrayList();
		List<Object> values = new ArrayList();
		for (int i=0; i<updatez.keys.length; i++) {
			if (updatez.keys[i].equals(POSITIONAL_ARG)) {
				keys.add(defaultz.keys[i]);
			} else {
				keys.add(updatez.keys[i]);				
			}
			values.add(updatez.vals[i]);
		}
		
		for (String key: defaultz.keySet()) {
			if (keys.contains(key)) {continue;}
			keys.add(key);
			values.add(defaultz.get(key));
		}
		return new Specializer(keys.toArray(new String[keys.size()]), values.toArray(), updates);
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{");
		for (String key: keys) {
			b.append(key);
			b.append(":");
			b.append(get(key));
			b.append(", ");
		}
		if (size() >0){b.delete(b.length()-2, b.length());}
		
		b.append("}");
		
		return "Specializer:" + b;}
}
