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

import java.util.List;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Range;
import stencil.module.operator.wrappers.RangeHelper;
import stencil.module.util.*;
import stencil.util.collections.ConstantList;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;
import static stencil.module.util.Utilities.noFunctions;
import static stencil.module.operator.StencilOperator.QUERY_FACET;

public class LongMath extends BasicModule {

	/**Sum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 **/
	public static final class FullSum extends AbstractOperator {
		private static final String NAME = "Sum";
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

 		public String getName() {return NAME;}
 		public FullSum duplicate() {return new FullSum(operatorData);}
	}

	/**Minimum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	public static final class FullMin extends AbstractOperator {
 		private static final String NAME = "Min";
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
		
		public List<Long> vectorApply(Long[][] args) {
			return new ConstantList(min, args.length);
		}


		public String getName() {return NAME;}
		public FullMin duplicate() {return new FullMin(operatorData);}
	}

	/**Maximum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	public static class FullMax extends AbstractOperator {
 		private static final String NAME = "Max";
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
		
		public List<Long> vectorApply(Long[][] args) {
			return new ConstantList(max, args.length);
		}
		
		public String getName() {return NAME;}
		public FullMax duplicate() {return new FullMax(operatorData);}

	}

	public static long abs(long d) {return Math.abs(d);}//Included here to ensure that the floating point version is grabbed...
	public static long add1(long d) {return d+1;}
	public static long add(long d, long d2) {return d+d2;}
	public static long div(long d1, long d2) {return Math.round(d1)/Math.round(d2);}
	public static long divide(long d1, long d2) {return d1/d2;}
	public static long mult(long d1, long d2) {return d1*d2;}
	public static long negate(long d) {return -1 * d;}
	public static long mod(long d1, long d2) {return Math.round(d1)%Math.round(d2);}

	public static long max(long... ds) {return FullMax.max(ds);}
	public static long min(long... ds) {return FullMin.min(ds);}
	public static long sub(long d, long d2) {return d-d2;}
	public static long sub1(long d) {return d-1;}
	public static long sum(long...ds) {return FullSum.sum(ds);}
	
	public static long nearest(long m, long n) {
 		//Round m to the nearest multiple of n (per http://mindprod.com/jgloss/round.html)
 		long near = ( m + n/2 ) / n * n;
 		return near;
 	}
 	
 	public static Number asNumber(Object v) {return Converter.toNumber(v);}
 	
 	public LongMath(ModuleData md) {super(md);}
 	
 	
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
				target = new FullSum(noFunctions(operatorData));
			} else if (name.equals("Max") && !range.isFullRange()) {
				target = RangeHelper.makeOperator(range, target, QUERY_FACET);
			} else if (name.equals("Max")) {
				target = new FullMax(noFunctions(operatorData));
			} else if (name.equals("Min") && !range.isFullRange()) {
				target = RangeHelper.makeOperator(range, target, QUERY_FACET);}
			else if (name.equals("Min") ) {
				target = new FullMin(noFunctions(operatorData));
			}else {throw new IllegalArgumentException(String.format("Unknown method/specializer combination requested: name = %1$s; specializer = %2$s.", name, specializer.toStringTree()));}

		} catch (Exception e) {throw new Error(String.format("Error locating %1$s operator in Numerics package.", name), e);}

		return target;
	}
}