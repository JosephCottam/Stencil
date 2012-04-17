package stencil.interpreter.tree;
import static stencil.parser.ParserConstants.NAME_SPACE;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;

/**Divides a name up into its constituent parts.*/
//final because it is immutable
public final class MultiPartName {	
	private final String pre;
	private final String name;
	private final String facet;

	
	public MultiPartName(String pre, String name) {this(pre, name, null);}
	public MultiPartName(String pre, String name, String facet) {
		this.pre = (pre == null || pre.trim().equals("")) ? "" : pre;
		this.name = name;
		this.facet = (facet == null || facet.trim().equals("")) ? null : facet;
	}
	
	
	/**What was the prefix on the name?*/
	public String prefix() {return pre;}
	public boolean prefixed() {return !pre.equals("");}
	
	/**What was the root name?*/
	public String name() {return name;}
	
	/**What was the facet (same as suffix, but used in operator names).*/
	public String facet() {
		if (facet == null) {
			throw new UnsupportedOperationException("Null facet.");
		} else {return facet;}
	}
	
	public boolean hasFacet() {return facet != null;}
		
	/**Create a new multi-part name, changing the prefix.  If the prefix is the
	 * same as the current prefix, the current object is returned.
	 */
	public MultiPartName modPrefix(String newPrefix) { 
		if (this.pre == null && newPrefix == null || (this.pre != null && this.pre.equals(newPrefix))) {return this;}

		return new MultiPartName(newPrefix, name, facet);
	}
	
	/**Create a new multi-part name, changing the suffix.  If the suffix is the
	 * same as the current suffix, the current object is returned.
	 */	
	public MultiPartName modFacet(String newFacet) {
		if (this.facet == null && newFacet == null 
				|| (this.facet!= null && this.facet.equals(newFacet))) {return this;}
		return new MultiPartName(pre, name, newFacet);
	}

	/**Return the whole name (prefix, name, facet), appropriately delimited.*/
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (!pre.equals("")) {b.append(pre); b.append(NAME_SPACE);}
		b.append(name);
		if (hasFacet()) {
			b.append(NAME_SEPARATOR);
			b.append(facet);
		}
		return b.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MultiPartName)) {return false;}
		MultiPartName other = (MultiPartName) o;
		return name.equals(other.name) 
				&& pre.equals(other.pre) 
				&& (facet == other.facet || (facet != null && facet.equals(other.facet)));
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() * pre.hashCode() * facet.hashCode();
	}

	
	public static MultiPartName parse(String input) {
		int ns = input.indexOf(NAME_SPACE);
		int sep = input.indexOf(NAME_SEPARATOR);
		
		String pre = ns < 0 ? "" : input.substring(0, ns);
		String facet = sep < 0 ? "" : input.substring(sep+1);
		
		String name =  input;
		if (pre.length() >0) {name=name.substring(ns+1);}
		if (facet.length()>0) {name=name.substring(0,sep);}
		
		return new MultiPartName(pre, name, facet);
	}
	
}