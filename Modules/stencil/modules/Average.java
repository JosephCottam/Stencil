package stencil.modules;

import java.util.HashMap;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.modules.stencilUtil.range.Range;
import stencil.modules.stencilUtil.range.RangeDescriptor;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.Context;
import stencil.parser.tree.StencilTree;
import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;

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
	@Operator(name="FullMean", defaultFacet="map")
	public static class FullMean extends AbstractOperator.Statefull {
		int start;
		double total =0;
		long count=0;

		public FullMean(OperatorData opData) {
			super(opData);			
		}
		
		private FullMean(OperatorData opData, int start) {
			super(opData);
			this.start = start;
		}

		@Facet(memUse="WRITER", prototype="(double avg)", counterpart="query")
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
	@Operator(name="Mean")
	@Facet(memUse="FUNCTION", prototype="(double avg)", alias={"query","map"})
	public static double mean(double...values) {
		double sum =0;
		for (double value: values) {sum += value;}
		return sum/values.length;
	}

	
	/**Returns the median value of a range.  The median is defined
	 * as either the middle-most value (when there are an odd number of elements
	 * in the range) or the mean of the two middle-most values.  Median is computed
	 * the same for full range as sub-range.
	 */
	@Operator(defaultFacet="map")
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
	//TODO: Provide an associative-map (Object->Count) based Mode for full-range optimization
	@Operator(defaultFacet="map")
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
	
	
	@Override
	public StencilOperator optimize(StencilOperator op, Context context) {
		if (op.getName().equals("Mean") 
			&& context.callSites().size() == 1
			&& context.callSites().get(0).is(StencilParser.OP_AS_ARG)) {
			StencilTree site = context.callSites().get(0).getAncestor(StencilParser.FUNCTION);
			MultiPartName name = Freezer.multiName(site.find(StencilParser.OP_NAME));

			//--------------------------------------------------------------------------------------------------------
			//HACK: This is a bad idea...but I don't have a better one right now!
			boolean isRange = name.name().contains("Range"); 
			//--------------------------------------------------------------------------------------------------------
			
			if (!isRange) {return op;}	//Can only optimize ranges...
			
			Specializer spec = Freezer.specializer(site.find(StencilParser.SPECIALIZER));
			if (!spec.containsKey(Range.RANGE_KEY)) {return op;}
			
			RangeDescriptor r = new RangeDescriptor(spec.get(Range.RANGE_KEY));
			if (r.isFullRange()) {
				OperatorData od=ModuleDataParser.operatorData(FullMean.class, this.getName());
				od = od.name(op.getName());
				return new FullMean(od);
			}
		}
		return op;
	}
}
