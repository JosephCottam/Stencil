package stencil.parser.tree;

import org.antlr.runtime.Token;

public class Guide extends StencilTree {

	public Guide(Token token) {super(token);}
	
	public String getGuideType() {return getChild(0).getText();}
	public Specializer getArguments() {return (Specializer) getChild(1);}
	public String getAttribute() {return getChild(2).getText();}
	public CallGroup getAction() {return (CallGroup) getChild(4);}
}
