package stencil.parser.tree;

 import org.antlr.runtime.Token;

import stencil.parser.string.StencilParser;
import stencil.parser.tree.util.Environment;

public class Consumes extends StencilTree {
	public Consumes(Token source) {super(source);}

	public String getStream() {return token.getText();}
	
	public ContextNode getContext() {
		ContextNode result = (ContextNode) this.getAncestor(StencilParser.LAYER);
		if (result != null) {return result;}
		return (ContextNode) this.getAncestor(StencilParser.STREAM_DEF);
	}

	public List<List<Predicate>> getFilters() {return (List<List<Predicate>>) getChild(0);}
	public List<Rule> getPrefilterRules() {return (List<Rule>) getChild(1);}
	public List<Rule> getLocalRules() {return (List<Rule>) getChild(2);}
	public List<Rule> getResultRules() {return (List<Rule>) getChild(3);}
	public List<Rule> getViewRules() {return (List<Rule>) getChild(4);}
	public List<Rule> getCanvasRules() {return (List<Rule>) getChild(5);}

	/**Check that all filters match the passed environment.*/
	public boolean matches(Environment env) {
		//Check all of the filters
		int count=0;
		for (List<Predicate> predicates: getFilters()) {
			count++;
			try {if (!Predicate.matches(predicates, env)) {return false;}}
			catch (Exception e) {throw new RuntimeException(String.format("Error applying filter chain %1$d.", count), e);}
		}
		return true;
	}
}
