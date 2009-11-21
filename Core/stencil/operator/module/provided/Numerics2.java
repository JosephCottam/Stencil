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
package stencil.operator.module.provided;

import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.operator.module.util.BasicModule;
import stencil.operator.module.util.Modules;
import stencil.operator.util.BasicProject;
import stencil.operator.wrappers.RangeHelper;
import stencil.parser.tree.Specializer;
import stencil.parser.tree.Range;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;

public class Numerics2 extends BasicModule {

	/**Sum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 **/
	public static class FullSum { //extends BasicProject {
		private static final String NAME = "Sum";
 		private double sum = 0;
		
 		protected static double sum(double... values) {
 			double sum = 0;
 			for (double v: values) {
				sum = sum + v;
			}
 			return sum;
 		}
 		
		public Tuple map(double... args) {
			sum += sum(args);
			return PrototypedTuple.singleton(sum);
		}

		public Tuple query(double... args) {
			if (args.length >0) {throw new IllegalArgumentException("Cannot invoke fixd-start-range mean in query context with arguments.");}
			return PrototypedTuple.singleton(sum);
		}


 		public String getName() {return NAME;}
 		public FullSum duplicate() {return new FullSum();}
	}

	/**Minimum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	public static final class FullMin {// extends BasicProject {
 		private static final String NAME = "Min";
 		private double min = Double.MAX_VALUE;
 		
 		protected static double min(double... values) {
			double min = Double.MAX_VALUE;
 			for (double v: values) {
				min = Math.min(min, v);
			}
			return min;
 		}
 		public Tuple map(double... values) {
 			min = Math.min(min, min(values));
			return PrototypedTuple.singleton(min);
		}
 		
		public Tuple query(double... args) {
			if (args.length >0) {throw new IllegalArgumentException("Cannot invoke fixd-start-range mean in query context with arguments.");}
			return PrototypedTuple.singleton(min);
		}

		public String getName() {return NAME;}
		public FullMin duplicate() {return new FullMin();}
	}

	/**Maximum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	public static class FullMax { //extends BasicProject {
 		private static final String NAME = "Max";
 		private double max = -Double.MAX_VALUE;	

 		protected static double max(double... values) {
 			double max = -Double.MAX_VALUE;
 			for (double v: values) {
				max = Math.max(max, v);
			}
			return max;
 		}
 		
		public Tuple map(double... values) {
 			max = Math.max(max, max(values));
 			return PrototypedTuple.singleton(max);
		}
		
		public Tuple query(double... args) {
			if (args.length >0) {throw new IllegalArgumentException("Cannot invoke fixd-start-range mean in query context with arguments.");}
			return PrototypedTuple.singleton(max);
		}

		public String getName() {return NAME;}
		public FullMax duplicate() {return new FullMax();}

	}

	public static Tuple add1(double d) {return PrototypedTuple.singleton(d+1);}
	public static Tuple sub1(double d) {return PrototypedTuple.singleton(d-1);}


	public static Tuple abs(double d) {return PrototypedTuple.singleton(Math.abs(d));}
	public static Tuple sum(double...ds) {return PrototypedTuple.singleton(FullSum.sum(ds));}
	public static Tuple add(double d, double d2) {return PrototypedTuple.singleton(d+d2);}
	public static Tuple sub(double d, double d2) {return PrototypedTuple.singleton(d-d2);}
	public static Tuple divide(double d1, double d2) {return PrototypedTuple.singleton(d1/d2);}
	public static Tuple mult(double d1, double d2) {return PrototypedTuple.singleton(d1*d2);}
	public static Tuple negate(double d) {return PrototypedTuple.singleton(-1 * d);}
	public static Tuple mod(double d1, double d2) {return PrototypedTuple.singleton(d1%d2);}
	public static Tuple div(double d1, double d2) {return PrototypedTuple.singleton(d1/d2);}

	public static Tuple log(double d1) {return  PrototypedTuple.singleton(Math.log(d1));}
	public static Tuple log10(double d1) {return  PrototypedTuple.singleton(Math.log10(d1));}
	
	public static Tuple max(double... ds) {return PrototypedTuple.singleton(FullMax.max(ds));}
	public static Tuple min(double... ds) {return PrototypedTuple.singleton(FullMin.min(ds));}

	public static Tuple floor(double d1) {return PrototypedTuple.singleton(Math.floor(d1));}
	public static Tuple ceil(double d1) {return PrototypedTuple.singleton(Math.ceil(d1));}
 	public static Tuple round(double d1) {return PrototypedTuple.singleton(Math.round(d1));}
 	public static Tuple nearest(double d1, double d2) {
 		long m = (long) d1;
 		long n = (long) d2;
 		//Round m to the nearest multiple of n (per http://mindprod.com/jgloss/round.html)
 		long near = ( m + n/2 ) / n * n;
 		return PrototypedTuple.singleton(near);
 	}
 	
 	public static Tuple asNumber(Object v) {
 		double d;
 		if (v == null) {d = Double.NaN;}
 		else if (v instanceof String) {d = Double.parseDouble((String) v);}
 		else if (v instanceof Number) {d = ((Number) v).doubleValue();}
 		else {d = Double.NaN;}

 		return PrototypedTuple.singleton(d);
 	}

 	public static Tuple sqrt(double d) {return PrototypedTuple.singleton(Math.sqrt(d));}
 	public static Tuple pow(double d1, double d2) {return PrototypedTuple.singleton(Math.pow(d1, d2));}
 	
 	public Numerics2(ModuleData md) {super(md);}
 	
 	public ModuleData getModuleData() {return moduleData;}
 	
 	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperators().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}

		if (specializer.getArgs().size() >0) {throw new SpecializationException(moduleData.getName(), name, specializer);}
 	}
 	
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		Range range = specializer.getRange();

		validate(name, specializer);
		StencilOperator target = null;
		String targetName = moduleData.getOperatorData(name).getAttribute("Target");
		
		
		try {
			target = Modules.instance(this.getClass(), targetName, getModuleData().getName(), name);
			if (specializer.isSimple()) {
				return target;
			} else if (name.equals("Sum") && !range.isFullRange()) {
				target = RangeHelper.makeLegend(specializer.getRange(), target);
			} else if (name.equals("Sum")) {
				target = (StencilOperator) new FullSum();
			} else if (name.equals("Max") && !range.isFullRange()) {
				target = RangeHelper.makeLegend(specializer.getRange(), target);
			} else if (name.equals("Max")) {
				target = (StencilOperator) new FullMax();
			} else if (name.equals("Min") && !range.isFullRange()) {
				target = RangeHelper.makeLegend(specializer.getRange(),target);}
			else if (name.equals("Min") ) {
				//target = new FullMin();
			}else {throw new IllegalArgumentException(String.format("Unknown method/specializer combination requested: name = %1$s; specializer = %2$s.", name, specializer.toString()));}

		} catch (Exception e) {throw new Error(String.format("Error locating %1$s operator in Numerics package.", name), e);}

		return target;
	}
}