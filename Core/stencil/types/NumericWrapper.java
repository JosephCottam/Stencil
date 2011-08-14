package stencil.types;

import stencil.parser.tree.StencilTree;
import stencil.tuple.Tuple;
import stencil.tuple.instances.NumericSingleton;
import stencil.tuple.prototype.TuplePrototype;
import static stencil.parser.string.StencilParser.NUMBER;

public class NumericWrapper implements TypeWrapper {
	private static final Class[] ACCEPTS = {Number.class, Integer.class, Long.class, Double.class, Float.class, int.class, long.class, double.class, float.class};


	public Tuple toTuple(Object o) {
		if (o instanceof Number) {return new NumericSingleton((Number) o);}
		throw new RuntimeException("Error wrapping:" + o.toString());
	}

	public Class[] appliesTo() {return ACCEPTS;}

	public static Double toDouble(Object value) {
		if (value instanceof Double) {return (Double) value;}
		if (value instanceof Number) {return ((Number) value).doubleValue();}
		if (value.equals("VERTICAL")) {return Math.toRadians(-90);} //TODO: Is there a better way to handle special values like this?
		
		if (value instanceof StencilTree && ((StencilTree) value).getType() == NUMBER) {
			return new Double(((StencilTree) value).getText());
		}
		
		return Double.parseDouble(value.toString());
	}
	
	public static  Float toFloat(Object value) {
		if (value instanceof Float) {return (Float) value;}
		if (value instanceof Number) {return ((Number) value).floatValue();}
		if (value instanceof StencilTree && ((StencilTree) value).getType() == NUMBER) {
			return new Float(((StencilTree) value).getText());
		}

		
		
		return Float.parseFloat(value.toString());	
	}
	
	public static Integer toInteger(Object value) {
		if (value instanceof Integer) {return (Integer) value;}
		if (value instanceof Long) {return ((Long) value).intValue();}
		if (value instanceof Number) {return ((Number) value).intValue();}
		if (value instanceof StencilTree && ((StencilTree) value).getType() == NUMBER) {
			return new Integer(((StencilTree) value).getText());
		}

		
		return toFloat(value).intValue();
	}
	
	public static Long toLong(Object value) {
		if (value instanceof Long) {return (Long) value;}
		if (value instanceof Number) {return ((Number) value).longValue();}
		if (value instanceof StencilTree && ((StencilTree) value).getType() == NUMBER) {
			return Long.valueOf(((StencilTree) value).getText());
		}

		return Long.valueOf(value.toString());
	}
	
	public static Number toNumber(Object value) {
		if (value instanceof Number) {return (Number)value;}

		String rep = value.toString();
		if (rep.contains(".")) {return toDouble(rep);}
		else {
			long val = toLong(rep);
			if (val <= Integer.MAX_VALUE && val >= Integer.MIN_VALUE) {
				return Integer.valueOf((int) val);
			} else{
				return val;
			}
		}
	}

	
	/**Takes the first value from the tuple and makes it an integer.*/
	public Object external(Tuple t, TuplePrototype p, Class c) {
		Object v = t.get(0);
		return convert(v, c);
	}

	public Object convert(Object value, Class c) {
		if (c.equals(Number.class)) {return toNumber(value);}
		if (c.equals(Double.class)) {return toDouble(value);}
		if (c.equals(double.class)) {return toDouble(value);}
		if (c.equals(Float.class)) {return toFloat(value);}
		if (c.equals(float.class)) {return toFloat(value);}
		if (c.equals(Integer.class)) {return toInteger(value);}
		if (c.equals(int.class)) {return toInteger(value);}
		if (c.equals(Long.class)) {return toLong(value);}
		if (c.equals(long.class)) {return toLong(value);}
		if (c.equals(String.class)) {return value.toString();}
		throw new RuntimeException(String.format("Error converting %1$s to %2$s.", value, c));
	}
	


}


