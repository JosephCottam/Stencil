package stencil.interpreter.tree;

import java.util.Arrays;

public class Selector {
	public final String att;
	public final String[] path;
	private final int hashCode;
	
	public Selector(String att, String[] path) {
		this.att = att;
		this.path = path;
		
		hashCode = att.hashCode() + Arrays.deepHashCode(path);
	}
	
	public String attribute() {return att;}
	public String[] path() {return path;}

	public int hashCode() {return hashCode;}
	public boolean equals(Object other) {
		return other instanceof Selector 
			&& att.equals(((Selector) other).att)
			&& Arrays.deepEquals(path, ((Selector) other).path);
	}
	
}
