package stencil.parser.string.util;



import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

import stencil.module.operator.StencilOperator;
import stencil.parser.tree.AstInvokeable;
import stencil.parser.tree.TupleRef;
import stencil.parser.tree.util.MultiPartName;
import static stencil.parser.string.StencilParser.STATE_QUERY;
import static stencil.parser.ParserConstants.QUERY_FACET;
import static stencil.parser.ParserConstants.STATE_ID_FACET;


public class Utilities {
	/**Name that should be used with genSym whenever creating an auotmatic label for a yield/map/fold operator.**/
	public static final String FRAME_SYM_PREFIX = "Frame";
	
	private static int gsCounter = 0;
	public static String genSym(String name) {
		if (gsCounter <0) {throw new Error("Exceed gensym guranteed namespace capacity.");}
		return "#" + name + "_" + gsCounter++;
	}
	
	
	/**Given a facet call, what is the equivalent query facet call?*/
	public static String queryName(String name) {return new MultiPartName(name).modSuffix(QUERY_FACET).toString();}

	public static List<AstInvokeable> gatherInvokeables(Tree root) {
		List<AstInvokeable> results = new LinkedList();
		
		for (int i=0; i< root.getChildCount(); i++) {
			Tree child = root.getChild(i); 
			if (child instanceof AstInvokeable) {
				results.add((AstInvokeable) child);
			} else {
				results.addAll(gatherInvokeables(child));
			}
		}
		return results;
	}
	
	/**Takes a tree and creates a suitable query for it.
	 * Query constructed per:
	 *    1) Gather invokeables
	 *    2) Retain only invokeables with operators that (a) are not functions and (b) have a StateQuery facet
	 *    3) Create a STATE_QUERY node with invokeables pointing to retained state query facets
	 */
	public static Tree stateQueryList(TreeAdaptor adaptor, Tree tree) {
		List<AstInvokeable> invokeables = gatherInvokeables(tree);
		Collection<AstInvokeable> targets = new HashSet();
		
		for (AstInvokeable inv: invokeables) {
			StencilOperator target = inv.getOperator();
			if (target != null) {
				if (target.getOperatorData().hasFacet(STATE_ID_FACET)) {
					AstInvokeable newInv = (AstInvokeable) adaptor.dupNode(inv);
					newInv.getToken().setText(target.getName());
					newInv.changeFacet(STATE_ID_FACET);
					targets.add(newInv);
				}
			}
		}

		Tree rv = (Tree) adaptor.create(STATE_QUERY, "STATE_QUERY");
		for (AstInvokeable target: targets) {
			adaptor.addChild(rv, target);					
		}		
		return rv;
	}
	
	public static String mapTreesToString(Map<TupleRef, Tree> subst) {
		StringBuilder b = new StringBuilder();
		for(TupleRef key: subst.keySet()) {
			b.append(key.toStringTree());
			b.append(":");
			b.append(subst.get(key).toStringTree());
			b.append(" --- ");
			b.append(key.hashCode());
			b.append("\n");
		}
		return b.toString();
	}
}
