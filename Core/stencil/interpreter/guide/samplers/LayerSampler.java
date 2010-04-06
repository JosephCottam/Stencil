package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.display.DisplayLayer;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.parser.tree.Layer;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public final class LayerSampler implements SampleOperator {
	/**Special seed operator to accompany this sampler type.*/
	public static final class SeedOperator implements stencil.interpreter.guide.SeedOperator {
		private final Layer layerDef;
		public SeedOperator(Layer l) {layerDef = l;}
		private final DisplayLayer<Tuple> getDisplayLayer() {return layerDef.getDisplayLayer();}

		public TuplePrototype getSamplePrototype() {
			return getDisplayLayer().getPrototype();
		}

		public SampleSeed getSeed() {return new SampleSeed(false);}

		public int stateID() {return getDisplayLayer().getStateID();}		
	}
	
	private final Layer layerDef;
	
	public LayerSampler(Layer layer) {
		this.layerDef = layer;
	}

	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		List<Tuple> l = new ArrayList();
		for (Tuple t: getDisplayLayer()) {l.add(t);}
		return l;
	}
	
	private final DisplayLayer<Tuple> getDisplayLayer() {return layerDef.getDisplayLayer();}
}
