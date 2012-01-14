package stencil.modules.stencilUtil;

import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.MethodInvokeFailedException;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import stencil.module.util.ann.*;
import stencil.tuple.Tuple;
import stencil.types.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Operator(name= "Split", spec="[fields:0, ordered:\"#F\"]", tags=stencil.module.util.OperatorData.HIGHER_ORDER_TAG)
@Description("Higher order operator for performing split/cross-tab summarization")
public abstract class SplitHelper implements StencilOperator {
	private static final Object[] EMPTY_ARGS = new Object[0];

	/**Keys that compose a split descriptor in a specializer**/
	public static final String SPLIT_KEY = "fields";
	public static final String ORDERED_KEY = "ordered";

	
	/**Handle unordered split cases (default case).*/
	public static class UnorderedHelper extends SplitHelper {
		protected Map<Object, StencilOperator> operators = new HashMap();
		
		public UnorderedHelper(Split split, StencilOperator operator) {super(split, operator);}
		
		public Object doSplit(String facet, Object[] args) {
			Object key = getKey(split.size(), args);
			Object[] newArgs = getArgs(split.size(), args);
			StencilOperator op = getOp(key);
			Invokeable inv = op.getFacet(facet);
			
			//Monitor stateID of the invoked operator
			Invokeable stateIDFacet = op.getFacet(STATE_ID_FACET);
			int oldID = Converter.toInteger(stateIDFacet.invoke(new Object[0]));

			Object rv = inv.invoke(newArgs);
			//System.out.println(key + ":" + facet + ":" + rv);
			
			int newID = Converter.toInteger(stateIDFacet.invoke(new Object[0]));
				if (newID != oldID) {stateID++;}
			return rv;
		}
				
		/**Check if the key has been seen before.
		 * Return the operator used on that occasion if it 
		 * has been, otherwise create a new base-operator duplicate.
		 * 
		 * @param key How to determine which operator replicate to use
		 * @return Operator to invoke for the given arguments
		 */
		private StencilOperator getOp(Object key) {
			if (!operators.containsKey(key)) {
				operators.put(key, operator.duplicate());
				stateID++;
			}
			return operators.get(key);
		}

		@Facet(memUse="READER", prototype="(int VALUE)")
		public int stateID() {
			Integer[] ids = new Integer[operators.size()];
			int i=0;
 			for (StencilOperator op: operators.values()) {
				ids[i] = ((Integer) op.getFacet(STATE_ID_FACET).invoke(EMPTY_ARGS));
			}
 			return stateID + Arrays.deepHashCode(ids);
		}

		
		public StencilOperator viewpoint() {
			UnorderedHelper nop = new UnorderedHelper(split, operator);
			for (Object key: operators.keySet()) {
				StencilOperator op = operators.get(key);
				StencilOperator view = op.viewpoint();
				nop.operators.put(key, view);
			}
			return nop;
		}
		
	}
	
	/**Handle ordered split cases in a more memory efficient way.*/
	public static class OrderedHelper extends SplitHelper {
		private Object oldKey;
		
		public OrderedHelper(Split split, StencilOperator operator) {
			super(split, operator);
		}
				
		public Object doSplit(String facet, Object[] args) {
			Object key = getKey(split.size(), args);
			Object[] newArgs = getArgs(split.size(), args);
			if (!key.equals(oldKey)) {
				try {operator = operator.duplicate();}
				catch (Exception e) {throw new Error("Error creating new split operator instance.", e);}
				oldKey = key;
				stateID++;
			}
			Invokeable inv = operator.getFacet(facet);
			return inv.invoke(newArgs);
		}

		public StencilOperator viewpoint() {
			OrderedHelper nop = new OrderedHelper(super.split, super.operator);
			nop.oldKey = this.oldKey;
			return nop;
		}

		@Facet(memUse="READER", prototype="(int VALUE)")
		public int stateID() {return stateID + ((Integer) operator.getFacet(STATE_ID_FACET).invoke(EMPTY_ARGS));}

	}
	
	private static class SplitTarget implements Invokeable {
		final SplitHelper helper;
		final String facet;
		
		public SplitTarget(SplitHelper helper, String facet) {
			this.helper = helper;
			this.facet = facet;
		}
		
		public Tuple tupleInvoke(Object[] arguments) {
			return Converter.toTuple(invoke(arguments));
		}
		public Object invoke(Object[] arguments)
				throws MethodInvokeFailedException {
			return helper.doSplit(facet, arguments);
		}
		
		public SplitTarget viewpoint() {
			return new SplitTarget((SplitHelper) helper.viewpoint(), facet);
		}
		public String targetIdentifier() {return helper.getName();}
	}

	private static FacetData STATE_ID_FD = new FacetData(STATE_ID_FACET, FacetData.MemoryUse.READER, "state");
	
	protected StencilOperator operator;
	protected final Split split;
	protected final OperatorData operatorData;
	protected int stateID;
	
	protected SplitHelper(Split split, StencilOperator operator) {
		this.split = split;
		this.operator = operator;		
		this.operatorData = noFunctions(operator.getOperatorData());
		this.operatorData.addFacet(STATE_ID_FD);
	}
	
	public Invokeable getFacet(String facet) {
		if (facet.equals(STATE_ID_FACET)) {
			return new ReflectiveInvokeable(STATE_ID_FACET, this);
		} else {
			try {operator.getFacet(facet);}	
			catch (Exception e) {throw new RuntimeException("Facet error intializing split for " + operator.getName() + "." + facet, e);}
		}
		
		SplitTarget target = new SplitTarget(this, facet); 
		return target;
	}
	
	public OperatorData getOperatorData() {return operatorData;}
	
	public String getName() {
		StringBuilder b = new StringBuilder(operator.getName());
		
		if (split.isOrdered()) {b.append(" (Ordered ");} 
		else {b.append(" (");}
		
		b.append("Split)");
		
		return b.toString();
	}

	/**Removes the first item from the arguments array.
	 * The first argument is assumed to be the split key.*/
	//final because it is a utility method
	protected static final Object[] getArgs(int splitSize, Object...args) {
		Object[] newArgs = new Object[args.length-splitSize];
		System.arraycopy(args, splitSize, newArgs, 0, newArgs.length);
		return newArgs;
	}
	
	protected abstract Object doSplit(String key, Object[] args);
	
	/**Returns the first item from the list.  This is assumed to be the split key.*/
//	private static final String DIVIDER = new String(new char[]{'\0','\0','\0','\0','\0'});
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
	
	//final because it is a utility method
	public static final StencilOperator makeOperator(Specializer spec, StencilOperator operator) {
		int keysize = Converter.toInteger(spec.get(SPLIT_KEY));
		Boolean ordered = (Boolean) Converter.convert(spec.get(ORDERED_KEY), Boolean.class);
		Split split = new Split(keysize, ordered);
		return makeOperator(split, operator);
	}
	
	private static final StencilOperator makeOperator(Split split, StencilOperator operator) {
 		if (split.size() ==0) {return operator;}
		if (AbstractOperator.isFunction(operator)) {throw new RuntimeException("Attempt to wrap pure function in Split.");}
		if (split.isOrdered()) {return new OrderedHelper(split, operator);}
		return new UnorderedHelper(split, operator);
	}

	
	public StencilOperator duplicate() {
		StencilOperator op = operator.duplicate();
		return makeOperator(split, op);
	}
	
	
	/**Produce operator meta-data that indicates all facets in the operator data are not functions.*/
	private static final OperatorData noFunctions(OperatorData od) {
 		List<FacetData> facets = new ArrayList();
 		
 		OperatorData nod = new OperatorData(od);
 		for (FacetData fd: nod.getFacets()) {
 			FacetData nfd =fd;
 			switch (fd.memUse()) {
 				case FUNCTION: 
 					nfd = new FacetData(fd, MemoryUse.READER);
 					break;
 				case READER: 
 					if (!fd.name().equals(STATE_ID_FACET)) {
 	 					nfd = new FacetData(fd, MemoryUse.READER);
 					}
 					break;
 				default: break; 	//no changes for WRITER and UNSPECIFIED
 			}
 			facets.add(nfd);
 		}

 		/**Add a stateID facet**/
 	 	facets.add(new FacetData(STATE_ID_FACET, MemoryUse.READER, "VALUE"));
 		
 		nod.setFacets(facets);
 		return nod;
 	} 	
	
}