package stencil.modules.stencilUtil;


import java.util.*;

import stencil.module.MethodInstanceException;
import stencil.module.ModuleCache;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.DirectOperator;
import stencil.module.operator.util.MethodInvokeFailedException;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.module.util.ModuleDataParser.MetaDataParseException;
import stencil.module.util.ann.*;
import stencil.parser.string.util.Context;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.types.Converter;

import static stencil.module.util.ModuleDataParser.operatorData;
import static stencil.module.util.ModuleDataParser.moduleData;
import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;


/**Operators used in various stencil transformations.*/
@Module
@Description("Utilities to manipulate the tuple data representation.")
public final class StencilUtil extends BasicModule {
	/**Some modules provide optimizations for some range sets.  
	 * If an operator instance is range optimized, it must include this tag in its operator data to indicate
	 * that no further ranging is required.  
	 * If this tag is included, the range wrapper factory will return the passed operator without further wrapping.
	 */
	public static final String RANGE_OPTIMIZED_TAG = "#__RANGE_OPTIMIZED";
	
	/**Some operators can be efficiently implemented for ranging if the list of lists of formals
	 * of arguments are first converted is combined into a single list of formals.  The conversion
	 * is called "flattening" and this tag indicates that it should be done.  This is the only way
	 * that a non-mutative facet can be used in range.
	 */
	public static final String RANGE_FLATTEN_TAG = "#__RANGE_FLATTEN";

	
	/**Indicates that range is explicitly disallowed.*/
	public static final String RANGE_DISALLOW_TAG = "#__NO_RANGE_ALLOWED";

	/**Indicates that range is explicitly disallowed.*/
	public static final String MAP_DISALLOW_TAG = "#__NO_MAP_ALLOWED";

	/**Indicates that split is explicitly disallowed.*/
	public static final String DISALLOW_TAG = "#__NO_SPLIT_ALLOWED";
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	@Description("Use the Converter to change the value(s) passed into a tuple.")
	public static final Tuple toTuple(Object... values) {return Converter.toTuple(values);} 
	
	
	@Operator()
	@Description("No-Op.  Always returns the empty tuple.")
	public static final class Nop extends DirectOperator implements SampleOperator {
		protected Nop(OperatorData od) {super(od);}

		@Override
		@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
		public Object invoke(Object[] arguments) throws MethodInvokeFailedException {return Tuples.EMPTY_TUPLE;}

		@Override
		public List<Tuple> sample(SampleSeed seed, Specializer details) {return new ArrayList();}
	}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	@Description("Repackage the the values passed as a tuple (simple wrapping, no conversion attempted).")
	public static final Tuple valuesTuple(Object... values) {return new ArrayTuple(values);} 

	@Override
	protected ModuleData loadOperatorData() throws MetaDataParseException {
		final String MODULE_NAME = this.getClass().getSimpleName();
		
		ModuleData md = moduleData(this.getClass());	//Loads the meta-data for operators defined in this class
		
		
		md.addOperator(operatorData(MonitorCategorical.class, MODULE_NAME));
		md.addOperator(operatorData(MonitorContinuous.class, MODULE_NAME));
		md.addOperator(operatorData(MonitorFlex.class, MODULE_NAME));

		md.addOperator(operatorData(MapWrapper.class, MODULE_NAME));
		md.addOperator(operatorData(RangeHelper.class, MODULE_NAME));
		md.addOperator(operatorData(SplitHelper.class, MODULE_NAME));
		md.addOperator(operatorData(SpecialTuples.Canvas.class, MODULE_NAME));
		md.addOperator(operatorData(SpecialTuples.View.class, MODULE_NAME));
		
		return md;
	}
	
	
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData od = moduleData.getOperator(name);

		//HACK: Avoid the direct name-based stuff...its difficult to maintain
		if (od.getName().contains("Monitor")) {od = MonitorBase.complete(od, specializer);}
		
		if (od.isComplete()) {return od;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, od);
	}
	
	public StencilOperator instance(String name, Context context, Specializer specializer) throws SpecializationException {
		OperatorData operatorData = getOperatorData(name, specializer);

		if (name.equals("ToTuple") || name.equals("ValuesTuple")) {
			return super.instance(name, context, specializer);
		} else if (name.equals("Nop")) {
			return new Nop(operatorData);
		} else if (name.equals("MonitorCategorical")) {
			return new MonitorCategorical(operatorData, specializer);
		} else if (name.equals("MonitorContinuous")) {
			return new MonitorContinuous(operatorData, specializer);			
		} else if (name.equals("MonitorFlex")) {
			return new MonitorFlex(operatorData, specializer);			
		} else if (name.equals("view")) {
			return new SpecialTuples.View(operatorData);
		} else if (name.equals("canvas")) {
			return new SpecialTuples.Canvas(operatorData);
		}
		
		throw new Error("Could not instantiate regular operator " + name);
	}

	public StencilOperator instance(String name, Context context, Specializer specializer, ModuleCache modules) throws SpecializationException {
		List<StencilOperator> opArgs = new ArrayList();

		
		//TODO: This is a horrible way to resolve things, have the operator as value in a CONST in the specializer instead of the name
		for (String key: specializer.keySet()) {
			if (key.startsWith("Op")) {
				StencilOperator op;
				try {
					op = modules.instance((MultiPartName) specializer.get(key), null, EMPTY_SPECIALIZER, false);
					opArgs.add(op);
				} catch (MethodInstanceException e) {throw new IllegalArgumentException("Error instantiate operator-as-argument " + specializer.get(key), e);}
			}
		}
		
		assert opArgs.size() >0;
		if (opArgs.size() >1) {throw new IllegalArgumentException(name + " can only accept one higher order arg, recieved " + opArgs.size());}
		
		
		if (name.equals("Map")) {
			return new MapWrapper(opArgs.get(0));
		} else if (name.equals("Range")) {
			return RangeHelper.makeOperator(specializer, opArgs.get(0));	
		} else if (name.equals("Split")) {
			return SplitHelper.makeOperator(specializer, opArgs.get(0));			
		} 
		
		throw new Error("Could not instantiate higher-order operator " + name);
	}
}
