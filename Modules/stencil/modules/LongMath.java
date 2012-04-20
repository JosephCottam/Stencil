package stencil.modules;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.types.Converter;

@Module
@Description("Math functions that are defined in terms of longs instead of the default doubles")
public class LongMath extends BasicModule {

	/**Sum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 **/
	@Operator(name="FullSum")
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
 		
		@Facet(memUse="WRITER", prototype="(double sum)", counterpart="query")
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

 		@Override
		public FullSum duplicate() {return new FullSum(operatorData);}
	}

	/**Minimum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	@Operator(name="FullMin")
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
 		
 		@Facet(memUse="WRITER", prototype="(double min)", counterpart="query")
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
		
		@Override
		public FullMin duplicate() {return new FullMin(operatorData);}
	}

	/**Maximum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	@Operator(name="FullMax")
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
 		
 		@Facet(memUse="WRITER", prototype="(double max)", counterpart="query")
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
		
		@Override
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
}