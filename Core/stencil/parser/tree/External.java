package stencil.parser.tree;

import java.util.Collection;

import org.antlr.runtime.Token;

public class External extends StencilTree {
	public External(Token source) {super(source);}

	public String getName() {return token.getText();}
	public TuplePrototype getPrototype() {return (TuplePrototype) getChild(0);}
	
	public static External find(String name, Collection<External> externals) {
		for (External s: externals) {
			if (s.getName().equals(name)) {return s;}
		}
		throw new RuntimeException("Could not find external of name " + name + ".");
	}
}
