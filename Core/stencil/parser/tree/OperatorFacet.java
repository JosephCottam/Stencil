package stencil.parser.tree;


import java.util.List;
import org.antlr.runtime.Token;

import stencil.parser.string.StencilParser;

/**Single facet to a synthetic operator.*/
public final class OperatorFacet extends StencilTree {
	public OperatorFacet(Token token) {super(token);}

	public String getName() {return token.getText();}
	
	/**What is the prototype pair?*/
	private Yields getYields() {return (Yields) this.getFirstChildWithType(StencilParser.YIELDS);}
	public TuplePrototype getArguments() {return getYields().getInput();}
	public TuplePrototype getResults() {return getYields().getOutput();}
	
	
	public List<OperatorRule> getRules() {
		return (List<OperatorRule>) findChild(StencilParser.LIST, "Rules");
	}
	
	public List<Rule> getPrefilterRules() {
		return (List<Rule>) findChild(StencilParser.LIST, "Prefilters");
	}
}
