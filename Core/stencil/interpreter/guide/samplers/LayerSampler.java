package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.LayerView;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.UnknownFacetException;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.parser.ParserConstants;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;


/**Iterates the tuples of a layer as the sample.*/
public class LayerSampler implements SampleOperator {
	/**Special seed operator to accompany this sampler type.*/
	@Operator(name="LayerMonitor", defaultFacet="stateID")
	public static final class MonitorOperator implements stencil.interpreter.guide.MonitorOperator {
		private static final OperatorData OPERATOR_DATA = ModuleDataParser.operatorData(MonitorOperator.class, ParserConstants.STAND_IN_GROUP);
		
		private final DisplayLayer layer;
		private final LayerView view;

		public MonitorOperator(DisplayLayer l) {this(l, false);}
		private MonitorOperator(DisplayLayer l, boolean viewpoint) {
			layer = l;
			view = viewpoint ? l.viewpoint() : null;
		}

		@Facet(memUse="READER")
		public int stateID() {return (view != null) ? view.stateID() : layer.stateID();}

		
		public TuplePrototype getSamplePrototype() {return layer.prototype();}
		public SampleSeed getSeed() {throw new UnsupportedOperationException("Should never seek the seed of a layer sampler.");}
		public MonitorOperator viewpoint() {
			return new MonitorOperator(layer);
		}
		
		@Override
		public Invokeable getFacet(String facet) throws UnknownFacetException {
			if (STATE_ID_FACET.equals(facet)) {
				return new ReflectiveInvokeable("stateID",this);
			}
			throw new IllegalArgumentException("Only stateID facet known on Layer Sampler Monitor");
		}
		
		@Override
		public OperatorData getOperatorData() {return OPERATOR_DATA;}

		@Override
		public String getName() {return OPERATOR_DATA.name();}

		@Override
		public StencilOperator duplicate() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}
	}
	
	protected final DisplayLayer layer;
	
	public LayerSampler(DisplayLayer layer) {this.layer = layer;}
	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		List<Tuple> l = new ArrayList();
		TableView view = ((Table) layer).tenured();
		for (Glyph t: view) {l.add(t);}
		return l;
	}
	
	public TuplePrototype prototype() {return layer.prototype();}
}
