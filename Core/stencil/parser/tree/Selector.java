package stencil.parser.tree;

import org.antlr.runtime.Token;

public class Selector extends List {
	public Selector(Token token) {super(token);}
	
	public String getLayer() {return get(0).getText();}
	public String getAttribute() {
		if (size() >0) {return get(1).getText();}
		else {return null;}
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
