package stencil.modules;

import java.util.*;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;

/**
 * A module of misc utilities that I haven't figured out where they really belong yet.
 */
@Module
public class Temp extends BasicModule {
	
	/**Perform a linear interpolation.*/
	@Operator(spec="[]")
	public static final class LinearInterp extends AbstractOperator {
		public LinearInterp(OperatorData opData) {super(opData);}
		
		@Facet(memUse="FUNCTION", prototype="(double value)", alias={"map","query"})
		public double query(double step, double steps, double min, double max) {
			return ((step/steps) * (min-max)) + min; 
		}
	}
	

	/**Takes a range of values and creates a set of partitions of that range.
	 * Will report what partition any value falls in.  This is done with modulus
	 * if a value is outside the original range and with abs if outside in a negative manner.
	 */
	@Operator(spec="[n:10]")
	public static final class Partition extends AbstractOperator.Statefull {
		private static final String MIN = "min";
		private static final String MAX = "max";
		private static final String BUCKETS = "n";
		
		private final double buckets;
		private final boolean autoMin;
		private final boolean autoMax;

		private double min;
		private double max;
		
		public Partition(OperatorData od, Specializer spec) {
			super(od);

			buckets = Converter.toDouble(spec.get(BUCKETS));

			autoMin = !spec.containsKey(MAX);
			autoMax = !spec.containsKey(MIN);
			max = autoMax ? Double.MIN_VALUE : Converter.toDouble(spec.get(MAX));
			min = autoMin ? Double.MAX_VALUE : Converter.toDouble(spec.get(MIN)); 
		}
		
		private Partition(OperatorData od, double buckets, boolean autoMin, boolean autoMax, double min, double max) {
			super(od);
			this.buckets=buckets;
			this.autoMax = autoMax;
			this.autoMin = autoMin;
			
			if (!autoMax) {this.max = max;}
			if (!autoMin) {this.min = min;}
		}

		private void updateSpan(double value) {
			if (autoMax && value > max) {max = value; stateID++;}
			if (autoMin && value < min) {min = value; stateID++;}
		}
		
		@Facet(memUse="READER", prototype="(double start, double end, int bucket)")
		public double[] query(double dv) {
			double bucketSpan = (max-min)/buckets;
			dv = dv-min;

			double bucket = (buckets)/((max-min)/dv);
			bucket = Math.abs(Math.floor(bucket));
			bucket = bucket%buckets;

			double start = bucket*bucketSpan;
			double end = (bucket +1)*bucketSpan;
			
			return new double[]{start,end,bucket};
		}
		
		@Facet(memUse="WRITER", prototype="(double start, double end, int bucket)")
		public double[] map(double dv) {
			updateSpan(dv);
			return query(dv);
		}
		
		public Partition duplicate() {
			return new Partition(operatorData, buckets, autoMin, autoMax, min, max);
		}
	}

	@Operator(spec="[style: \"CIRCLE\", states: 2]")
	public static class Oscillate extends AbstractOperator.Statefull {
		public static enum STYLE {CIRCLE, SINE, SINE2}
		
		public static final String STATES_KEY = "states";	//How many states should it oscillate through (may either be an int or a coma-separated list)
		public static final String STYLE_KEY = "style";	//circle (A B C A B C A B C)  or sine (A B C B A B C B A) or sine2 (A B C C B A A B C C B A)
		
		private final Object[] states;
		private final STYLE style;
		
		private int idx=0;
		private boolean goingUp = true;
		
		public Oscillate(OperatorData opData, Specializer spec) {
			super(opData);
			style = STYLE.valueOf(Converter.toString(spec.get(STYLE_KEY)).toUpperCase());
		
			Object[] candidate;
			try {
				int stateCount = Converter.toInteger(spec.get(STATES_KEY));
				candidate = new Integer[stateCount];
				for (int i =0; i< candidate.length; i++) {candidate[i] =i;}
			}
			catch (NumberFormatException ex) {
				String states  = Converter.toString(spec.get(STATES_KEY));
				candidate = states.split("\\s*,\\s*");
			}
			states = candidate;
			if (states.length ==1) {throw new RuntimeException("Oscillate specified with only one state: " + Arrays.deepToString(states));}
		}
		
		protected Oscillate(OperatorData opData, Object[] states, STYLE style) {
			super(opData);
			this.states = states;
			this.style = style;
		}
			
			
		@Facet(memUse="READER", prototype="(value)")
		public synchronized Object query() {
			return states[idx];
		}
		
		@Facet(memUse="WRITER", prototype="(value)")
		public synchronized Object map(){
			stateID++;
			Object rv = query();
			
			switch (style) {
			case CIRCLE : idx = (idx+1)%states.length; break;
			case SINE :
				if (goingUp) {
					idx = idx+1;
					if (idx == states.length) {
						idx = states.length-2;
						goingUp = false;
					} 
				} else {
					idx = idx -1;
					if (idx <0) {
						idx = 1;
						goingUp = true;
					}
				}
				break;
			case SINE2 :
				if (goingUp) {
					idx = idx+1;
					if (idx == states.length) {
						idx = states.length-1;
						goingUp = false;
					} 
				} else {
					idx = idx -1;
					if (idx <0) {
						idx = 0;
						goingUp = true;
					}
				}
				break;
			default : throw new Error("cycle style not covered: " + style.toString());
			}
			return rv;
		}
		
		public StencilOperator duplicate() {
			StencilOperator nop = new Oscillate(operatorData, states, style);
			return nop;
		}
		
	}
}
