package stencil.operator.wrappers;

import stencil.tuple.Tuple;
import stencil.operator.StencilOperator;
import stencil.parser.tree.Split;
import stencil.parser.tree.Value;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class SplitHelper implements StencilOperator {
	protected StencilOperator operator;
	protected Split split;

	/**Handle unordered split cases (default case).*/
	public static class UnorderedHelper extends SplitHelper {
		protected Map<Object, StencilOperator> operators = new HashMap();
		
		public UnorderedHelper(Split split, StencilOperator operator) {super(split, operator);}
		
		public Tuple map(Object... args) {
			Object key = getKey(args);
			Object[] newArgs = getArgs(args);
			StencilOperator op = getOp(key);
			return op.map(newArgs);
		}
		
		public Tuple query(Object...args) {
			Object key = getKey(args);
			Object[] newArgs = getArgs(args);
			StencilOperator op = getOp(key);
			return op.query(newArgs);
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
		
		public Tuple map(Object...args) {
			Object[] newArgs = doSplit(args);
			return operator.map(newArgs);
		}
		
		public Tuple query(Object...args) {
			Object[] newArgs = doSplit(args);			
			return operator.query(newArgs);
		}
		
		private Object[] doSplit(Object... args) {
			Object key = getKey(args);
			Object[] newArgs = getArgs(args);
			if (!key.equals(oldKey)) {
				try {operator = operator.duplicate();}
				catch (Exception e) {throw new Error("Error creating new split operator instance.", e);}
				oldKey = key;
			}
			return newArgs;
		}
		
	}
	
	protected SplitHelper(Split split, StencilOperator operator) {
		this.split = split;
		this.operator = operator;
	}
	
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

	/**Returns the first item from the list.  This is assumed to be the split key.*/
	//final because it is a utility method
	protected static final Object getKey(Object... args) {return args[0];}
	
	//final because it is a utility method
	public static final StencilOperator makeOperator(Split split, StencilOperator operator) {
		if (!split.hasSplitField()) {return operator;}
		if (split.isOrdered()) {return new OrderedHelper(split, operator);}
		return new UnorderedHelper(split, operator);
	}

	
	public StencilOperator duplicate() {
		StencilOperator op = operator.duplicate();
		return makeOperator(split, op);
	}
	
	//TODO: Support compound operator types.  Then split is a combined categorize followed by a project.
	public List guide(List<Value> formalArguments, List<Object[]> sourceArguments,  List<String> prototype) {throw new UnsupportedOperationException(String.format("Split cannot autoguide (wrapping %1$s).", operator.getName()));} //TODO: Handle as a compound categorize and project.  Needs to return list of categorize (split values) and the results of each.  May not work for ordered splits.  
	public boolean refreshGuide() {throw new UnsupportedOperationException(String.format("Split cannot autoguide (wrapping %1$s).", operator.getName()));}
}
