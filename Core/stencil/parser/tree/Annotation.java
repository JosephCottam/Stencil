package stencil.parser.tree;

import org.antlr.runtime.Token;

/**Annotations are key/value pairs for additional information.
 * They may appear in a variety of places, which changes their
 * interpretation.
 */
public class Annotation extends StencilTree {
	public Annotation(Token token) {super(token);}
	public String getKey() {return token.getText();}
	public String getValue() {return getChild(0).getText();} 
}
