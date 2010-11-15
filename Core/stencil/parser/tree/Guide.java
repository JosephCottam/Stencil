package stencil.parser.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.Token;

import stencil.display.Display;
import stencil.display.DisplayGuide;
import stencil.interpreter.Interpreter;
import stencil.interpreter.guide.*;
import stencil.interpreter.guide.samplers.LayerSampler;
import stencil.module.operator.util.Invokeable;
import stencil.parser.string.StencilParser;
import stencil.tuple.Tuple;
import static stencil.parser.string.StencilParser.STATE_QUERY;

public class Guide extends StencilTree {
	private SeedOperator seedOperator;
	private SampleOperator sampleOperator;
	
	public Guide(Token token) {super(token);}

	/**What is this guide associated with?  
	 * This is a path to some other part of the program.
	 * Additionally, the selector indicates what attribute to display the results on.
	 */
	public Selector getSelector() {return (Selector) getFirstChildWithType(StencilParser.SELECTOR);}
	
	/**What type of guide needs to be created (axis, sidebar, etc)?*/
	public String getGuideType() {return getFirstChildWithType(StencilParser.ID).getText();}
	
	/**What are the specializer arguments?*/
	public Specializer getSpecializer() {return (Specializer) this.getFirstChildWithType(StencilParser.SPECIALIZER);}
		
	/**Rules are applied to the generated tuples.
	 * This is post-processing of the raw results; 
	 * useful for controlling formatting, double-encoding on the guide or
	 * highlighting on the guide itself.
	 * */
	public List<Rule> getRules() {return (List)	this.getFirstChildWithType(StencilParser.LIST);}

	/**The seed operator creates the basis for the sample.
	 * For direct guides, it is shared with the rule indicated in the selector.
	 * For summarization guides, it is a link to the selected data group.
	 */
	public SeedOperator getSeedOperator() {return seedOperator;}
	public void setSeedOperator(Invokeable inv) {
		seedOperator = (SeedOperator) inv.getTarget();
	}

	/**Given a seed, the sample operator produces a list of input values
	 * for the generator.
	 */
	public SampleOperator getSampleOperator() {return sampleOperator;}
	public void setSampleOperator(SampleOperator op) {
		sampleOperator = op;
	}

	
	/**The generator is used to create the guide descriptor.
	 * The generator creates a set of input/output values where
	 * the input is provided by the sample operator and the output
	 * is the resulting value from the generator. 
	 */
	public Rule getGenerator() {
		if (this.getChildCount() < 5) {throw new RuntimeException(String.format("Generator not set yet for guide %1$s.", getSelector().toString()));}
		return (Rule) getChild(4).getChild(0);
	}
	
	public StateQuery getStateQuery() {
		return (StateQuery) getFirstChildWithType(STATE_QUERY);
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
		
	public void update(DisplayGuide guide) {
		assert guide != null : "Null guide passed.";
		
		Specializer details = getSpecializer();
		SeedOperator seedOp = getSeedOperator();
		List<Tuple> sample, projection, results;

		if (seedOp instanceof LayerSampler.SeedOperator) {
			sample = getSampleOperator().sample(null, details);
			projection = sample;
		} else {
			SampleSeed seed = seedOp.getSeed();

			try {
				sample = getSampleOperator().sample(seed, details);
				projection = processAll(sample, Arrays.asList(getGenerator()));
			} catch (Exception e) {throw new RuntimeException("Error creating guide sample.", e);}
		}

		try {results = processAll(projection, getRules());}
		catch (Exception e) {throw new RuntimeException("Error formatting guide results.", e);}

		//TODO: Make this get something other than canvas...when guides can be declared on layers or views
		//TODO: Fix the object creation order so the canvas exists before the updater is started
		if (Display.canvas != null) {
			guide.setElements(results, Display.canvas.getComponent().getContentBounds(false));
		}
	}
		
	private List<Tuple> processAll(List<Tuple> sources, List<Rule> rules) throws Exception {
		List<Tuple> results = new ArrayList();
		for (Tuple source: sources) {
			results.add(Interpreter.processSequential(source, rules));
		}
		return results;
	}
}
