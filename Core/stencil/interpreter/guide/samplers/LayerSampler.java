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
	public static final class MonitorOperator implements stencil.interpreter.guide.MonitorOperator {
		private final DisplayLayer layer;
		private final LayerView view;

		public MonitorOperator(DisplayLayer l) {this(l, false);}
		private MonitorOperator(DisplayLayer l, boolean viewpoint) {
			layer = l;
			view = viewpoint ? l.viewpoint() : null;
		}
		
		public TuplePrototype getSamplePrototype() {return layer.getPrototype();}
		public SampleSeed getSeed() {throw new UnsupportedOperationException("Should never seek the seed of a layer sampler.");}
		public int stateID() {return (view != null) ? view.getStateID() : layer.getStateID();}
		public MonitorOperator viewpoint() {
			return new MonitorOperator(layer);
		}
	}
	
	private final DisplayLayer layer;
	
	public LayerSampler(DisplayLayer layer) {this.layer = layer;}
	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		List<Tuple> l = new ArrayList();
		for (Glyph t: getDisplayLayer().viewpoint()) {l.add(t);}
		return l;
	}
	public final DisplayLayer<Glyph> getDisplayLayer() {return layer;}
}
