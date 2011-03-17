package stencil.modules.stencilUtil;

import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.MethodInvokeFailedException;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.tuple.Tuple;
import stencil.types.Converter;
import static stencil.module.operator.wrappers.Utilities.noFunctions;

import java.util.Map;
import java.util.HashMap;

@Operator(name= "Split", spec="[fields:0, ordered:\"#F\"]", tags=stencil.module.util.OperatorData.HIGHER_ORDER_TAG)
@Description("Higher order operator for performing split/cross-tab summarization")
public abstract class SplitHelper implements StencilOperator {
	/**Handle unordered split cases (default case).*/
	public static class UnorderedHelper extends SplitHelper {
		protected Map<Object, StencilOperator> operators = new HashMap();
		
		public UnorderedHelper(Split split, StencilOperator operator) {super(split, operator);}
		
		public Object doSplit(String facet, Object[] args) {
			Object key = getKey(args);
			Object[] newArgs = getArgs(args);
			StencilOperator op = getOp(key);
			Invokeable inv = op.getFacet(facet);
			
			//Monitor stateID of the invoked operator
			Invokeable stateIDFacet = op.getFacet(STATE_ID_FACET);
			int oldID = Converter.toInteger(stateIDFacet.invoke(new Object[0]));

			Object rv = inv.invoke(newArgs);
			
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
			Object key = getKey(args);
			Object[] newArgs = getArgs(args);
			if (!key.equals(oldKey)) {
				try {operator = operator.duplicate();}
				catch (Exception e) {throw new Error("Error creating new split operator instance.", e);}
				oldKey = key;
			}
			Invokeable inv = operator.getFacet(facet);
			return inv.invoke(newArgs);
		}

		public StencilOperator viewpoint() {
			OrderedHelper nop = new OrderedHelper(super.split, super.operator);
			nop.oldKey = this.oldKey;
			return nop;
		}
	
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
		public Object getTarget() {return this;}
		
		public SplitTarget viewpoint() {throw new UnsupportedOperationException("FIX THIS SOON!!!");}
		public String targetIdentifier() {return helper.getName();}
	}

	private static FacetData STATE_ID_FD = new FacetData(STATE_ID_FACET, FacetData.MemoryUse.READER, "state");
	
	protected StencilOperator operator;
	protected final Split split;
	protected final OperatorData operatorData;
	protected int stateID;					//State ID of the split operator
	
	protected SplitHelper(Split split, StencilOperator operator) {
		this.split = split;
		this.operator = operator;		
		this.operatorData = noFunctions(operator.getOperatorData(), true);
		this.operatorData.addFacet(STATE_ID_FD);
	}
	
	@Facet(memUse="READER", prototype="(int VALUE)")
	public int stateID() {return stateID;}
	
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
	protected static final Object[] getArgs(Object...args) {
		Object[] newArgs = new Object[args.length-1];
		System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		return newArgs;
	}
	
	protected abstract Object doSplit(String key, Object[] args);
	
	/**Returns the first item from the list.  This is assumed to be the split key.*/
	//final because it is a utility method
	protected static final Object getKey(Object... args) {return args[0];}
	
	//final because it is a utility method
	public static final StencilOperator makeOperator(Specializer spec, StencilOperator operator) {
		int keysize = Converter.toInteger(spec.get(Split.SPLIT_KEY));
		Boolean ordered = (Boolean) Converter.convert(spec.get(Split.ORDERED_KEY), Boolean.class);
		Split split = new Split(keysize, ordered);
		return makeOperator(split, operator);
	}
	
	private static final StencilOperator makeOperator(Split split, StencilOperator operator) {
 		if (split.getFields() ==0) {return operator;}
		if (AbstractOperator.isFunction(operator)) {throw new RuntimeException("Attempt to wrap pure function in Split.");}
		if (split.isOrdered()) {return new OrderedHelper(split, operator);}
		return new UnorderedHelper(split, operator);
	}

	
	public StencilOperator duplicate() {
		StencilOperator op = operator.duplicate();
		return makeOperator(split, op);
	}
}