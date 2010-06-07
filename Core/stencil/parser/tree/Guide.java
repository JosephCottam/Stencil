package stencil.parser.tree;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.Token;

import stencil.interpreter.guide.*;
import stencil.module.operator.util.Invokeable;
import stencil.parser.string.StencilParser;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import static stencil.parser.string.StencilParser.STATE_QUERY;

public class Guide extends StencilTree {
	private SeedOperator seedOperator;
	private SampleOperator sampleOperator;
	
	public Guide(Token token) {super(token);}

	public Selector getSelector() {return (Selector) getFirstChildWithType(StencilParser.SELECTOR);}
	
	/**What type of guide needs to be created?*/
	public String getGuideType() {return getFirstChildWithType(StencilParser.ID).getText();}
	
	/**What are the specializer arguments?*/
	public Specializer getSpecializer() {return (Specializer) this.getFirstChildWithType(StencilParser.SPECIALIZER);}
		
	/**Rules are applied to the generated tuples.*/
	public List<Rule> getRules() {return (List)	this.getFirstChildWithType(StencilParser.LIST);}

	public SeedOperator getSeedOperator() {return seedOperator;}
	public void setSeedOperator(Invokeable inv) {
		seedOperator = (SeedOperator) inv.getTarget();
	}

	public SampleOperator getSampleOperator() {return sampleOperator;}
	public void setSampleOperator(SampleOperator op) {
		sampleOperator = op;
	}

	
	/**The generator is used to create the guide descriptor.  It is linked
	 * to a particular layer's attribute definition.  The generator call group
	 * creates a set of tuples. To produce the guide, the rules are applied to
	 * each resulting tuple.
	 */
	public Rule getGenerator() {
		if (this.getChildCount() < 5) {throw new RuntimeException(String.format("Generator not set yet for guide %1$s on layer %2$s.", getSelector().toString()));}
		return (Rule) getChild(4).getChild(0);
	}
	
	public StencilTree getStateQuery() {
		return (StencilTree) this.findChild(STATE_QUERY, null);
	}
	
	public String toString() {
		String base = super.toString();
		if (seedOperator == null) {base = base + " -missingSeed";}
		if (sampleOperator == null) {base = base + " -missingSample";}
		return base;
	}
	
	public Guide dupNode() {
		Guide g = (Guide) super.dupNode();
		g.seedOperator = seedOperator;
		g.sampleOperator = sampleOperator;
		return g;
	}
	
	
	public TuplePrototype getPrototype() {
		List<Rule> rules = getRules();
		List<String> names = new ArrayList();
		
		for (Rule r: rules) {
			String[] targets = TuplePrototypes.getNames(r.getTarget().getPrototype());
			for (String target: targets) {names.add(target);}
		}
		
		return new SimplePrototype(names);
	}
}
