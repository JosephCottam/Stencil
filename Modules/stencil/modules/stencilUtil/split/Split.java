package stencil.modules.stencilUtil.split;

import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.types.Converter;

//TODO: Optimize zero-sized split and constant-value operator split
//TODO: Unlike most higher-order ops, this is a producer of new ops...maybe the op argument should come through the specializer...(well, a ref to it, requires two-phase operator construction)
@Operator(name= "Split", spec="[fields:0, ordered:\"#F\"]", defaultFacet="map")
@Description("Higher order operator for performing split/cross-tab summarization")
public class Split extends AbstractOperator.Statefull<Split> {
	/**Keys that compose a split descriptor in a specializer**/
	public static final String SPLIT_KEY = "fields";
	public static final String ORDERED_KEY = "ordered";

	private final OperatorCache opCache;
	
	public Split(OperatorData od, Specializer spec) {
		super(od);
		SplitDescriptor split = new SplitDescriptor(Converter.toInteger(spec.get(SPLIT_KEY)), Converter.toBoolean(spec.get(ORDERED_KEY)));
		opCache = split.isOrdered() ? new OrderedHelper(split) : new UnorderedHelper(split);
	}
	
	private Split(OperatorData od, OperatorCache cache) {
		super(od);
		this.opCache = cache;
	}	
	
	@Facet(memUse="WRITER", counterpart="query")
	public Object map(StencilOperator op, String facet, Object...args) {
		return opCache.doSplit(op, facet, args);
	}

	@Facet(memUse="READER")	
	public Object query(StencilOperator op, String facet, Object...args) {
		return opCache.querySplit(op, facet, args);
	}
	
	@Override
	@Facet(memUse="READER", prototype="(int VALUE)")
	public int stateID() {return opCache.stateID();}
	
	@Override
	public OperatorData getOperatorData() {return operatorData;}
	
	@Override
	public String getName() {return operatorData.name();}

	/**Removes the first item from the arguments array.
	 * The first argument is assumed to be the split key.*/
	//final because it is a utility method
	protected static final Object[] getArgs(int splitSize, Object...args) {
		Object[] newArgs = new Object[args.length-splitSize];
		System.arraycopy(args, splitSize, newArgs, 0, newArgs.length);
		return newArgs;
	}
	
	/**Returns the first item from the list.  This is assumed to be the split key.*/
	private static final String DIVIDER = "//";
	protected static final Object getKey(final int size, final Object... args) {
		if (size ==0 ) {return args[0];}
		StringBuilder b = new StringBuilder();
		for (int i=0; i< size; i++) {
			b.append(args[i]);
			b.append(DIVIDER);
		}
		return b.toString();
	}
	
	@Override
	public StencilOperator duplicate() {
		return new Split(operatorData, opCache.viewpoint()); 
	}
	
	@Override
	public Split viewpoint() {
		OperatorCache cache = opCache.viewpoint();
		return new Split(operatorData, cache);
	}		
}