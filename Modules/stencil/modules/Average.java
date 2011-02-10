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

import java.util.HashMap;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.modules.stencilUtil.Range;
import stencil.modules.stencilUtil.StencilUtil;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.Specializer;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.Context;

//TODO: Extend median to handle any sortable objects
//TODO: Extend Mode to handle any object with .equals (because you can count with .equals!)

@Module
@Description("Module for handling the average, in various manners.")
public class Average extends BasicModule {
	/**Keeps a mean over a range from a fixed start point until the current point
	 * in a stream.  Start point may be 0 or greater.  If the start point has not
	 * yet been reached, the mean is returned as 0.
	 *
	 * TODO: Implement the 'calculate but do not render' message to handle case where minimum range has not bee reached
	 * TODO: Augment full-mean to take absolute start and relative end (e.g. hybrid-style range instead of just a full range)
	 */
	@Suppress 
	@Operator(name="Mean", tags=stencil.modules.stencilUtil.StencilUtil.RANGE_OPTIMIZED_TAG)
	public static class FullMean extends AbstractOperator.Statefull {
		int start;
		double total =0;
		long count=0;

		public FullMean(OperatorData opData, Specializer specializer, int start) {
			super(opData);			
			this.start = start;
		}
		
		private FullMean(OperatorData opData, int start) {
			super(opData);
			this.start = start;
		}

		@Facet(memUse="WRITER", prototype="(double avg)")
		public double map(double... values) {
			if (start >0) {start--;}
			if (start >0) {return 0;}

			double sum=0;
			for (int i=0;i < values.length; i++) {sum += values[i];}

			count += values.length;
			total += sum;
			stateID++;
			return total/count;
		}
		
		@Facet(memUse="READER", prototype="(double avg)")
		public double query(double... args) {
//			if (args.length ==0) {
				return count==0 ? 0 : total/count;
//			} else {
//				double sum=0;
//				for (int i=0;i < args.length; i++) {sum += args[i];}
//				return (total+sum)/(count+args.length);
//			}
		}
		
		public FullMean duplicate() {return new FullMean(operatorData, start);}
	}


	/**Takes a mean over exactly what is passed, keeps no memory*/
	@Operator(name="Mean", tags=StencilUtil.RANGE_FLATTEN_TAG)
	@Facet(memUse="FUNCTION", prototype="(double avg)", alias={"query","map"})
	public static double mean(double...values) {
		double sum =0;
		for (double value: values) {sum += value;}
		return sum/values.length;
	}

	@Suppress @Operator(name = "Mode")
	public static class SimpleMode extends AbstractOperator {
		public SimpleMode(OperatorData opData) {super(opData);}
		
		@Facet(memUse="FUNCTION", prototype="(double avg)", alias={"query","map"})
		public Object query(Object... values) {
			HashMap<Object, Integer> m = new HashMap();
			for (Object value: values) {
				if (!m.containsKey(value)) {m.put(value,0);}
				m.put(value, m.get(value)+1);
			}
			
			int max = Integer.MIN_VALUE;
			Object result = null;
			for (Object key: m.keySet()) {
				if (m.get(key) > max) {
					max = m.get(key);
					result = key;
				}
			}
			
			return result; 
		}
	}
	
	@Suppress @Operator(name = "Median")
	public static class SimpleMedian extends AbstractOperator {
		public SimpleMedian(OperatorData opData) {super(opData);}
		
		@Facet(memUse="FUNCTION", prototype="(double avg)", alias={"query","map"})
		public Object query(Object... values) {
			java.util.Arrays.sort(values);
			int idx = (int) Math.floor(values.length/2);

			//If an incomplete median can averaged...do so
			if (values.length%2 != 0 
					&& values[idx] instanceof Number && values[idx+1] instanceof Number) {
				return (((Number) values[idx]).doubleValue() + ((Number)values[idx+1]).doubleValue())/2;
			}
			
			return values[idx];
		}
	}
	
	/**Returns the median value of a range.  The median is defined
	 * as either the middle-most value (when there are an odd number of elements
	 * in the range) or the mean of the two middle-most values.  Median is computed
	 * the same for full range as sub-range.
	 */
	@Operator()
	public static class Median extends AbstractOperator {
		protected Median(OperatorData opData) {super(opData);}
		
		@Facet(memUse="FUNCTION", prototype="(double avg)", alias={"query","map"})
		public double query(double... args) {
			if (args.length == 0) {throw new RuntimeException("Cannot compute median on empty list");}

			java.util.Arrays.sort(args);
			int idx = (int) Math.floor(args.length/2);

			if (idx != args.length/2) {
				return (args[idx] + args[idx+1])/2;
			}
			return args[idx];
		}
	}

	/**Returns the mode value of  range.  The Mode is the most commonly
	 * occurring entry in a range.  Mode is computed the same
	 * for full range as sub-range.
	 */
	@Operator()
	public static class Mode extends AbstractOperator {
		public Mode(OperatorData opData) {super(opData);}
		
		@Facet(memUse="FUNCTION", prototype="(double avg)", alias={"query","map"})
		public double query(Double... args) {
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
	}

	public StencilOperator instance(String name, Context context, Specializer specializer) 
		throws SpecializationException,IllegalArgumentException {

		validate(name, specializer);
		
		try {
		if (name.equals("Mean")) {
			if (context.highOrderUses("Range").size() ==0) {return super.instance(name, context, specializer);}
			
			Specializer spec = Freezer.specializer(context.highOrderUses("Range").get(0).findDescendant(StencilParser.SPECIALIZER));
			Range range = new Range(spec.get(Range.RANGE_KEY));

			if (range.isFullRange()) {
				StencilOperator op = new FullMean(ModuleDataParser.operatorData(FullMean.class, "Average"), specializer, range.getStart());
				return op;
			} 
			return super.instance(name, context, specializer);}	

		} catch(Exception e) {throw new Error("Error locating method to invoke in Average package.", e);}
		throw new Error("Unannticiapted argumente combination");
	}

}
