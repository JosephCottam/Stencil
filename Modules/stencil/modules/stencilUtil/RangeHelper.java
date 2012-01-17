package stencil.modules.stencilUtil;

import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.FacetData;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.tuple.Tuple;
import stencil.types.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.String.format;


/**Wraps an operator with range support.
 * Keeps a copy of every argument set seen within the range
 * and re-invokes the operator with an argument list equal to concatenating
 * all in-range arguments.  
 * 
 * Warnings:  
 * 1) If using for a range with a static start, the memory consumption is linear with the number of tuples seen
 * 2) Assumes that an operator instance is used either in Query or Map context (not both).
 * 
 */


@Operator(name="Range", spec=RangeHelper.DEFAULT_SPECIALIZER, tags=stencil.module.util.OperatorData.HIGHER_ORDER_TAG)
@Description("Higher order operator for performing range-related summarization")
public abstract class RangeHelper implements StencilOperator<StencilOperator>, Cloneable {
	public static final String FACET_KEY = "facet";
	public static final String DEFAULT_SPECIALIZER = "[" + Range.RANGE_KEY + ": \"ALL\", " + FACET_KEY +": \"map\"]";
	
	private abstract static class RangeTarget implements Invokeable {
		final RangeHelper helper;
		final StencilOperator base;
		final String facetName;
		final boolean flatten;
		
		public RangeTarget(RangeHelper helper, StencilOperator base, String facetName, boolean flatten) {
			this.helper = helper;
			this.base = base;
			this.facetName = facetName;
			this.flatten = flatten;
		}
		
		public Tuple tupleInvoke(Object[] arguments) {
			return Converter.toTuple(invoke(arguments));
		}
		
		private Object _invoke(Object[][] sourceArgs) {
			if (flatten) {
				Invokeable inv = base.getFacet(facetName);
				return inv.invoke(flatten(sourceArgs));
			} else {
				StencilOperator op = base.duplicate();
				Invokeable inv = op.getFacet(facetName);
				
				Object result = null;
				for (int i=0; i<sourceArgs.length; i++) {
					result = inv.invoke(sourceArgs[i]);
				}
				return result;
			}
		}

		public static Object[] flatten(Object[][] values) {
			int size = 0;
			for (int i=0; i< values.length; i++) {
				size += values[i].length;
			}

			final Object[] result = new Object[size];
			int offset = 0;
			
			for (int i=0; i< values.length; i++) {
				for (int j=0; j< values[i].length;j++) {
					result[offset] = values[i][j];
					offset++;
				}
			}
			return result;
		}		
		
		public static class Query extends RangeTarget {
			public Query(RangeHelper helper, StencilOperator base, String facetName, boolean flatten) {super(helper, base, facetName, flatten);}
			public Query viewpoint() {return new Query(helper.viewpoint(), base.viewpoint(), facetName, flatten);}

			/** Ignores its arguments**/
			public Object invoke(Object[] args) {return super._invoke(helper.getCache());}
			@Override
			public String targetIdentifier() {return helper.getName();}
		}
		
		public static class Map extends RangeTarget {
			public Map(RangeHelper helper, StencilOperator base, String facetName, boolean flatten) {super(helper, base, facetName, flatten);}
			public Map viewpoint() {return new Map(helper.viewpoint(), base.viewpoint(), facetName, flatten);}
			public Object invoke(Object[] args) {return super._invoke(helper.updateCache(args));}
			public String targetIdentifier() {return helper.getName();}
		}
		
	}
	
	private static final class RelativeHelper extends RangeHelper {
		public RelativeHelper(Range range, StencilOperator operator, String facetName, boolean flatten) {
			super(range, operator, facetName, flatten);
			
			if (range.getStart() < range.getEnd()) {throw new IllegalArgumentException("Range ends before it starts: " + range.toString());}
			
			values = new ArrayList(range.getStart());
		}
		 
		protected Object[][] updateCache(Object... args) {
			//Rotate in the new values
			if (values.size() > range.getStart()) {values.remove(0);} //Range.start indicates the oldest value that needs to be remembered.  In an offset, this is the larger number
			values.add(args);
			stateID.incrementAndGet();
			return getCache();
		}
		
		protected Object[][] getCache() {
			//We can always start at 0, since that is the 'oldest' value, but we may
			//have an end that is not the end of the range we must remember
			//(e.g. the range arg was -10 to -5, just five items are returned but you need to remember 10)
			int endRange = values.size()-1 > range.getEnd() ? values.size()-1 - range.getEnd() : 0;
			Object[][] formals = values.subList(0, endRange).toArray(new Object[endRange][]);
			return formals;			
		}
	}
	
	private static final class AbsoluteHelper extends RangeHelper {
		private boolean trimmed = false;
		private int offsetCountdown;
		
		public AbsoluteHelper(Range range, StencilOperator operator, String facetName, boolean flatten) {
			super(range, operator, facetName, flatten);

			if (range.getStart() > range.getEnd()) {throw new IllegalArgumentException("Range ends before it starts: " + range.toString());}

			values = new ArrayList(range.getEnd());
			offsetCountdown = range.getStart();
		}
		
		protected Object[][] updateCache(Object... args) {
			if (offsetCountdown >0) {offsetCountdown--; return new Object[0][0];}
			
			if (values.size() < range.getEnd()) {
				values.add(args);
			} else if (!trimmed) {
				//A range with absolute indices on both start and end will eventually become a constant...
				//so we trim it to that constant value
				values = values.subList(0, range.getEnd()); //The endpoint is exclusive, but range is 1-based, so it all works out!
				trimmed = true;
			}
			stateID.incrementAndGet();
			return getCache();
		}
		
		protected Object[][] getCache() {return values.toArray(new Object[values.size()][]);}
	}
	
	private static final class HybridHelper extends RangeHelper {
		int offsetCountdown;
			
		public HybridHelper(Range range, StencilOperator operator, String facetName, boolean flatten) {
			super(range, operator, facetName, flatten);
			
			if (range.relativeStart()) {throw new RuntimeException("Hybrid ranges must have absolute start points.  Recieved range " + range.toString());}
			values = new ArrayList();
			offsetCountdown = range.getStart();
		}
		
		protected Object[][] updateCache(Object... args) {
			if (offsetCountdown >0) {offsetCountdown--; return new Object[0][0];}
			values.add(args);			
			stateID.incrementAndGet();
			return getCache();
		}
		
		protected Object[][] getCache() {
			return values.subList(0, range.getEnd()).toArray(new Object[range.getEnd()][]);
		}
	}
		
	protected List<Object[]> values; 
	protected final Range range;
	protected final OperatorData operatorData;
	private final StencilOperator baseOperator;
	private final String opFacetName;
	private boolean flatten;
	protected AtomicInteger stateID = new AtomicInteger();
	
	protected RangeHelper(Range range, StencilOperator operator, String facetName, boolean flatten) {
		this.baseOperator = operator;
		this.range = range;
		this.opFacetName = facetName;
		operatorData = ModuleDataParser.operatorData(RangeHelper.class, "StencilUtil");
		this.flatten = flatten;
	}

	public OperatorData getOperatorData() {return operatorData;}

	/**Get a facet on this range helper object.
	 * The returned facet will use the operator/facet specified at construction.
	 * If the operator/facet is not a function, then query is not a premited facetName to use here.
	 */
	public Invokeable getFacet(String facetName) {
		if (facetName.equals("map")) {
			return new RangeTarget.Map(this, baseOperator, opFacetName, flatten);			
		} else if (facetName.equals("query")) {
			return new RangeTarget.Query(this, baseOperator, opFacetName, flatten);
		} else if (facetName.equals(STATE_ID_FACET)) {
			return new ReflectiveInvokeable("stateID", this);
		}
		throw new IllegalArgumentException("Facet not known: " + facetName);
	}

	public String getName() {return baseOperator.getName() + "(Ranged)";}
	
	/**Updates the list storage according to the specified range.
	 * MUST UPDATE STATE_ID any time it is called!
	 * 
	 * @return List of arguments to actually invoke the underlying operator with.
	 */
	protected abstract Object[][] updateCache(Object... args);
	
	/**Gets a list of stored items according to the specified range.
	 * This is the analog of 'updateCache' but without mutating state.
	 * @return
	 */
	protected abstract Object[][] getCache();
	
	/**StateID indicating changes to the cache.*/
	@Facet(memUse="READER", prototype="(int VALUE)")
	public int stateID() {return stateID.intValue();}
	
	/**For convenient operator data construction.  Always throws an error.**/
	@Facet(memUse="OPAQUE")
	public void map(){throw new Error("Invoked method that should never be invoked.");}	
	
	/**For convenient operator data construction.  Always throws an error.**/
	@Facet(memUse="READER")
	public void query() {throw new Error("Invoked method that should never be invoked.");}
	
	public StencilOperator duplicate() {
		StencilOperator op = baseOperator.duplicate();
		return makeOperator(range, op, opFacetName);
	}
	
	public RangeHelper viewpoint() {
		try {return (RangeHelper) this.clone();}
		catch (Exception e) {throw new Error("Error creating viewpoint in range helper.");}
	}	
	
	/**Produce an operator that works over the requested range specification.
	 * 
	 * @param range  Range to operator over
	 * @param operator The base operator instance (also the default return value, if the range is simple) 
	 * @param baseFacet The name of the facet to be invoked by the range helper (must be a function)
	 * @return
	 */
	public static StencilOperator makeOperator(Specializer spec, StencilOperator operator) {
		Range range = new Range(spec.get(Range.RANGE_KEY));
		String facet = (String) spec.get(FACET_KEY);
		return makeOperator(range, operator, facet);
	}
	
	private static StencilOperator makeOperator(Range range, StencilOperator operator, String facetName) {
		OperatorData od = operator.getOperatorData();
		if (od.hasTag(StencilUtil.RANGE_OPTIMIZED_TAG)) {return operator;}
		boolean flatten = od.hasTag(StencilUtil.RANGE_FLATTEN_TAG);
		
		if (range.isSimple()) {return operator;}
		
		FacetData fd = od.getFacet(facetName);
		if (fd == null) {throw new IllegalArgumentException(format("Cannot construct range opertor with %1$s, facet %2$s does not exist.", operator.getName(), facetName));}
		if (!fd.mutative() && !flatten) {throw new IllegalArgumentException(format("Operator %1$s, facet `%2$s' is not mutative.  Automatic ranging does not make sense.", operator.getName(), facetName));}
		
		if (range.relativeStart() && range.relativeEnd()) {return new RelativeHelper(range, operator, facetName, flatten);}
		if (!range.relativeStart() && !range.relativeEnd()) {return new AbsoluteHelper(range, operator, facetName, flatten);}
		if (!range.relativeStart() && range.relativeEnd()) {return new HybridHelper(range, operator, facetName, flatten);}
		
		throw new RuntimeException("Unsupported paramter combinitation in range: " + range.toString());
	}
}
