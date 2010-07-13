package stencil.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import stencil.parser.tree.Atom;
import stencil.parser.tree.StencilString;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.*;
import stencil.util.ConversionException;
import stencil.util.collections.PropertyUtils;
import stencil.util.enums.ValueEnum;

public final class Converter {
	private static final Map<Class, TypeWrapper> WRAPPER_FOR = new HashMap();
	public static final String WRAPPER_KEY = "wrapper";
	
	public static final Map<Class, TypeWrapper> getWrappers() {
		return Collections.unmodifiableMap(WRAPPER_FOR);
	}
	
	public static final void registerWrappers(Properties props) {
		for (String key: PropertyUtils.filter(props, WRAPPER_KEY)) {
			String className = props.getProperty(key);

			try {
				TypeWrapper wrapper = (TypeWrapper) Class.forName(className).getConstructor().newInstance();
				registerWrapper(wrapper);
			} catch (Exception e) {
				throw new Error("Error loading type wrapper: " + key + ": " + className, e);
			}
			
		}
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
	public static Tuple toTuple(Object value) {
		if (value == null) {return Tuples.EMPTY_TUPLE;}
		if (value instanceof Tuple) {return (Tuple) value;}
		
		Class clazz = value.getClass();
		if (clazz.isArray()) {
			clazz = clazz.getComponentType();
			if (clazz.equals(double.class)) {return new Doubles((double[]) value);}
			if (clazz.equals(int.class)) {return new Ints((int[]) value);}			
			return new ArrayTuple(value);
		}
		
		if (WRAPPER_FOR.containsKey(clazz)) {
			TypeWrapper w = WRAPPER_FOR.get(clazz);
			return w.toTuple(value);
		}
		
		if (Number.class.isAssignableFrom(clazz)) {return new NumericSingleton((Number) value);}
		
		return new Singleton(value);
	}
	
	
	public static String toString(Object value) {
		if (value == null) {return null;}
		if (value instanceof String) {return (String) value;}
		if (value instanceof StencilString) {return ((StencilString) value).getString();} 
		if (value instanceof ValueEnum) {return toString(((ValueEnum) value).getValue());}
		if (value.getClass().isEnum()) {return ((Enum) value).name();}
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
			if (value == null) {return null;}
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
