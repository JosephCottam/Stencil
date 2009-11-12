package stencil.parser.tree;

import org.antlr.runtime.Token;

/**A direct yield is a yield used in a call chain.
 * The name is used in name resolution but may be null.
 * 
 * @author jcottam
 *
 */
public class DirectYield extends Pass {
	public DirectYield(Token token) {super(token);}
	public String getName() {return token.getText();}
}
