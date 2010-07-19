package stencil.module.operator.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

import stencil.module.operator.StencilOperator;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.tuple.Tuple;

public abstract class AbstractOperator implements StencilOperator {
	protected final OperatorData operatorData;
	protected int stateID = Integer.MIN_VALUE;
	
	protected AbstractOperator(OperatorData opData) {
		this.operatorData = opData;
	}

	/**Naive implementation of getFacet.
	 * 
	 * Searches member methods of this instance and returns
	 * the one that matches (case insensitive) the name of the
	 * facet.
	 */
	public Invokeable getFacet(String name) {
		FacetData fd = operatorData.getFacet(name);
		String searchName = fd.getTarget().toUpperCase();
		for (Method method: this.getClass().getMethods()) {
			if (method.getName().toUpperCase().equals(searchName)) {
				return new ReflectiveInvokeable(method, this);
			}
		}
		throw new IllegalArgumentException(format("Could not find method named %1$s.", name));
	}
	
	public OperatorData getOperatorData() {return operatorData;}	
	public String getName() {return operatorData.getName();}

	public int StateID() {return stateID;}
	
	public List vectorQuery(Object[][] args) {return doVectorQuery(this, args);}
	
	/**Unsupported operation in BasicProject, must be supplied by the 
	 * actual implementation.
	 */
	public StencilOperator duplicate() {throw new UnsupportedOperationException();}
	
	/**General purpose vector query operation.
	 * Applies the query facet to each item in the listed arguments.
	 * This is a general-purpose implementation; specific operators
	 * may have more efficient equivalents (e.g. full-range Max can be done in 
	 * constant time).
	 * 
	 * */
	public static List<Tuple> doVectorQuery(StencilOperator op, Object[][] args) {
		Invokeable query = op.getFacet("query");
		Tuple[] results = new Tuple[args.length];
		for (int i=0; i< args.length;i++) {
			Object[] argSet = args[i];
			results[i] = query.tupleInvoke(argSet);
		}
		return Arrays.asList(results);
	}
}
