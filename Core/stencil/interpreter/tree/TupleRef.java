package stencil.interpreter.tree;

import java.util.Arrays;

import stencil.tuple.Tuple;

public class TupleRef {
	private final int[] steps;
	private final int hashCode;
	
	public TupleRef(int[] steps) {
		this.steps = Arrays.copyOf(steps, steps.length);

		int code =1;
		for (int step: steps) {
			int stepCode = Integer.valueOf(step).hashCode();
			code = code * (stepCode ==0 ? 2 : stepCode);
		}
		hashCode = code;
	}

	/**Given a list of candidates for resolution (e.g., lexical arguments),
	 * resolve each and return the results of resolution.
	 */
	public static final Object[] resolveAll(Object[] candidates, Tuple valueSource) {
		//Pack arguments...
		Object[] formals = new Object[candidates.length];
		for (int i=0; i< formals.length; i++) {
			if (candidates[i] instanceof TupleRef) {
				formals[i] = ((TupleRef) candidates[i]).resolve(valueSource);
			} else {
				formals[i] = candidates[i];
			}
		}

		return formals;
	}
	
	
	public Object resolve(Tuple t) {
		Tuple source;
		Object result = t;
		for (int i: steps) {
			if (result == null) {return null;}	//HACK: This means a null is recursively resolved as null...should it thrown an exception instead?
			source = (Tuple) result;
			result = source.get(i);
		}
		return result;
	}
	
	public int hashCode() {return hashCode;}
	
	public boolean equals(Object o) {
		if (o == null 
				|| !(o instanceof TupleRef)
				|| (o.hashCode() != hashCode)) {return false;}
		
		TupleRef other = (TupleRef) o;
		
		for (int i=0; i<steps.length;i++) {
			if (other.steps[i] != steps[i]) {return false;}
		}
		return true;
	}
	
}
