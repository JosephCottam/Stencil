package stencil.parser.tree;

 import org.antlr.runtime.Token;

import stencil.parser.string.StencilParser;
import stencil.parser.tree.util.Environment;

public class Consumes extends StencilTree {
	private static final int FILTERS   = 0;
	private static final int PREFILTER = 1;
	private static final int LOCAL     = 2;
	private static final int RESULT    = 3;
	private static final int VIEW      = 4;
	private static final int CANVAS    = 5;
	private static final int DYNAMIC   = 6;
	
	public Consumes(Token source) {super(source);}

	public String getStream() {return token.getText();}
	
	public ContextNode getContext() {
		ContextNode result = (ContextNode) this.getAncestor(StencilParser.LAYER);
		if (result != null) {return result;}
		return (ContextNode) this.getAncestor(StencilParser.STREAM_DEF);
	}

	public List<List<Predicate>> getFilters() {return (List<List<Predicate>>) getChild(FILTERS);}
	public List<Rule> getPrefilterRules() {return (List<Rule>) getChild(PREFILTER);}
	public List<Rule> getLocalRules() {return (List<Rule>) getChild(LOCAL);}
	public List<Rule> getResultRules() {return (List<Rule>) getChild(RESULT);}
	public List<Rule> getViewRules() {return (List<Rule>) getChild(VIEW);}
	public List<Rule> getCanvasRules() {return (List<Rule>) getChild(CANVAS);}
	public List<DynamicRule> getDynamicRules() {return (List<DynamicRule>) getChild(DYNAMIC);}

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
