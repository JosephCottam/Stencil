package stencil.interpreter.tree;


import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import stencil.display.DisplayGuide;
import stencil.interpreter.Interpreter;
import stencil.interpreter.UpdateableComposite;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.MonitorOperator;
import stencil.interpreter.guide.samplers.LayerSampler;
import stencil.interpreter.guide.samplers.NumericSampler;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

public class Guide implements UpdateableComposite<Guide> {
	private final String identifier;
	private final String type;
	private final Specializer selectors;
	private final Rule rule;
	private final MonitorOperator<MonitorOperator>[] monitorOps;
	private final SampleOperator[] sampleOps;
	private final StateQuery stateQuery;
	private final Rule[] generators;
	private final Specializer spec;
	
	public Guide(String identifier, String type, Specializer selectors, Rule rule,
			MonitorOperator[] monitorOp, SampleOperator[] sampleOps, StateQuery query,
			Rule[] generators, Specializer spec) {
		super();
		this.identifier = identifier;
		this.type = type;
		this.rule = rule;
		this.selectors = selectors;
		this.monitorOps = monitorOp;
		this.sampleOps = sampleOps;
		this.stateQuery = query;
		this.generators = generators;
		this.spec = spec;
	}

	/**Axis, legend, trend-line, etc.**/
	public String type() {return type;}
	
	/**Which fields are displayed (and what are their sample profiles)**/
	public Specializer selectors() {return selectors;}
	
	public StateQuery stateQuery() {return stateQuery;}
	public String identifier() {return identifier;}
	public Specializer specializer() {return spec;}
	public Rule rule() {return rule;}
	public Rule[] generators() {return generators;}
	
	public boolean isNumeric() {
		boolean numeric = true;
		for (SampleOperator op: sampleOps) {
			numeric = numeric && op instanceof NumericSampler;
		}
		return numeric;
	}
	
	public void update(DisplayGuide guide, Rectangle2D bounds) {
		assert guide != null : "Null guide passed.";
		
		List<? extends Tuple> projection;
		List<PrototypedTuple> results;

		if (monitorOps[0] instanceof LayerSampler.MonitorOperator) {
			projection = sampleOps[0].sample(null, spec);
		} else {
			List<PrototypedTuple>[] projections = new List[monitorOps.length];
			for (int i=0; i<monitorOps.length; i++) {
				SampleSeed seed = monitorOps[i].getSeed();
				List<Tuple> sample = sampleOps[i].sample(seed, spec);
				try {projections[i] = processAll(sample, generators[i]);}
				catch (Exception e) {throw new RuntimeException("Error creating guide sample.", e);}

			}
			projection = cross(projections);			
		}

		try {results = processAll(projection, rule);}
		catch (Exception e) {throw new RuntimeException("Error formatting guide results.", e);}

		guide.setElements(results, bounds);
	}

	private static List<PrototypedTuple> processAll(List<? extends Tuple> sources, Rule rule) throws Exception {
		List<PrototypedTuple> results = new ArrayList();
		for (Tuple source: sources) {
			results.add((PrototypedTuple) Interpreter.processTuple(source, rule));
		}
		return results;
	}

	/**Calculates a cross product of the samples passed.
	 * The cross product will have one value from each of the samples list and cover all combinations.
	 * 
	 * The result is NOT guaranteed to be memory independent of the input.  
	 * 
	 * @param samples
	 * @return
	 */
	private static List<PrototypedTuple> cross(List<PrototypedTuple>[] samples) {
		if (samples.length ==1) {return samples[0];}
		if (samples.length>2) {throw new UnsupportedOperationException("Can only cross one or two values, given " + samples.length);}
		
		List<PrototypedTuple> sample = new ArrayList();
		for (PrototypedTuple t1: samples[0]) {
			for (PrototypedTuple t2: samples[1]) {
				sample.add(Tuples.merge(t1,t2));
			}
		}
		return sample;
	}
	
	
	public Guide viewpoint() {		
		final MonitorOperator<MonitorOperator>[] movp = new MonitorOperator[monitorOps.length];
		final Rule[] genp = new Rule[generators.length];
		for (int i=0; i< movp.length; i++) {
			movp[i] = monitorOps[i].viewpoint();
			genp[i] = generators[i].viewpoint();
		}
		
		return new Guide(identifier, type, selectors, rule.viewpoint(), movp, sampleOps, stateQuery.viewpoint(), genp, spec);
	}
}
