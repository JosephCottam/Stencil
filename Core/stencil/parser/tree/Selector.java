package stencil.parser.tree;

import java.util.ArrayList;

import org.antlr.runtime.Token;

public class Selector extends List implements stencil.util.Selector {
	public Selector(Token token) {super(token);}
	
	public String getName(int i) {return get(i).getText();}
	public String getLayer() {return getName(0);}
	public String getAttribute() {
		if (size() >0) {return getName(1);}
		else {return null;}
	}
	
	public java.util.List<String> getNames() {
		ArrayList result = new ArrayList();
		for (Object a: this) {result.add(((Atom) a).getText());}
		return result;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		for (Object o: this) {
			b.append(o.toString());
			b.append(".");
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}

	public int hashCode() {return toString().hashCode();}
	
	public boolean equals(Object o) {
		if (!(o instanceof Selector)) {return false;}
	    Selector os = (Selector) o;
	    
	    if (os.size() != size()) {return false;}
	    
	    for (int i=0; i< size(); i++) {
	    	if (!(os.get(i).equals(get(i)))) {return false;}
	    }
		return true;
	}
}
