package stencil.operator.module.provided;

import stencil.operator.module.ModuleData;
import stencil.operator.module.util.BasicModule;
import stencil.streams.Tuple;
import stencil.util.BasicTuple;

public class Trigonometry extends BasicModule {
	public Trigonometry(ModuleData md) {super(md);}

	protected static double validate(Object d) {
		if (d == null) {throw new IllegalArgumentException("Cannot use nulls in numerics functions.");}
		if (String.class.isInstance(d)) {d = Double.parseDouble((String) d);}
		if (!Number.class.isInstance(d)) {throw new IllegalArgumentException("Can only handle incomming number-derived classes (recieved " + d.getClass() + ").");}
		return ((Number) d).doubleValue();
	}

	public static Tuple toRadians(Object d) {return BasicTuple.singleton(Math.toRadians(validate(d)));}
	public static Tuple toDegrees(Object d) {return BasicTuple.singleton(Math.toDegrees(validate(d)));}
	
	public static Tuple cos(Object d) {return BasicTuple.singleton(Math.cos(validate(d)));}
	public static Tuple sin(Object d) {return BasicTuple.singleton(Math.sin(validate(d)));}
	public static Tuple tan(Object d) {return BasicTuple.singleton(Math.tan(validate(d)));}
	
	public static Tuple acos(Object d) {return BasicTuple.singleton(Math.acos(validate(d)));}
	public static Tuple asin(Object d) {return BasicTuple.singleton(Math.asin(validate(d)));}
	public static Tuple atan(Object d) {return BasicTuple.singleton(Math.atan(validate(d)));}	
	
	
	public static Tuple cosine(Object d) {return BasicTuple.singleton(Math.cos(Math.toRadians(validate(d))));}
	public static Tuple sine(Object d) {return BasicTuple.singleton(Math.sin(Math.toRadians(validate(d))));}
	public static Tuple tangent(Object d) {return BasicTuple.singleton(Math.tan(Math.toRadians(validate(d))));}
	
	public static Tuple arccosine(Object d) {return BasicTuple.singleton(Math.acos(Math.toRadians(validate(d))));}
	public static Tuple arcsine(Object d) {return BasicTuple.singleton(Math.asin(Math.toRadians(validate(d))));}
	public static Tuple arctangent(Object d) {return BasicTuple.singleton(Math.atan(Math.toRadians(validate(d))));}
	
	
}
