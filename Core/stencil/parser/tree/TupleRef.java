package stencil.parser.tree;

import java.util.List;

import org.antlr.runtime.Token;

import stencil.tuple.Tuple;

/**Reference to a value in a tuple (represented as a list of reference steps)
 * 
 * TODO: Would an optimized version of this help?  
 * 		 All tuple refs have two steps: Field and Frame
 * 	     Special compiler pass initializes the instances so that field and frame are pre-computed
 * 		 ALL is represented as -1
 * */
public final class TupleRef extends Value {
	public TupleRef(Token source) {super(source);}

	/**Unsupported operation*/
	public final Atom getValue() {throw new UnsupportedOperationException("Cannot get simple value from a TupleRef");}

	/**Get the value this reference holds with respect to the tuple.*/
	public Object getValue(Tuple source) {
		Object value = source;
		for (int i=0; i< getChildCount(); i++) {
			if (value == null) {return null;}
			StencilNumber num = (StencilNumber) getChild(i);
			int idx = num.intValue();
			value = ((Tuple) value).get(idx);
		}
		return value;
	}
	
	public boolean isTupleRef() {return true;}	
		
	/**Given a potential reference and source, gets a Java value.
	 * Values are resolved in the following order of precedence:
	 * 	1) Literals
	 * 	2) Local tuple reference
	 * 	3) Global tuple references
	 * 
	 * Local tuple reference is resolved relative to the passed source.
	 * If the potential reference is a reference and it can be resolved
	 * against the source, it will be.  If it cannot be, then it will
	 * be resolved against global sources. 
	 *
	 */
	public static final Object resolve(Value potentialRef, Tuple valueSource) {
		if (potentialRef instanceof Atom) {return potentialRef.getValue();}
		
		if (!potentialRef.isTupleRef()) {throw new IllegalArgumentException("Can only handle literals and tuple references.");}
		TupleRef ref = (TupleRef) potentialRef;
		return ref.getValue(valueSource);
	}
	
	/**Given a list of candidates for resolution (e.g., lexical arguments),
	 * resolve each and return the results of resolution.
	 */
	public static final Object[] resolveAll(List<? extends Value> candidates, Tuple valueSource) {
		//Pack arguments...
		Object[] formals = new Object[candidates.size()];
		for (int i=0; i< formals.length; i++) {
			formals[i] = TupleRef.resolve(candidates.get(i), valueSource);
		}

		return formals;
	}
	
	public int hashCode() {
		int code = 1;
		for (int i=0; i<getChildCount(); i++) {
			code = code * Math.abs(getChild(i).hashCode())+1;
		}
		return code;
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TupleRef)) {return false;}
		TupleRef other = (TupleRef) o;
		
		for (int i=0; i<getChildCount();i++) {
			if (!other.getChild(i).equals(getChild(i))) {return false;}
		}
		return true;
	}
}
