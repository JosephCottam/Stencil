package stencil.interpreter.guide;

import stencil.interpreter.guide.samplers.*;
import stencil.modules.stencilUtil.StencilUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Samplers {
	public static final String SAMPLE_KEY = "#sample";
	
	private Samplers() {}

	/**Sampler mapping used for direct samples*/
	private static Map<String, Class<? extends SampleOperator>> samplers = new HashMap();

	/**Sample type to monitor type**/
	private static Map<String, String> monitors = new HashMap();

	static {
		add("FLEX", "MonitorFlex" , FlexSampler.class);
		add("LINEAR", "MonitorContinuous" , NumericSampler.class);
		add("LOG", "MonitorContinuous", NumericSampler.class);
		add("CATEGORICAL", "MonitorCategorical", StringSampler.class);
		add("COLOR", "MonitorCategorical", ColorSampler.class);
		add("LAYER", null, LayerSampler.class);
		add("NOP", "Nop", StencilUtil.Nop.class);
		
	}
	
	/**Given the guide type, what is the required monitor operator?
	 * Passed type must be non-null; the match is case insensitive;
	 * **/
	public static String monitor(String type) {
		assert type != null : "Must provide a non-null type to get a monitor oeprator";
		return monitors.get(type.toUpperCase());
	}

	/**Get a sampler based on the passed arguments.
	 * The raw type should be passed as the first value.
	 * All arguments will be passed on to the selected sampler for further refinement.
	 */
	public static SampleOperator get(Object... args) {
		assert args.length != 0 : "Must specify a sample type as the first argument.";
		String type = (String) args[0];
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
	public static void add(String type, String monitorOp, Class<? extends SampleOperator> op) {
		assert op != null : "Must specify an operator class";
		assert type != null : "Must specify a class associated with an operator";
		
		if (samplers.containsKey(type)) {System.err.printf("Overriding type %1$s in samplers from %2$s to %3$s.", type, samplers.get(type).getName(), op.getName());}
		
		samplers.put(type, op);
		monitors.put(type, monitorOp);
	}
}
