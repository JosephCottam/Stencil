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

import java.util.HashMap;

import stencil.operator.StencilOperator;
import stencil.operator.module.SpecializationException;
import stencil.operator.module.util.BasicModule;
import stencil.operator.module.util.ModuleData;
import stencil.operator.module.util.OperatorData;
import stencil.operator.util.BasicProject;
import stencil.operator.wrappers.RangeHelper;
import stencil.operator.wrappers.SplitHelper;
import static stencil.operator.StencilOperator.RANGE_FACET;
import stencil.parser.tree.Specializer;

//TODO: Extend median to handle any sortable objects
//TODO: Extend Mode to handle any object with .equals (because you can count with .equals!)
public class Average extends BasicModule {
	public static final String MODULE_NAME = "Average";


	/**Returns a mean over a range.
	 * An empty range is considered to have a mean of 0.
	 * Can be used with zero-length range (n..n) to indicate 'mean of current arguments'
	 * */
	protected static class RangeMean extends BasicProject {
		protected RangeMean(OperatorData opData) {super(opData);}

		private static final String NAME = "Mean";
		
		public double range(Object... args) {
			return map((double[]) RangeHelper.flatten(args, double.class));
		}
		
		public double map(double... args) {
			double mean=0;

			if (args.length !=0) {
				int sum =0;
				for (double d: args) {sum += d;}
				mean = sum/args.length;
			}
			return mean;
		}
		public String getName() {return NAME;}
		public double query(double... args) {return map(args);}
		public double invoke(double... args) {return map(args);}
		
		public RangeMean duplicate() {return new RangeMean(operatorData);}
	}

	/**Keeps a mean over a range from a fixed start point until the current point
	 * in a stream.  Start point may be 0 or greater.  If the start point has not
	 * yet been reached, the mean is returned as 0.
	 *
	 * TODO: Implement the 'calculate but do not render' message to handle case where minimum range has not bee reached
	 * TODO: Augment full-mean to take absolute start and relative end (e.g. hybrid-style range instead of just a full range)
	 */
	public static class FullMean extends BasicProject {
		private static final String NAME = "Mean";
		int start;
		double total =0;
		long count=0;

		public FullMean(OperatorData opData, Specializer specializer) {
			super(opData);
			
			assert !specializer.getRange().relativeStart() : "Can only use FullMean with an absolute start value.";
			assert specializer.getRange().endsWithStream() : "Can only use FullMean with a range that ends with the stream.";
			
			start = specializer.getRange().getStart();
		}
		
		private FullMean(OperatorData opData, int start) {
			super(opData);
			this.start = start;
		}


		public double map(double... values) {
			if (start >0) {start--;}
			if (start >0) {return 0;}

			Double sum=0d;
			for (int i=0;i < values.length; i++) {sum += values[i];}

			count += values.length;
			total += sum;
			return total/count;
		}
		
		public String getName() {return NAME;}

		public double query(Object... args) {
			if (args.length >0) {throw new IllegalArgumentException("Cannot invoke fixd-start-range mean in query context with arguments.");}
			double value =0;
			if (count > 0) {value = total/count;}
			return value;
		}
		
		public FullMean duplicate() {return new FullMean(operatorData, start);}
	}


	/**Returns the median value of a range.  The median is defined
	 * as either the middle-most value (when there are an odd number of elements
	 * in the range) or he mean of the two middle-most values.  Median is computed
	 * the same for full range as sub-range.
	 */
	protected static class Median extends BasicProject {
		private static final String NAME = "Median";

		protected Median(OperatorData opData) {super(opData);}
		
		public double range(Object... args) {
			return map((double[]) RangeHelper.flatten(args, double.class));
		}
		public double map(double... args) {
			if (args.length == 0) {throw new RuntimeException("Cannot compute median on empty list");}

			java.util.Arrays.sort(args);
			int idx = (int) Math.floor(args.length/2);

			if (idx != args.length/2) {
				return (args[idx] + args[idx+1])/2;
			}
			return args[idx];
		}
		public String getName() {return NAME;}

		public double query(double... args) {return map(args);}
		
		public Median duplicate() {return new Median(operatorData);}
	}

	/**Returns the mode value of  range.  The Mode is the most commonly
	 * occurring entry in a range.  Mode is computed the same
	 * for full range as sub-range.
	 */
	protected static class Mode extends BasicProject {
		private static final String NAME = "Mode";

		public Mode(OperatorData opData) {super(opData);}
		
		public double range(Object... args) {
			return map((Double[]) RangeHelper.flatten(args, Double.class));
		}
		public double map(Double... args) {
			HashMap<Double, Integer> counts = new HashMap<Double, Integer>();

			//count values into a hash
			for (double d: args) {
				if (counts.containsKey(d)) {counts.put(d, counts.get(d) +1);}
				else {counts.put(d, 1);}
			}

			//iterate hash for max
			int max =-1;
			double value = Double.NaN;
			for (double d: counts.keySet()) {
				if (counts.get(d) > max) {
					value = d;
					max = counts.get(d);
				}
			}

			if (max == -1) {throw new RuntimeException("Cannot compute mode on empty set.");}

			//return max value as a tuple
			return value;
		}
		public String getName() {return NAME;}

		public double query(Double... args) {return map(args);}
		public Mode duplicate() {return new Mode(operatorData);}
	}

	
	public Average(ModuleData md) {super(md);}
	
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}

		//Accept greater-than-zero ranges and no additional arguments (split is allowed).
		if (specializer.getRange().isSimple() ||
			specializer.getArgs().size() >0) {
			throw new SpecializationException(moduleData.getName(), name, specializer);
		}
	}


	public StencilOperator instance(String name, Specializer specializer)
			throws SpecializationException,IllegalArgumentException {
		StencilOperator target;
		validate(name, specializer);
		
		try {
			OperatorData opData = this.getOperatorData(name, specializer);
			if (name.equals("Average") || name.equals("Mean")) {
				if (specializer.getRange().isFullRange()) {
					target =  new FullMean(opData, specializer);
				} else {
					target = RangeHelper.makeOperator(specializer.getRange(), new RangeMean(opData), RANGE_FACET);
				}
			} else if (name.equals(Median.NAME)) {
				target = RangeHelper.makeOperator(specializer.getRange(), new Median(opData), RANGE_FACET);
			} else if (name.equals(Mode.NAME)) {
				target = RangeHelper.makeOperator(specializer.getRange(), new Mode(opData), RANGE_FACET);
			}else {
				throw new IllegalArgumentException("Method name not found in package: " + name);
			}

		} catch(Exception e) {throw new Error("Error locating method to invoke in Average package.", e);}

		target = SplitHelper.makeOperator(specializer.getSplit(), target);
		
		return target;
	}

}
