package stencil.types;

import stencil.parser.tree.Id;
import stencil.parser.tree.StencilNumber;
import stencil.parser.tree.StencilString;
import stencil.parser.tree.TupleRef;
import stencil.util.ConversionException;
import stencil.util.enums.ValueEnum;

public final class Converter {
	
	public static Double toDouble(Object value) {
		if (value instanceof Double) {return (Double) value;}
		if (value instanceof ValueEnum) {return toDouble(((ValueEnum) value).getValue());}
		if (value.equals("VERTICAL")) {return new Double(-90);} //TODO: Is there a better way to handle special values like this?
		
		if (value instanceof StencilNumber) {return new Double(((StencilNumber) value).getNumber().doubleValue());}
		
		Class sourceClass = value.getClass();
		if (TypesCache.hasTypeFor(sourceClass)) {return (Double) TypesCache.getType(sourceClass).convert(value, Double.class);}

		
		return Double.parseDouble(value.toString());
	}
	
	public static  Float toFloat(Object value) {
		if (value instanceof Float) {return (Float) value;}
		if (value instanceof ValueEnum) {return toFloat(((ValueEnum) value).getValue());}
		if (value instanceof StencilNumber) {return new Float(((StencilNumber) value).getNumber().floatValue());}
		
		Class sourceClass = value.getClass();
		if (TypesCache.hasTypeFor(sourceClass)) {return (Float) TypesCache.getType(sourceClass).convert(value, Float.class);}

		
		return Float.parseFloat(value.toString());	
	}
	
	public static Integer toInteger(Object value) {
		if (value instanceof Integer) {return (Integer) value;}
		if (value instanceof ValueEnum) {return toInteger(((ValueEnum) value).getValue());}
		if (value instanceof StencilNumber) {return new Integer(((StencilNumber) value).getNumber().intValue());}

		Class sourceClass = value.getClass();
		if (TypesCache.hasTypeFor(sourceClass)) {return (Integer) TypesCache.getType(sourceClass).convert(value, Integer.class);}

		return toFloat(value).intValue();
	}
	
	public static String toString(Object value) {
		if (value instanceof String) {return (String) value;}
		if (value instanceof StencilString) {return ((StencilString) value).getString();} 
		if (value instanceof ValueEnum) {return toString(((ValueEnum) value).getValue());}
		if (value.getClass().isEnum()) {return ((Enum) value).name();}
		if (value instanceof TupleRef && ((TupleRef) value).isNamedRef()) {return ((Id) ((TupleRef) value).getValue()).getName();}
		
		Class sourceClass = value.getClass();
		if (TypesCache.hasTypeFor(sourceClass)) {return (String) TypesCache.getType(sourceClass).convert(value, String.class);}
		
		return value.toString();
	}
	
	public static Number toNumber(Object value) {
		if (value instanceof Number) {return (Number)value;}
		if (value instanceof ValueEnum) {return toNumber(((ValueEnum) value).getValue());}
		if (value instanceof StencilNumber) {return ((StencilNumber) value).getNumber();}
		
		Class sourceClass = value.getClass();
		if (TypesCache.hasTypeFor(sourceClass)) {return (Number) TypesCache.getType(sourceClass).convert(value, Number.class);}

		return toDouble(value); //TODO: have it try to figure if it is a whole number or a float..
	}
	
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
			if (value == null || target.isInstance(value)) {return value;}
			
			if (value instanceof ValueEnum) {
				value = ((ValueEnum) value).getValue();
				if (target.isInstance(value)) {return value;}
			}
			
			if (TypesCache.hasTypeFor(target)) {return TypesCache.getType(target).convert(value, target);}
			if (TypesCache.hasTypeFor(value)) {return TypesCache.getType(value).convert(value, target);}
	
			if (target.equals(Number.class)) {return toNumber(value);}
			
			if (target.equals(Integer.class) || target.equals(int.class)) {return toInteger(value);}
			if (target.equals(Long.class) || target.equals(long.class)) {
				if (value instanceof StencilNumber) {return new Long(((StencilNumber) value).getNumber().longValue());}
				return new Long(Long.parseLong(value.toString()));
			}
	
			if (target.equals(Double.class) || target.equals(double.class)) {return toDouble(value);}
			
			if (target.equals(Float.class) || target.equals(float.class)) {return toFloat(value);}
			
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
