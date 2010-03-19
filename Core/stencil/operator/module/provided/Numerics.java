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
import stencil.operator.module.util.ModuleData;
import stencil.operator.module.util.Modules;
import stencil.operator.module.util.OperatorData;
import stencil.operator.util.BasicProject;
import stencil.operator.wrappers.RangeHelper;
import stencil.parser.tree.Specializer;
import stencil.parser.tree.Range;
import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;

public class Numerics extends BasicModule {

	/**Sum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 **/
	public static final class FullSum extends BasicProject {
		private static final String NAME = "Sum";
 		private double sum = 0;
 		
 		public FullSum(OperatorData opData) {super(opData);}
		
 		protected static double sum(Object... values) {
 			double sum = 0;
 			for (Object o: values) {
				Double v = validate(o);
				sum = sum + v;
			}
 			return sum;
 		}
 		
		public Tuple map(Object... args) {
			sum += sum(args);
			return new ArrayTuple(sum);
		}

		/**Arguments are ignored.*/
		public Tuple query(Object... args) {
			return new ArrayTuple(sum);
		}


 		public String getName() {return NAME;}
 		public FullSum duplicate() {return new FullSum(operatorData);}
	}

	/**Minimum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	public static final class FullMin extends BasicProject {
 		private static final String NAME = "Min";
 		private double min = Double.MAX_VALUE;

 		public FullMin(OperatorData opData) {super(opData);}

 		
 		protected static double min(Object... values) {
			double min = Double.MAX_VALUE;
 			for (Object o: values) {
				Double v = validate(o);
				min = Math.min(min, v);
			}
			return min;
 		}
 		public Tuple map(Object... values) {
 			min = Math.min(min, min(values));
			return new ArrayTuple(min);
		}

		/**Arguments are ignored.*/
		public Tuple query(Object... args) {
			return new ArrayTuple(min);
		}

		public String getName() {return NAME;}
		public FullMin duplicate() {return new FullMin(operatorData);}
	}

	/**Maximum of full range of values.
	 * TODO: Modify to handle fixed-start range
	 */
	public static class FullMax extends BasicProject {
 		private static final String NAME = "Max";
 		private double max = -Double.MAX_VALUE;	

 		public FullMax(OperatorData opData) {super(opData);}
 		
 		protected static double max(Object... values) {
 			double max = -Double.MAX_VALUE;
 			for (Object o: values) {
				Double v = validate(o);
				max = Math.max(max, v);
			}
			return max;
 		}
 		
		public Tuple map(Object... values) {
 			max = Math.max(max, max(values));
 			return new ArrayTuple(max);
		}
		
		/**Arguments are ignored.*/
		public Tuple query(Object... args) {
			return new ArrayTuple(max);
		}

		public String getName() {return NAME;}
		public FullMax duplicate() {return new FullMax(operatorData);}

	}


	protected static double validate(Object d) {
		if (d == null) {throw new IllegalArgumentException("Cannot use nulls in numerics functions.");}
		if (String.class.isInstance(d)) {d = Double.parseDouble((String) d);}
		if (!Number.class.isInstance(d)) {throw new IllegalArgumentException("Can only handle incomming number-derived classes (recieved " + d.getClass() + ").");}
		return ((Number) d).doubleValue();
	}

	public static Tuple add1(Object d) {return new ArrayTuple(validate(d)+1);}
	public static Tuple sub1(Object d) {return new ArrayTuple(validate(d)-1);}


	public static Tuple abs(Object d) {return new ArrayTuple(Math.abs(validate(d)));}
	public static Tuple sum(Object...ds) {return new ArrayTuple(FullSum.sum(ds));}
	public static Tuple add(Object d, Object d2) {return new ArrayTuple(validate(d)+validate(d2));}
	public static Tuple sub(Object d, Object d2) {return new ArrayTuple(validate(d)-validate(d2));}
	public static Tuple divide(Object d1, Object d2) {return new ArrayTuple(validate(d1)/validate(d2));}
	public static Tuple mult(Object d1, Object d2) {return new ArrayTuple(validate(d1)*validate(d2));}
	public static Tuple negate(Object d) {return new ArrayTuple(-1 * validate(d));}
	public static Tuple mod(Object d1, Object d2) {return new ArrayTuple(Math.round(validate(d1))%Math.round(validate(d2)));}
	public static Tuple div(Object d1, Object d2) {return new ArrayTuple(Math.round(validate(d1))/Math.round(validate(d2)));}

	public static Tuple log(Object d1) {return  new ArrayTuple(Math.log(validate(d1)));}
	public static Tuple log10(Object d1) {return  new ArrayTuple(Math.log10(validate(d1)));}
	
	public static Tuple max(Object... ds) {return new ArrayTuple(FullMax.max(ds));}
	public static Tuple min(Object... ds) {return new ArrayTuple(FullMin.min(ds));}

	public static Tuple floor(Object d1) {return new ArrayTuple(Math.floor(validate(d1)));}
	public static Tuple ceil(Object d1) {return new ArrayTuple(Math.ceil(validate(d1)));}
 	public static Tuple round(Object d1) {return new ArrayTuple(Math.round(validate(d1)));}
 	public static Tuple nearest(Object d1, Object d2) {
 		long m = (long) validate(d1);
 		long n = (long) validate(d2);
 		//Round m to the nearest multiple of n (per http://mindprod.com/jgloss/round.html)
 		long near = ( m + n/2 ) / n * n;
 		return new ArrayTuple(near);
 	}
 	
 	public static Tuple asNumber(Object d) {return new ArrayTuple(validate(d));}

 	public static Tuple sqrt(Object d) {return new ArrayTuple(Math.sqrt(validate(d)));}
 	public static Tuple pow(Object d1, Object d2) {return new ArrayTuple(Math.pow(validate(d1), validate(d2)));}
 	
 	public Numerics(ModuleData md) {super(md);}
 	
 	public ModuleData getModuleData() {return moduleData;}
 	
 	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}

		if (specializer.getArgs().size() >0) {throw new SpecializationException(moduleData.getName(), name, specializer);}
 	}
 	
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		Range range = specializer.getRange();

		validate(name, specializer);
		StencilOperator target = null;
		OperatorData operatorData = getModuleData().getOperator(name);
		
		try {
			
			target = Modules.instance(this.getClass(), operatorData);
			if (specializer.isSimple()) {
				return target;
			} else if (name.equals("Sum") && !range.isFullRange()) {
				target = RangeHelper.makeLegend(specializer.getRange(), target);
			} else if (name.equals("Sum")) {
				target = new FullSum(operatorData);
			} else if (name.equals("Max") && !range.isFullRange()) {
				target = RangeHelper.makeLegend(specializer.getRange(), target);
			} else if (name.equals("Max")) {
				target = new FullMax(operatorData);
			} else if (name.equals("Min") && !range.isFullRange()) {
				target = RangeHelper.makeLegend(specializer.getRange(),target);}
			else if (name.equals("Min") ) {
				target = new FullMin(operatorData);
			}else {throw new IllegalArgumentException(String.format("Unknown method/specializer combination requested: name = %1$s; specializer = %2$s.", name, specializer.toString()));}

		} catch (Exception e) {throw new Error(String.format("Error locating %1$s operator in Numerics package.", name), e);}

		return target;
	}
}