package stencil.interpreter.tree;


import java.util.ArrayList;
import java.util.List;

import stencil.display.Display;
import stencil.display.DisplayGuide;
import stencil.interpreter.Interpreter;
import stencil.interpreter.Viewpoint;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.SeedOperator;
import stencil.interpreter.guide.samplers.LayerSampler;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TupleFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

public class Guide implements Viewpoint<Guide> {
	private final Selector selector;
	private final String type;
	private final Rule[] rules;
	private final SeedOperator seedOp;
	private final SampleOperator sampleOp;
	private final StateQuery query;
	private final Rule[] generator;	//Only has one rule, but it makes it easier to work with the interpreter
	private final Specializer spec;
	
	public Guide(Selector selector, String type, Rule[] rules,
			SeedOperator seedOp, SampleOperator sampleOp, StateQuery query,
			Rule generator, Specializer spec) {
		super();
		this.selector = selector;
		this.type = type;
		this.rules = rules;
		this.seedOp = seedOp;
		this.sampleOp = sampleOp;
		this.query = query;
		this.generator = new Rule[]{generator};
		this.spec = spec;
	}

	public String type() {return type;}
	public StateQuery stateQuery() {return query;}
	public Selector selector() {return selector;}
	public Specializer specializer() {return spec;}
	
	public TuplePrototype resultsPrototype() {
		//TODO: Calc this is the tree, just store it as a StencilTree of the TuplePrototype variety and freeze at guide creation time
		List<TupleFieldDef> defs = new ArrayList();
		for (Rule rule: rules) {
		    TuplePrototype<TupleFieldDef> targetPrototype = rule.prototype();

			for (TupleFieldDef def: targetPrototype.fields()) {
				defs.add(def);
			}
		}
		return new SimplePrototype(TuplePrototypes.getNames(defs), TuplePrototypes.getTypes(defs));
	}

	public void update(DisplayGuide guide) {
		assert guide != null : "Null guide passed.";
		
		List<Tuple> sample, projection, results;

		if (seedOp instanceof LayerSampler.SeedOperator) {
			sample = sampleOp.sample(null, spec);
			projection = sample;
		} else {
			SampleSeed seed = seedOp.getSeed();

			try {
				sample = sampleOp.sample(seed, spec);
				projection = processAll(sample, generator);
			} catch (Exception e) {throw new RuntimeException("Error creating guide sample.", e);}
		}

		try {results = processAll(projection, rules);}
		catch (Exception e) {throw new RuntimeException("Error formatting guide results.", e);}

		//TODO: Make this get something other than canvas...when guides can be declared on layers or views
		//TODO: Fix the object creation order so the canvas exists before the updater is started
		if (Display.canvas != null) {
			guide.setElements(results, Display.canvas.getComponent().getContentBounds(false));
		}
	}

	private static List<Tuple> processAll(List<Tuple> sources, Rule[] rules) throws Exception {
		List<Tuple> results = new ArrayList();
		for (Tuple source: sources) {
			results.add(Interpreter.processTuple(source, rules));
		}
		return results;
	}

	
	public Guide viewpoint() {
		SeedOperator  sdr = seedOp.viewpoint();
		final Rule[] vpr = new Rule[rules.length];
		for (int i=0; i<vpr.length; i++) {vpr[i] = rules[i].viewpoint();}
		return new Guide(selector, type, vpr, sdr, sampleOp, query.viewpoint(), generator[0].viewpoint(), spec);
	}
}
