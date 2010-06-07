package stencil.interpreter.guide;

import stencil.display.DisplayLayer;
import stencil.interpreter.guide.samplers.*;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.parser.tree.Layer;

import java.util.HashMap;
import java.util.Map;
import java.awt.Color;

public final class Samplers {
	public static final String CATEGORICAL ="CATEGORICAL";
	public static final String SAMPLE_KEY = "sample";

	
	private Samplers() {}

	private static Map<Class, Class<? extends SampleOperator>> samplers = new HashMap();
	static {
		samplers.put(Integer.class, NumericSampler.class);
		samplers.put(Long.class, NumericSampler.class);
		samplers.put(int.class, NumericSampler.class);
		samplers.put(long.class, NumericSampler.class);
		samplers.put(Double.class, NumericSampler.class);
		samplers.put(double.class, NumericSampler.class);
		samplers.put(Float.class, NumericSampler.class);
		samplers.put(float.class, NumericSampler.class);
		samplers.put(Number.class, NumericSampler.class);
		samplers.put(String.class, StringSampler.class);
		samplers.put(Color.class, ColorSampler.class);
		samplers.put(DisplayLayer.class, LayerSampler.class);
		samplers.put(Layer.class, LayerSampler.class);
	}
	
	
	public static SampleOperator get(Class clazz, Object... args) {
		assert clazz != null : "Must specify a class.";
		
		Class<? extends SampleOperator> opClass = samplers.get(clazz);
		
		Class[] argTypes = new Class[args.length];
		for (int i=0; i< args.length; i++) {argTypes[i] = args[i].getClass();}

		try {return opClass.getConstructor(argTypes).newInstance(args);}
		catch (Exception e) {throw new RuntimeException("Error creating sample operator for class: " + clazz.getName());}
	}
	
	public static Invokeable getInvokeable(Class clazz, Object... args) {
		SampleOperator o = get(clazz, args);
		return new ReflectiveInvokeable("sample", o);
	}
	
	public static void add(Class clazz, Class<? extends SampleOperator> op) {
		assert op != null : "Must specify an operator class";
		assert clazz != null : "Must specify a class associated with an operator";
		
		samplers.put(clazz, op);
	}
}
