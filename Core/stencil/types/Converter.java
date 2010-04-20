package stencil.types;

import java.util.HashMap;
import java.util.Map;

import stencil.parser.tree.Atom;
import stencil.parser.tree.Id;
import stencil.parser.tree.StencilString;
import stencil.parser.tree.TupleRef;
import stencil.tuple.ArrayTuple;
import stencil.tuple.NumericSingleton;
import stencil.tuple.Tuple;
import stencil.util.ConversionException;
import stencil.util.enums.ValueEnum;

public final class Converter {
	private static final Map<Class, TypeWrapper> WRAPPER_FOR = new HashMap();

	static {
		//TODO: Move to something like the modules mechanism, loaded from a configuration file
		registerWrapper(new NumericWrapper());
		registerWrapper(new stencil.types.color.ColorWrapper());
	}
	
	public static final void registerWrapper(TypeWrapper wrapper) {
		for (Class c: wrapper.appliesTo()) {WRAPPER_FOR.put(c, wrapper);}
	}

	/**Make the given object into a tuple; 
	 * uses the passed container array tuple if an array tuple would be used anyway.
	 * 
	 * @param value
	 * @param container
	 * @return
	 */
	public static Tuple toTuple(Object value, ArrayTuple container) {
		if (value instanceof Tuple) {return (Tuple) value;}
		
		Class clazz = value.getClass();
		
		if (WRAPPER_FOR.containsKey(clazz)) {
			TypeWrapper w = WRAPPER_FOR.get(clazz);
			return w.toTuple(value);
		}
		
		if (Number.class.isAssignableFrom(clazz)) {return new NumericSingleton((Number) value);}
		
		container.setArray(value);
		return container;
	}
	
	
	public static String toString(Object value) {
		if (value instanceof String) {return (String) value;}
		if (value instanceof StencilString) {return ((StencilString) value).getString();} 
		if (value instanceof ValueEnum) {return toString(((ValueEnum) value).getValue());}
		if (value.getClass().isEnum()) {return ((Enum) value).name();}
		if (value instanceof TupleRef && ((TupleRef) value).isNamedRef()) {return ((Id) ((TupleRef) value).getValue()).getName();}
		if (WRAPPER_FOR.containsKey(value.getClass())) {return (String) WRAPPER_FOR.get(value.getClass()).convert(value, String.class);}
		return value.toString();
	}
	
	public static Double toDouble(Object value) {return NumericWrapper.toDouble(value);}
	public static  Float toFloat(Object value) {return NumericWrapper.toFloat(value);}
	public static Integer toInteger(Object value) {return NumericWrapper.toInteger(value);}
	public static Number toNumber(Object value) {return NumericWrapper.toNumber(value);}
	
	/**Tries to convert values from the current class to
	 * the target class.  
	 * 
	 * Primitive numeric are handled
	 * by invoking the toString on the value and then parse
	 * UNLESS the value is a StencilNumber, then the StencilNumber
	 * methods are used to extract the value.
	 *
	 *  ValueEnums are converted via getValue if the object type
	 *  returned fromgetValue is instance compatible with the
	 *  target class.
	 *
	 *  Colors are parsed through ColorParser.safeParse.
	 *  Enumerations are returned through Enum.valueOf
	 *
	 *
	 * @param value
	 * @param target
	 * @return
	 */
	public static final Object convert(Object value, Class target) throws ConversionException {
		try {
			if (value == null || target.equals(Object.class) || target.isInstance(value)) {return value;}
			if (value instanceof Atom) {return convert(((Atom) value).getValue(), target);}
						
			TypeWrapper wrapper = WRAPPER_FOR.get(target);
			if (wrapper != null) {return wrapper.convert(value, target);}
			
			if (value instanceof ValueEnum) {
				value = ((ValueEnum) value).getValue();
				if (target.isInstance(value)) {return value;}
			}
			if (target.equals(String.class)) {return toString(value);}

			if (target.equals(boolean.class) || target.equals(Boolean.class)) {
				String v = value.toString().toUpperCase();
				return new Boolean(v.equals("TRUE") || v.equals("#T"));
			}
			
			if (target.isEnum()) {return Enum.valueOf(target, value.toString());}

			
		} catch (Exception e) {
			throw new ConversionException(value, target, e);
		}
		
		throw new ConversionException(value, target);
	}
	
	/**Convert a value for storage in the given target.
	 * This method should only be used if the target is the exact type
	 * required (super classes, especially Object) may result in incorrect conversions.  
	 */
	public static final <T> T convertFor(Object value, T target) throws ConversionException {
		return (T) convert(value, target.getClass());
	}
}
