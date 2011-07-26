package stencil.interpreter.tree;

import java.util.Arrays;
import java.util.Iterator;

public class TupleField implements Iterable<String>{
	private final String[] parts;

	public TupleField(String[] parts) {this.parts= Arrays.copyOf(parts, parts.length);}

	public String parts(int i) {return parts[i];}
	public int size() {return parts.length;}
	public Iterator<String> iterator() {return Arrays.asList(parts).iterator();}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (String part: parts) {
			b.append(part);
			b.append(".");
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof TupleField)) {return false;}
		TupleField alter = (TupleField) other;
		
		if (parts.length != alter.parts.length) {return false;}
		
		for (int i=0; i< parts.length; i++) {
			if (!parts[i].equals(alter.parts[i])) {return false;}
		}
		return true;
	}
	
	public int hashCode() {return Arrays.deepHashCode(parts);}
}
