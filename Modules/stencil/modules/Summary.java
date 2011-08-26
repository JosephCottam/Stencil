package stencil.modules;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.modules.stencilUtil.Range;
import stencil.modules.stencilUtil.StencilUtil;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;
import static stencil.module.util.ModuleDataParser.operatorData;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.Context;

@Description("Summarization of groups of things (currently just max and min).")
@Module()
public class Summary extends BasicModule {

	/**Minimum of full range of values.*/
	@Suppress
	@Operator(name="Min", spec="[c: \"Object\"]", tags=stencil.modules.stencilUtil.StencilUtil.RANGE_OPTIMIZED_TAG)
	public static final class FullMin extends AbstractOperator.Statefull {
 		private final Class targetClass;
 		private Comparable min;

 		public FullMin(OperatorData opData, Specializer spec) {
 			super(opData);
 			targetClass = (Class) Converter.convert(spec.get("c"), Class.class);
 		}
 		private FullMin(FullMin source) {
 			super(source.operatorData);
 			targetClass = source.targetClass;
 			min = source.min;
 		}

 		protected static Comparable min(Class cls, Object... values) {
 			if (!Comparable.class.isAssignableFrom(cls)) {throw new IllegalArgumentException("Can only use comparable clases; recieved " + cls.getName());}
 			
 			Comparable min = null;
 			for (Object v: values) {
 				Comparable c = (Comparable) Converter.convert(v, cls);
 				if (min == null) {min = c;}
 				else if (min.compareTo(c) >0) {min = c;}
			}
			return min;
 		}
 		
 		@Facet(memUse="WRITER", prototype="(double min)")
 		public Comparable map(Object... values) {
 			Comparable newMin = min(targetClass, values);
 			if (min != null && min.compareTo(newMin) <= 0) {newMin = min;} 
 			if (newMin != min) {
 				stateID++;
 				min = newMin;
 			}
			return min;
		}

		@Description("Return the current minimum, arguments are ignored.")
 		@Facet(memUse="READER", prototype="(double min)")
		public Comparable query(Object... args) {return min;}

		public FullMin duplicate() {return new FullMin(this);}
	}
	/**Minimum of full range of values.*/
	@Suppress
	@Operator(name="Min", spec="[c: \"Object\"]", tags=stencil.modules.stencilUtil.StencilUtil.RANGE_OPTIMIZED_TAG)
	public static final class FullMax extends AbstractOperator.Statefull {
 		private final Class targetClass;
 		private Comparable max;

 		public FullMax(OperatorData opData, Specializer spec) {
 			super(opData);
 			targetClass = (Class) Converter.convert(spec.get("c"), Class.class);
 		}
 		private FullMax(FullMax source) {
 			super(source.operatorData);
 			targetClass = source.targetClass;
 			max = source.max;
 		}

 		protected static Comparable max(Class cls, Object... values) {
 			if (!Comparable.class.isAssignableFrom(cls)) {throw new IllegalArgumentException("Can only use comparable clases; recieved " + cls.getName());}
 			
 			Comparable max = null;
 			for (Object v: values) {
 				Comparable c = (Comparable) Converter.convert(v, cls);
 				if (max == null) {max = c;}
 				else if (max.compareTo(c) < 0) {max = c;}
			}
			return max;
 		}
 		
 		@Facet(memUse="WRITER", prototype="(double min)")
 		public Comparable map(Object... values) {
 			Comparable newMax = max(targetClass, values);
 			if (max != null && max.compareTo(newMax) <= 0) {newMax = max;} 
 			if (newMax != max) {
 				stateID++;
 				max = newMax;
 			}
			return max;
		}

		@Description("Return the current minimum, arguments are ignored.")
 		@Facet(memUse="READER", prototype="(double min)")
		public Comparable query(Object... args) {return max;}

		public FullMax duplicate() {return new FullMax(this);}
	}

	@Description("Find the minimum of the list of values; assumes all passed objects are mutually comprable.")
	@Operator(tags=StencilUtil.RANGE_FLATTEN_TAG)
	@Facet(memUse="FUNCTION", prototype="(double max)", alias={"map","query"})	
	public static Comparable max(Object... ds) {return FullMax.max(Object.class, ds);}

	@Description("Find the maximum of the list of values; assumes all passed objects are mutually comprable.")
	@Operator(tags=StencilUtil.RANGE_FLATTEN_TAG)
	@Facet(memUse="FUNCTION", prototype="(double min)", alias={"map","query"})	
	public static Comparable min(Object... ds) {return FullMin.min(Object.class, ds);}
	
	public StencilOperator instance(String name, Context context, Specializer specializer) throws SpecializationException {
		OperatorData operatorData = getModuleData().getOperator(name);

		validate(name, specializer);
		
		try {
			if (context == null || context.highOrderUses("Range").size() ==0) {return Modules.instance(this.getClass(), operatorData);}

			Specializer spec = Freezer.specializer(context.highOrderUses("Range").get(0).find(StencilParser.SPECIALIZER));
			Range range = new Range(spec.get(Range.RANGE_KEY));
			
			if (range.isFullRange()) {
				if (name.equals("Max")) {return new FullMax(operatorData(FullMax.class, getName()), specializer);}
				if (name.equals("Min")) {return new FullMin(operatorData(FullMin.class, getName()), specializer);}
			} 


		} catch (Exception e) {throw new Error(String.format("Error locating %1$s operator in Numerics package.", name), e);}
		throw new Error("Unnanticipated argument set encountered");
	}
}