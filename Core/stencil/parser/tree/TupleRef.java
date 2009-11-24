package stencil.parser.tree;

import java.util.List;
import org.antlr.runtime.Token;

import stencil.tuple.Tuple;

/**Unpack a tuple reference.*/
public final class TupleRef extends Value {

	public TupleRef(Token source) {super(source);}

	/**Get the simple value portion of this reference (what will be used to de-reference the tuple)*/
	public Atom getValue() {return (Atom) getChild(0);}

	/**Get the value this reference holds with respect to the tuple.*/
	public Object getValue(Tuple source) {return externalValue(source);}

	public TupleRef getSubRef() {
		if (!hasSubRef()) {throw new RuntimeException("Attempt to get subref where none is present.");}
		return (TupleRef) getChild(1);
	}
	
	public boolean isTupleRef() {return true;}	
	public boolean isNumericRef() {return getValue().isNumber();}
	public boolean isNamedRef() {return getValue().isName();}
	public boolean hasSubRef() {return this.getChildCount() >= 2;}
	
	
	/**Given the prototype, returns the numeric offset this represents in that prototype.
	 * 
	 * If this is a numeric reference, the prototype may safely be null, but a named 
	 * reference requires the prototype to be non-null.
	 * 
	 * @param prototype
	 * @return
	 */
	public int toNumericRef(List<String> prototype) {
		if (isNumericRef()) {return ((StencilNumber) getValue()).getNumber().intValue();}
		else {
			String name = getValue().getValue().toString();
			if (!prototype.contains(name)) {throw new IllegalArgumentException(String.format("Could not find %1$s in %2$s.", name, prototype.toString()));}			
			return prototype.indexOf(name);
		}
	}

	/**Get the java-type-value of this tuple-ref, relative to the passed tuple.
	 *
	 * @param source
	 * @return
	 */
	private final Object externalValue(Tuple source) {
		if (source == null) {return null;}

		Atom ref = getValue();
		Object value = doRef(source, ref);
		
		if (hasSubRef()) {return getSubRef().externalValue((Tuple) value);}
		else {return value;}
	}

	private final Object doRef(Tuple source, Atom ref) {
		if (ref.isName()) {
			return source.get(((Id) ref).getName());
		} else if (ref.isNumber()){
			return source.get(((StencilNumber) getValue()).getNumber().intValue());
		}
		throw new RuntimeException("Could not get tuple ref with value of type " + typeName(getType()));
	}
	
	
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
		if (potentialRef instanceof Atom.Literal
			|| potentialRef instanceof Atom) {return potentialRef.getValue();}
		
		if (!potentialRef.isTupleRef()) {throw new IllegalArgumentException("Can only handle literals and tuple references.");}
		TupleRef ref = (TupleRef) potentialRef;
		return ref.externalValue(valueSource);
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
}
