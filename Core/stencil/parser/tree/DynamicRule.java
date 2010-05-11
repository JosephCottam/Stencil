package stencil.parser.tree;

import java.util.Arrays;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import stencil.interpreter.Interpreter;
import stencil.parser.string.StencilParser;
import stencil.tuple.Tuple;

public class DynamicRule extends StencilTree {
	public DynamicRule(Token token) {super(token);}

	public Rule getAction() {return (Rule) getChild(0);}

	/**What are the state facets of this query.*/
	public StateQuery getStateQuery() {return (StateQuery) getChild(1);}
	
	/**What group does this rule belong to?*/
	public Consumes getGroup() {
		Tree t = this.getAncestor(StencilParser.CONSUMES);
		if (t == null) {throw new RuntimeException("Rules not part of a layer do not belong to a group.");}
		return (Consumes) t;
	}
	
	/**Calculate a result for each input.  Result set will be in the same order as the input set.*/
	public java.util.List<Tuple> apply(java.util.List<Tuple> inputs) throws Exception {
		//TODO: Convert to a data-state model invocation
		final Tuple[] results = new Tuple[inputs.size()];
		final Rule rule = getAction();
		for (int i=0; i< results.length; i++) {
			results[i] = Interpreter.process(inputs.get(i), rule);
		}
		return Arrays.asList(results);
	}
		
	/**Should this dynamic rule be run now??*/
	public boolean requiresUpdate() {
		return getStateQuery().requiresUpdate();
	}
}
