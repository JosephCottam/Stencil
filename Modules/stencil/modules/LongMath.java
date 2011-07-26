package stencil.modules;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.modules.stencilUtil.Range;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;
import static stencil.module.util.ModuleDataParser.operatorData;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.Context;

@Module
@Description("Math functions that are defined in terms of longs instead of the default doubles")
public class LongMath extends BasicModule {

	/**Sum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 **/
	@Suppress
	@Operator(name="Sum", tags=stencil.modules.stencilUtil.StencilUtil.RANGE_OPTIMIZED_TAG)
	public static final class FullSum extends AbstractOperator.Statefull {
 		private long sum = 0;
 		
 		public FullSum(OperatorData opData) {super(opData);}
		
 		protected static long sum(long... values) {
 			long sum = 0;
 			for (long v: values) {
				sum = sum + v;
			}
 			return sum;
 		}
 		
		@Facet(memUse="WRITER", prototype="(double sum)")
		public long map(long... args) {
			sum += sum(args);
			stateID++;
			return sum;
		}

		/**Arguments are ignored.*/
		@Facet(memUse="READER", prototype="(double sum)")
		public long query(Object... args) {
			return sum;
		}

 		public FullSum duplicate() {return new FullSum(operatorData);}
	}

	/**Minimum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	@Suppress
	@Operator(name="Min", tags=stencil.modules.stencilUtil.StencilUtil.RANGE_OPTIMIZED_TAG)
	public static final class FullMin extends AbstractOperator.Statefull {
 		private long min = Long.MAX_VALUE;

 		public FullMin(OperatorData opData) {super(opData);}

 		
 		protected static long min(long... values) {
			long min = Long.MAX_VALUE;
 			for (long v: values) {
				min = Math.min(min, v);
			}
			return min;
 		}
 		
 		@Facet(memUse="WRITER", prototype="(double min)")
 		public long map(long... values) {
 			long newMin = Math.min(min, min(values));
 			if (newMin != min) {
 				stateID++;
 				min = newMin;
 			}
			return min;
		}

		/**Arguments are ignored.*/
 		@Facet(memUse="READER", prototype="(double min)")
		public long query(Object... args) {
			return min;
		}
		
		public FullMin duplicate() {return new FullMin(operatorData);}
	}

	/**Maximum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	@Suppress
	@Operator(name="Max", tags=stencil.modules.stencilUtil.StencilUtil.RANGE_OPTIMIZED_TAG)
	public static class FullMax extends AbstractOperator.Statefull {
 		private long max = -Long.MAX_VALUE;	

 		public FullMax(OperatorData opData) {super(opData);}
 		
 		protected static long max(long... values) {
 			long max = -Long.MAX_VALUE;
 			for (long v: values) {
				max = Math.max(max, v);
			}
			return max;
 		}
 		
 		@Facet(memUse="WRITER", prototype="(double max)")
		public long map(long... values) {
 			long newMax = Math.max(max, max(values));
 			if (newMax != max) {
 				max = newMax;
 				stateID++;
 			}
 			return max;
		}
		
		/**Arguments are ignored.*/
 		@Facet(memUse="READER", prototype="(double max)")
		public long query(Object... args) {
			return max;
		}
		
		public FullMax duplicate() {return new FullMax(operatorData);}

	}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long abs)", alias={"map","query"})
	public static long abs(long d) {return Math.abs(d);}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long sum)", alias={"map","query"})	
	public static long add1(long d) {return d+1;}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long sum)", alias={"map","query"})	
	public static long add(long d, long d2) {return d+d2;}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long quotient)", alias={"map","query"})		
	public static long div(long d1, long d2) {return Math.round(d1/d2);}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long quotient)", alias={"map","query"})		
	public static long divide(long d1, long d2) {return d1/d2;}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long product)", alias={"map","query"})		
	public static long mult(long d1, long d2) {return d1*d2;}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long value)", alias={"map","query"})		
	public static long negate(long d) {return -1 * d;}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long mod)", alias={"map","query"})		
	public static long mod(long d1, long d2) {return d1%d2;}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long max)", alias={"map","query"})	
	public static long max(long... ds) {return FullMax.max(ds);}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long min)", alias={"map","query"})	
	public static long min(long... ds) {return FullMin.min(ds);}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long diff)", alias={"map","query"})
	public static long sub(long d, long d2) {return d-d2;}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long diff)", alias={"map","query"})
	public static long sub1(long d) {return d-1;}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long sum)", alias={"map","query"})
	public static long sum(long...ds) {return FullSum.sum(ds);}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long value)", alias={"map","query"})
	public static long nearest(long m, long n) {
 		//Round m to the nearest multiple of n (per http://mindprod.com/jgloss/round.html)
 		long near = ( m + n/2 ) / n * n;
 		return near;
 	}
 	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(long value)", alias={"map","query"})
	//TODO: Remove when converter has its own module/operator
	public static Number asNumber(Object v) {return Converter.toNumber(v);}
 	 	
	public StencilOperator instance(String name, Context context, Specializer specializer) throws SpecializationException {
		OperatorData operatorData = getModuleData().getOperator(name);

		validate(name, specializer);
		
		try {
			if (context.highOrderUses("Range").size() ==0) {return Modules.instance(this.getClass(), operatorData);}
			Specializer spec = Freezer.specializer(context.highOrderUses("Range").get(0).find(StencilParser.SPECIALIZER));
			Range range = new Range(spec.get(Range.RANGE_KEY));
				
			if (range.isFullRange()) {
				if (name.equals("Sum")) {return new FullSum(operatorData(FullSum.class, getName()));}
				if (name.equals("Max")) {return new FullMax(operatorData(FullMax.class, getName()));}
				if (name.equals("Min")) {return new FullMin(operatorData(FullMin.class, getName()));}
			} 
		} catch (Exception e) {throw new Error(String.format("Error locating %1$s operator in Numerics package.", name), e);}
		throw new Error("Unnanticipated argument set encountered");
	}
}