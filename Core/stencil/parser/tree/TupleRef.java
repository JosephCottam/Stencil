package stencil.parser.tree;

import java.util.List;
import java.util.Arrays;

import org.antlr.runtime.Token;
import stencil.streams.Tuple;

/**Unpack a tuple reference.*/
public class TupleRef extends Value {

	public TupleRef(Token source) {super(source);}

	/**Get the value object that this tuple ref contains.
	 * This is the name or the number or the qualified name/numbers.*/
	public Atom getValue() {
		//TODO: Handle non-atom references (like qualified names).
		return (Atom) getChild(0);
	}

	/**Get the value this reference holds with respect to the tuple.*/
	public Object getValue(Tuple source) {
		return externalValue(source);
	}

	public boolean isTupleRef() {return true;}
	
	public boolean isNumericRef() {return getValue().isNumber();}
	public boolean isNamedRef() {return getValue().isName();} 
	
	/**Is the tuple-ref able to return a value from the given source?*/
	public boolean canRef(Tuple source) {
		try {externalValue(source); return true;}
		catch (Exception e) {return false;}
	}

	/**Get the java-type-value of this tuple-ref, relative to the passed tuple.
	 *
	 * @param source
	 * @return
	 */
	private Object externalValue(Tuple source) {
		assert source != null : "Cannot use null source to get value with a TupleRef";

		Atom value = getValue();

		if (value.isName()) {
			return source.get(((Id)value).getName());
		} else if (value.isNumber()){
			String field = source.getFields().get((Integer) ((StencilNumber) value).getNumber());
			return source.get(field);
		} //TODO: Sub-tuples...
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
		
		//Check for local reference
		//TODO: Make it so that valueSource is never null...
		if (valueSource == null) {return null;} 
		
		if (ref.canRef(valueSource)) {return ref.getValue(valueSource);}
		
		//Check for global reference
		if (ref.isNamedRef()) {
			//Named value (needs further resolution because of global tuples)
			Id id = (Id) ref.getValue();
			String name = id.getName();
			
			if (View.Global.isViewField(name)) {
				return View.Global.getView().get(View.Global.regularField(name));
			} else if (Canvas.Global.isCanvasField(name)) {
				return Canvas.Global.getCanvas().get(Canvas.Global.regularField(name));
			}
		} 
		
		throw new IllegalArgumentException(String.format("Could not resolved %1$s to a value (valid local tuple fields are %2$s).", potentialRef.toStringTree(), Arrays.deepToString(valueSource.getFields().toArray())));
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
