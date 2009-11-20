package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;

public class Guide extends StencilTree {
	public static final String SAMPLE= "sample";	//HACK: Magic constant .... TODO: How can we tie this back to something
	public static final String TICKS = "tickCount"; //HACK: Magic constant .... TODO: How can we tie this back to something
	
	public static final class SampleStrategy {
		public static final int AUTO_TICK_COUNT = 0;
		
		final String sampleType;
		final int tickCount;
		
		public SampleStrategy(Specializer spec) {
			if (spec.getMap().containsKey(SAMPLE)) {
				sampleType = spec.getMap().get(SAMPLE).getText();
			} else {sampleType = "CATEGORICAL";}		//HACK: This should be gotten from the guide type somehow...

			
			if (spec.getMap().containsKey(TICKS)) {
				tickCount = ((Number) spec.getMap().get(TICKS).getValue()).intValue();
			} else {
				tickCount = AUTO_TICK_COUNT;			//HACK: This should be gotten from the guide type somehow...
			}

		}
		protected SampleStrategy(Atom type, Atom ticks) {
			sampleType = type.getText();
			tickCount = Converter.toInteger(ticks.getValue());
		}
		
		public boolean isCategorical() {return sampleType.equals("CATEGORICAL");}
	}
	
	public Guide(Token token) {super(token);}
	

	public String getAttribute() {return token.getText();}
	public String getLayer() {return getChild(0).getText();}
	public String getGuideType() {return getChild(1).getText();}
	public Specializer getSpecializer() {return (Specializer) getChild(2);}
		
	/**Rules are applied to the generated tuples.*/
	public List<Rule> getRules() {return (List<Rule>) getChild(3);}

	/**The generator is used to create the guide descriptor.  It is linked
	 * to a particular layer's attribute definition.  The generator call group
	 * creates a set of tuples. To produce the guide, the rules are applied to
	 * each resulting tuple.
	 */
	public CallChain getGenerator() {
		if (this.getChildCount() < 5) {throw new RuntimeException(String.format("Generator not set yet for guide %1$s on layer %2$s.", getAttribute(), getLayer()));}
		return (CallChain) getChild(4);
	}

	
	
	/**Apply the rules of this guide to the tuple passed.
	 * The result is a format descriptor for the guide position corresponding to the source tuple.
	 */
	public Tuple apply(Tuple source) throws Exception {
		Tuple buffer = null;
		for (Rule rule: getRules()) {
			Tuple result = rule.apply(source);
			buffer = Tuples.merge(result, buffer);
		}
		return buffer;
	}
	
	public SampleStrategy strategy() {return new SampleStrategy(getSpecializer());}
}
