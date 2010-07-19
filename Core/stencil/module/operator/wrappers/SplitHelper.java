package stencil.module.operator.wrappers;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.MethodInvokeFailedException;
import stencil.module.operator.util.Split;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.parser.tree.Value;
import stencil.tuple.Tuple;
import stencil.types.Converter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class SplitHelper implements StencilOperator {
	protected StencilOperator operator;
	protected Split split;
	protected OperatorData operatorData;

	/**Handle unordered split cases (default case).*/
	public static class UnorderedHelper extends SplitHelper {
		protected Map<Object, StencilOperator> operators = new HashMap();
		
		public UnorderedHelper(Split split, StencilOperator operator) {super(split, operator);}
		
		public Object doSplit(String facet, Object[] args) {
			Object key = getKey(args);
			Object[] newArgs = getArgs(args);
			StencilOperator op = getOp(key);
			Invokeable inv = op.getFacet(facet);
			return inv.invoke(newArgs);
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
			}
			return operators.get(key);
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
	}
	
	protected SplitHelper(Split split, StencilOperator operator) {
		this.split = split;
		this.operator = operator;
		OperatorData opData = new OperatorData(operator.getOperatorData()); 
		this.operatorData = opData;
		for (FacetData facet: opData.getFacets()) {
			facet.setFunction(false);
		}
	}
	
	public Invokeable getFacet(String facet) {
		try {operator.getFacet(facet);}	
		catch (Exception e) {throw new RuntimeException("Facet error intializing split for " + operator.getName() + "." + facet, e);}
		
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
	public static final StencilOperator makeOperator(Split split, StencilOperator operator) {
		if (split.getFields() ==0) {return operator;}
		if (split.isOrdered()) {return new OrderedHelper(split, operator);}
		return new UnorderedHelper(split, operator);
	}

	
	public StencilOperator duplicate() {
		StencilOperator op = operator.duplicate();
		return makeOperator(split, op);
	}
	
	public List<Tuple> vectorQuery(Object[][] args) {
		return AbstractOperator.doVectorQuery(this, args);
	}
	
	//TODO: Support compound operator types.  Then split is a combined categorize followed by a project.
	public List guide(List<Value> formalArguments, List<Object[]> sourceArguments,  List<String> prototype) {throw new UnsupportedOperationException(String.format("Split cannot autoguide (wrapping %1$s).", operator.getName()));} //TODO: Handle as a compound categorize and project.  Needs to return list of categorize (split values) and the results of each.  May not work for ordered splits.  
	public boolean refreshGuide() {throw new UnsupportedOperationException(String.format("Split cannot autoguide (wrapping %1$s).", operator.getName()));}
}
