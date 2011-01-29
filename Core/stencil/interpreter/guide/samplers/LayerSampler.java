package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.LayerView;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

/**Iterates the tuples of a layer as the sample.*/
public final class LayerSampler implements SampleOperator {
	/**Special seed operator to accompany this sampler type.*/
	public static final class SeedOperator implements stencil.interpreter.guide.SeedOperator {
		private final DisplayLayer layer;
		private final LayerView view;

		public SeedOperator(DisplayLayer l) {this(l, false);}
		private SeedOperator(DisplayLayer l, boolean viewpoint) {
			layer = l;
			view = viewpoint ? l.getView() : null; ;
		}
		
		public TuplePrototype getSamplePrototype() {return layer.getPrototype();}
		public SampleSeed getSeed() {return new SampleSeed(false);}
		public int stateID() {return (view != null) ? view.getStateID() : layer.getStateID();}
		public SeedOperator viewpoint() {
			return new SeedOperator(layer);
		}
	}
	
	private final DisplayLayer layer;
	
	public LayerSampler(DisplayLayer layer) {this.layer = layer;}
	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		List<Tuple> l = new ArrayList();
		for (Glyph t: getDisplayLayer().getView()) {l.add(t);}
		return l;
	}
	public final DisplayLayer<Glyph> getDisplayLayer() {return layer;}
}
