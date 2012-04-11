package stencil.modules.stencilUtil;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
import stencil.tuple.instances.MultiResultTuple;


//TODO: Implement separate instance class if there is a constant operator being passed in
@Operator(name="Map", defaultFacet="map")
@Description("Higher order operator for applying an opertor to each value of a tuple.")
public class Map extends AbstractOperator {
	public Map(OperatorData od) {super(od);}

	
	@Facet(memUse="WRITER", counterpart="query")
	public MultiResultTuple map(StencilOperator op, String facet, Object... arguments) {
		return execute(op, facet, arguments);
	}
	
	@Facet(memUse="READER")
	public MultiResultTuple query(StencilOperator op, String facet, Object... arguments) {
		facet = op.getOperatorData().getFacet(facet).counterpart();
		return execute(op, facet, arguments);
	}	
	
	private MultiResultTuple execute(StencilOperator op, String facet, Object... arguments) {
		Invokeable inv = op.getFacet(facet);
		Tuple t = (Tuple) arguments[0];
		Tuple[] results = new Tuple[t.size()];
		final int size = t.size();
		for (int i=0; i<size; i++) {
			Object[] innerArgs = new Object[arguments.length];	//To be passed to the op-as-arg
			for (int arg=0; arg<arguments.length; arg++) {
				if (arguments[arg] instanceof Tuple) {
					innerArgs[arg] = ((Tuple) arguments[arg]).get(i);
				} else {
					innerArgs[arg] = arguments[arg];
				}
			}
			
			results[i] = inv.tupleInvoke(innerArgs);
		}
		return new MultiResultTuple(results);		
	}
	
	public Map duplicate() throws UnsupportedOperationException {return this;}
}
