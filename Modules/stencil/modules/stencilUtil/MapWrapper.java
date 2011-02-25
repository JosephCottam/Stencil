package stencil.modules.stencilUtil;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.UnknownFacetException;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.MethodInvokeFailedException;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
import stencil.tuple.instances.MapMergeTuple;
import stencil.types.Converter;

@Operator(name="Map", tags=stencil.module.util.OperatorData.HIGHER_ORDER_TAG)
@Description("Higher order operator for applying an opertor to each value of a tuple.")
public class MapWrapper implements StencilOperator {
	private static final class MapInvokeable implements Invokeable {
		private final Invokeable inv;
		private final MapWrapper wrapper;
		private final String name;
		
		public MapInvokeable(MapWrapper wrapper, String name) {
			this.wrapper = wrapper;
			this.inv = wrapper.op.getFacet(name);
			this.name = name;
		}
		
		public Object getTarget() {return wrapper;}

		public Object invoke(Object[] arguments) throws MethodInvokeFailedException {
			assert arguments[0] instanceof Tuple;
			assert arguments.length == 1;

			Tuple t = (Tuple) arguments[0];
			Tuple[] results = new Tuple[t.size()];
			final int size = t.size();
			for (int i=0; i<size; i++) {results[i] = inv.tupleInvoke(new Object[]{t.get(i)});}
			return new MapMergeTuple(results);
		}

		public Tuple tupleInvoke(Object[] arguments) throws MethodInvokeFailedException {
			return Converter.toTuple(invoke(arguments));
		}

		public Invokeable viewpoint() {
			return new MapInvokeable(wrapper.viewpoint(), name);
		}

		@Override
		public String targetIdentifier() {return wrapper.getName();}
	}
	
	private final StencilOperator op;
	public MapWrapper(StencilOperator op) {this.op =op;}
	
	public Invokeable getFacet(String facet) throws UnknownFacetException {
		if (facet.equals(STATE_ID_FACET)) {
			return op.getFacet(STATE_ID_FACET);
		} else {
			return new MapInvokeable(this, facet);
		}
	}
	
	public OperatorData getOperatorData() {return op.getOperatorData();}
	public String getName() {return op.getName() + "#Map";}
	public MapWrapper viewpoint() {return new MapWrapper(op.viewpoint());}
	public MapWrapper duplicate() throws UnsupportedOperationException {throw new UnsupportedOperationException("Duplicate not supported for map.");}
}