package stencil.interpreter.guide;

import stencil.interpreter.guide.samplers.*;
import stencil.modules.stencilUtil.StencilUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Samplers {
	public static final String SAMPLE_KEY = "sample";
	public static enum Monitor {CATEGORICAL, CONTINUOUS, FLEX, NONE, NOP}
	
	private Samplers() {}

	/**Sampler mapping used for direct samples*/
	private static Map<String, Class<? extends SampleOperator>> samplers = new HashMap();

	/**Sample type to monitor type**/
	private static Map<String, Monitor> monitors = new HashMap();

	static {
		add("FLEX", Monitor.FLEX, FlexSampler.class);
		add("LINEAR", Monitor.CONTINUOUS, NumericSampler.class);
		add("LOG", Monitor.CONTINUOUS, NumericSampler.class);
		add("CATEGORICAL", Monitor.CATEGORICAL, StringSampler.class);
		add("COLOR", Monitor.CATEGORICAL, ColorSampler.class);
		add("LAYER", Monitor.NONE, LayerSampler.class);
		add("NOP", Monitor.NOP, StencilUtil.Nop.class);
	}
	
	
	public static Monitor monitor(String type) {return monitors.get(type.toUpperCase());}
	public static SampleOperator get(String type, Object... args) {
		assert type != null : "Must specify a class.";
		type = type.toUpperCase();
		
		Class<? extends SampleOperator> opClass = samplers.get(type);
		
		Class[] argTypes = new Class[args.length];
		for (int i=0; i< args.length; i++) {argTypes[i] = args[i].getClass();}

		try {return opClass.getConstructor(argTypes).newInstance(args);}
		catch (Exception e) {throw new RuntimeException(String.format("Error creating sample operator for %1$s with args %2$s ", type, Arrays.deepToString(args)), e);}
	}
	
	/**Register a sampler.
	 * @param type    Sampler name to be used in the Stencil program (probably the selector of a guide)
	 * @param monitor The monitor to use with the given type
	 * @param op      The class of the sampler.  This class will be used to reflectively construct, so it must have a public constructor.
	 */
	public static void add(String type, Monitor monitor, Class<? extends SampleOperator> op) {
		assert op != null : "Must specify an operator class";
		assert type != null : "Must specify a class associated with an operator";
		
		if (samplers.containsKey(type)) {System.err.printf("Overriding type %1$s in samplers from %2$s to %3$s.", type, samplers.get(type).getName(), op.getName());}
		
		samplers.put(type, op);
		monitors.put(type, monitor);
	}
}
