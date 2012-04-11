package stencil.modules.stencilUtil.range;

import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;

@Operator(name="Range", spec=Range.DEFAULT_SPECIALIZER)
@Description("Higher order operator for performing range-related summarization")
public class Range extends AbstractOperator<Range> {
	public static final String RANGE_KEY = "range";
	public static final String DEFAULT_SPECIALIZER = "[" + Range.RANGE_KEY + ": \"ALL\"]";
	
	final CacheHelper cache;
	
	public Range(OperatorData opData, Specializer spec) {
		this(opData, new RangeDescriptor(spec.get(RANGE_KEY)));
	}

	protected Range(OperatorData opData, RangeDescriptor range) {
		this(opData, CacheHelper.make(range));
	}

	protected Range(OperatorData opData, CacheHelper cache) {
		super(opData);
		this.cache = cache;
	}

	//TODO: Change this to OPAQUE then institute an operator-type-update protocol to enable this to be changed if the operator/facet pair is static and not opaque
	@Facet(memUse="WRITER", counterpart="query")
	public Object map(StencilOperator op, String facet, Object...args) {
		Object[][] rangedArgs = cache.update(args);
		return execute(op, facet, rangedArgs);
	}
	
	//TODO: Institute an operator-type-update protocol to enable this to be changed if the operator/facet pair is static and not opaque
	@Facet(memUse="READER")
	public Object query(StencilOperator op, String facet, Object...args) {
		Object[][] rangedArgs = cache.examine();
		facet = op.getOperatorData().getFacet(facet).counterpart();
		return execute(op, facet, rangedArgs);
	}
	
	private Object execute(StencilOperator op, String facet, Object[][] rangedArgs) {
		Invokeable inv = op.getFacet(facet);
		
		Object[] finalArgs = maybeFlatten(op, facet, rangedArgs);
		return inv.invoke(finalArgs);		
	}
	
	private static Object[] maybeFlatten(StencilOperator op, String facet, Object[][] rangedArgs) {
		if (!op.getOperatorData().getFacet(facet).mutative()) {
			return CacheHelper.flatten(rangedArgs);
		} 
		return rangedArgs;
	}
	
	@Facet(memUse="READER", prototype="(int VALUE)")
	public int stateID() {return cache.stateID.get();}
	
	public Range duplicate() {
		return new Range(this.operatorData, cache.range);
	}
	
	public Range viewpoint() {
		return new Range(this.operatorData, cache.viewpoint());
	}
}
