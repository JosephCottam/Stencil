package stencil.parser.tree;

import org.antlr.runtime.Token;

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
	
	public String getGuideType() {return getChild(0).getText();}
	public Specializer getArguments() {return (Specializer) getChild(1);}
	public String getAttribute() {return getChild(2).getText();}
	public CallGroup getAction() {return (CallGroup) getChild(3);}
	
	public SampleStrategy strategy() {
		return new SampleStrategy(getArguments());
	}
}
