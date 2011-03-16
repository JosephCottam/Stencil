package stencil.module.operator.util;

import java.lang.reflect.Method;

import static java.lang.String.format;

import stencil.module.operator.StencilOperator;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import stencil.module.util.ann.Facet;

public abstract class AbstractOperator<T extends StencilOperator> implements StencilOperator<T>, Cloneable {
	
	/**Simple implementation for StateID tracking.  
	 * 
	 * To help provide consistency the per-instance stateID method is synchronized.
	 * Any method that uses state should also be synchronized at the method level.
	 * 
	 * Inheriting from this class is not required to support stateful operations,
	 * but the stateID facet as defined must be provided by any implementation and the meta-data must match
	 * the meta-data provided here.
	 **/
	public static abstract class Statefull<R extends StencilOperator> extends AbstractOperator<R> {
		protected int stateID = Integer.MIN_VALUE;
		
		protected Statefull(OperatorData opData) {super(opData);}

		//TODO: When considering multi-threaded analysis, does stateID need to be volatile?  What about object state?  What about just requiring all methods to be synchronized?
		@Facet(memUse="READER", prototype="(int VALUE)")
		public int stateID() {return stateID;}
		
		/**Throws an UnsupportedOperationException; duplicate must be supplied by the underlying
		 * implementation for stateful operations.
		 */
		public StencilOperator duplicate() {throw new UnsupportedOperationException();}
		
		/**Default viewpoint creation is to just clone the underlying operator.
		 * This is sufficient, provided the referenced object and everything it transitively refers to are immutable.
		 * Otherwise, viewpoint must be implemented by the implementing class.
		 **/
		@Override
		public R viewpoint() {
			try {return (R) this.clone();}
			catch (Exception e) {throw new RuntimeException("Error creating viewpoint.", e);}
		}
	}
	
	protected final OperatorData operatorData;
	
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
		String target = fd.getTarget();
		try {
			for (Method method: this.getClass().getMethods()) {
				if (method.getName().equals(target)) {return new ReflectiveInvokeable(method, this);}
			}
		} catch (Exception e) {
			throw new RuntimeException(format("Error retrieving facet %1$s on %2$s.", name, operatorData.getName()),e);
		}
		throw new IllegalArgumentException(format("Facet %1$s has incorrect target on %2$s.", name, operatorData.getName()));
	}
	
	public OperatorData getOperatorData() {return operatorData;}	
	public String getName() {return operatorData.getName();}
	
	/**Returns self (essentially assuming that the operator implements a function).*/
	public StencilOperator duplicate() {return this;}

	/**Default viewpoint is self.  This is safe ONLY if the operator implements a true function.**/
	public T viewpoint() {return (T) this;}
	
	/** Does given operator/meta-data include any stateful facets?  If not, it is a function.**/ 
	public static boolean isFunction(StencilOperator op) {return isFunction(op.getOperatorData());}
	public static boolean isFunction(OperatorData op) {
		for (FacetData fd: op.getFacets()) {
			if (fd.getMemUse() != MemoryUse.FUNCTION) {return false;}
		}
		return true;
	}
}
