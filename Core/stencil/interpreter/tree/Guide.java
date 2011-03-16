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
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public class Guide implements UpdateableComposite<Guide> {
	private final Selector selector;
	private final String type;
	private final Rule rules;
	private final MonitorOperator<MonitorOperator> monitorOp;
	private final SampleOperator sampleOp;
	private final StateQuery stateQuery;
	private final Rule generator;	//Only has one rule, but it makes it easier to work with the interpreter
	private final Specializer spec;
	
	public Guide(Selector selector, String type, Rule rules,
			MonitorOperator monitorOp, SampleOperator sampleOp, StateQuery query,
			Rule generator, Specializer spec) {
		super();
		this.selector = selector;
		this.type = type;
		this.rules = rules;
		this.monitorOp = monitorOp;
		this.sampleOp = sampleOp;
		this.stateQuery = query;
		this.generator = generator;
		this.spec = spec;
	}

	public String type() {return type;}
	public StateQuery stateQuery() {return stateQuery;}
	public Selector selector() {return selector;}
	public Specializer specializer() {return spec;}
	
	public TuplePrototype resultsPrototype() {return rules.prototype();}

	//TODO: Allow something other than canvas to be passed...when guides can be declared on layers or views
	public void update(DisplayGuide guide, Rectangle2D bounds) {
		assert guide != null : "Null guide passed.";
		
		List<Tuple> sample, projection, results;

		if (monitorOp instanceof LayerSampler.MonitorOperator) {
			sample = sampleOp.sample(null, spec);
			projection = sample;
		} else {
			SampleSeed seed = monitorOp.getSeed();

			try {
				sample = sampleOp.sample(seed, spec);
				projection = processAll(sample, generator);
			} catch (Exception e) {throw new RuntimeException("Error creating guide sample.", e);}
		}

		try {results = processAll(projection, rules);}
		catch (Exception e) {throw new RuntimeException("Error formatting guide results.", e);}

		guide.setElements(results, bounds);
	}

	private static List<Tuple> processAll(List<Tuple> sources, Rule rule) throws Exception {
		List<Tuple> results = new ArrayList();
		for (Tuple source: sources) {
			results.add(Interpreter.processTuple(source, rule));
		}
		return results;
	}

	
	public Guide viewpoint() {
		return new Guide(selector, type, rules.viewpoint(), monitorOp.viewpoint(), sampleOp, stateQuery.viewpoint(), generator.viewpoint(), spec);
	}
}
