/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.modules;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Range;
import stencil.module.operator.wrappers.RangeHelper;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;
import static stencil.module.util.ModuleDataParser.operatorData;
import static stencil.module.operator.StencilOperator.QUERY_FACET;

@Module
@Description("Math functions that are defined in terms of longs instead of the default doubles")
public class LongMath extends BasicModule {

	/**Sum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 **/
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
 		
		public long map(long... args) {
			sum += sum(args);
			stateID++;
			return sum;
		}

		/**Arguments are ignored.*/
		public long query(Object... args) {
			return sum;
		}

 		public FullSum duplicate() {return new FullSum(operatorData);}
	}

	/**Minimum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
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
 		
 		public long map(long... values) {
 			long newMin = Math.min(min, min(values));
 			if (newMin != min) {
 				stateID++;
 				min = newMin;
 			}
			return min;
		}

		/**Arguments are ignored.*/
		public long query(Object... args) {
			return min;
		}
		
		public FullMin duplicate() {return new FullMin(operatorData);}
	}

	/**Maximum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
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
 		
		public long map(long... values) {
 			long newMax = Math.max(max, max(values));
 			if (newMax != max) {
 				max = newMax;
 				stateID++;
 			}
 			return max;
		}
		
		/**Arguments are ignored.*/
		public long query(Object... args) {
			return max;
		}
		
		public FullMax duplicate() {return new FullMax(operatorData);}

	}

	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long abs)", alias={"map","query"})
	public static long abs(long d) {return Math.abs(d);}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long sum)", alias={"map","query"})	
	public static long add1(long d) {return d+1;}

	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long sum)", alias={"map","query"})	
	public static long add(long d, long d2) {return d+d2;}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long quotient)", alias={"map","query"})		
	public static long div(long d1, long d2) {return Math.round(d1)/Math.round(d2);}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long quotient)", alias={"map","query"})		
	public static long divide(long d1, long d2) {return d1/d2;}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long product)", alias={"map","query"})		
	public static long mult(long d1, long d2) {return d1*d2;}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long value)", alias={"map","query"})		
	public static long negate(long d) {return -1 * d;}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long mod)", alias={"map","query"})		
	public static long mod(long d1, long d2) {return Math.round(d1)%Math.round(d2);}

	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long max)", alias={"map","query"})	
	public static long max(long... ds) {return FullMax.max(ds);}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long min)", alias={"map","query"})	
	public static long min(long... ds) {return FullMin.min(ds);}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long diff)", alias={"map","query"})
	public static long sub(long d, long d2) {return d-d2;}

	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long diff)", alias={"map","query"})
	public static long sub1(long d) {return d-1;}

	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long sum)", alias={"map","query"})
	public static long sum(long...ds) {return FullSum.sum(ds);}
	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long value)", alias={"map","query"})
	public static long nearest(long m, long n) {
 		//Round m to the nearest multiple of n (per http://mindprod.com/jgloss/round.html)
 		long near = ( m + n/2 ) / n * n;
 		return near;
 	}
 	
	@Operator(spec="[range: LAST, split: 0]")
	@Facet(memUse="FUNCTION", prototype="(long value)", alias={"map","query"})
	//TODO: Remove when converter has its own module/operator
	public static Number asNumber(Object v) {return Converter.toNumber(v);}
 	 	
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		OperatorData operatorData = getModuleData().getOperator(name);
		Range range = new Range(specializer.get(Specializer.RANGE));

		validate(name, specializer);
		StencilOperator target = null;
		
		try {
			
			target = Modules.instance(this.getClass(), operatorData);
			if (specializer.isLowMem()) {
				return target;
			} else if (name.equals("Sum") && !range.isFullRange()) {
				target = RangeHelper.makeOperator(range, target, QUERY_FACET);
			} else if (name.equals("Sum")) {
				target = new FullSum(operatorData(FullSum.class, getName()));
			} else if (name.equals("Max") && !range.isFullRange()) {
				target = RangeHelper.makeOperator(range, target, QUERY_FACET);
			} else if (name.equals("Max")) {
				target = new FullSum(operatorData(FullMax.class, getName()));
			} else if (name.equals("Min") && !range.isFullRange()) {
				target = RangeHelper.makeOperator(range, target, QUERY_FACET);}
			else if (name.equals("Min") ) {
				target = new FullSum(operatorData(FullMin.class, getName()));
			}else {throw new IllegalArgumentException(String.format("Unknown method/specializer combination requested: name = %1$s; specializer = %2$s.", name, specializer.toStringTree()));}

		} catch (Exception e) {throw new Error(String.format("Error locating %1$s operator in Numerics package.", name), e);}

		return target;
	}
}