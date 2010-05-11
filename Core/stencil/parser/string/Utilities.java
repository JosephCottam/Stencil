package stencil.parser.string;



import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

import stencil.util.MultiPartName;
import stencil.parser.tree.AstInvokeable;
import static stencil.parser.string.StencilParser.STATE_QUERY;
import static stencil.parser.string.StencilParser.AST_INVOKEABLE;
import static stencil.parser.ParserConstants.QUERY_FACET;


public class Utilities {

	/**Given a facet call, what is the equivalent query facet call?*/
	public static String queryName(String name) {return new MultiPartName(name).modSuffix(QUERY_FACET).toString();}


	/**Turns a chain into a list, discarding anything
	 * that is not an AST_INVOKEABLE with properly set operator.
	 */
	public static Tree stateQueryList(TreeAdaptor adaptor, Tree tree) {
		Tree rv = (Tree) adaptor.create(STATE_QUERY, "");
		while (tree != null) {
			if (tree.getType() == AST_INVOKEABLE &&
					((AstInvokeable) tree).getInvokeable() != null) {
				adaptor.addChild(rv, adaptor.dupNode(tree));
			}
			tree = tree.getChild(0);
		}
		return rv;
	}
}
