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
import stencil.operator.module.ModuleData;
import stencil.operator.module.SpecializationException;
import stencil.operator.module.util.BasicModule;
import stencil.operator.util.BasicProject;
import stencil.operator.wrappers.RangeHelper;
import stencil.operator.wrappers.SplitHelper;
import stencil.parser.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.types.Converter;

//TODO: Extend median to handle any sortable objects
//TODO: Extend Mode to handle any object with .equals (because you can count with .equals!)
public class Average extends BasicModule {
	public static final String MODULE_NAME = "Average";


	/**Returns a mean over a range.
	 * An empty range is considered to have a mean of 0.
	 * Can be used with zero-length range (n..n) to indicate 'mean of current arguments'
	 * */
	protected static class RangeMean extends BasicProject {
		private static final String NAME = "Mean";
		
		public Tuple map(Object... args) {
			Double[] values = RangeHelper.flatten(args, Double.NaN);

			double mean=0;

			if (values.length !=0) {
				int sum =0;
				for (double d: values) {sum += d;}
				mean = sum/values.length;
			}
			return PrototypedTuple.singleton(mean);
		}
		public String getName() {return NAME;}
		public Tuple query(Object... args) {return map(args);}
		public Tuple invoke(Object... args) {return map(args);}
		
		public RangeMean duplicate() {return new RangeMean();}
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

		public FullMean(Specializer specializer) {
			assert !specializer.getRange().relativeStart() : "Can only use FullMean with an absolute start value.";
			assert specializer.getRange().endsWithStream() : "Can only use FullMean with a range that ends with the stream.";

			start = specializer.getRange().getStart();
		}
		
		private FullMean(int start) {this.start = start;}


		public Tuple map(Object... values) {
			if (start >0) {start--;}
			if (start >0) {return PrototypedTuple.singleton(0);}

			Double sum=0d;
			for (Object value: values) {
				Double num = Converter.toDouble(value);
				sum += num;
			}

			count += values.length;
			total += sum;
			return PrototypedTuple.singleton(total/count);
		}
		
		public String getName() {return NAME;}

		public Tuple query(Object... args) {
			if (args.length >0) {throw new IllegalArgumentException("Cannot invoke fixd-start-range mean in query context with arguments.");}
			double value =0;
			if (count > 0) {value = total/count;}
			return PrototypedTuple.singleton(value);
		}
		
		public FullMean duplicate() {return new FullMean(start);}
	}


	/**Returns the median value of a range.  The median is defined
	 * as either the middle-most value (when there are an odd number of elements
	 * in the range) or he mean of the two middle-most values.  Median is computed
	 * the same for full range as sub-range.
	 */
	protected static class Median extends BasicProject {
		private static final String NAME = "Median";
		

		public Tuple map(Object... args) {
			Double[] values = RangeHelper.flatten(args, Double.NaN);
			if (values.length == 0) {throw new RuntimeException("Cannot compute median on empty list");}

			java.util.Arrays.sort(values);
			int idx = (int) Math.floor(values.length/2);

			if (idx != values.length/2) {
				return PrototypedTuple.singleton((values[idx] + values[idx+1])/2);
			}
			return PrototypedTuple.singleton(values[idx]);
		}
		public String getName() {return NAME;}

		public Tuple query(Object... args) {return map(args);}
		public Tuple invoke(Object... args) {return map(args);}
		
		public Median duplicate() {return new Median();}
	}

	/**Returns the mode value of  range.  The Mode is the most commonly
	 * occurring entry in a range.  Mode is computed the same
	 * for full range as sub-range.
	 */
	protected static class Mode extends BasicProject {
		private static final String NAME = "Mode";

		public Tuple map(Object... args) {
			HashMap<Double, Integer> counts = new HashMap<Double, Integer>();
			Double[] values = RangeHelper.flatten(args, Double.NaN);

			//count values into a hash
			for (double d: values) {
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
			return PrototypedTuple.singleton(value);
		}
		public String getName() {return NAME;}

		public Tuple query(Object... args) {return map(args);}
		public Tuple invoke(Object... args) {return map(args);}
		public Mode duplicate() {return new Mode();}
	}

	
	public Average(ModuleData md) {super(md);}
	
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperators().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}

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
			if (name.equals("Average") || name.equals("Mean")) {
				if (specializer.getRange().isFullRange()) {
					target =  new FullMean(specializer);
				} else {
					target = RangeHelper.makeLegend(specializer.getRange(), new RangeMean());
				}
			} else if (name.equals(Median.NAME)) {
				target = RangeHelper.makeLegend(specializer.getRange(), new Median());
			} else if (name.equals(Mode.NAME)) {
				target = RangeHelper.makeLegend(specializer.getRange(), new Mode());
			}else {
				throw new IllegalArgumentException("Method name not found in package: " + name);
			}

		} catch(Exception e) {throw new Error("Error locating method to invoke in Average package.", e);}

		target = SplitHelper.makeOperator(specializer.getSplit(), target);
		
		return target;
	}

}
