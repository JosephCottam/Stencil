package stencil.modules;

import java.awt.geom.Point2D;
import java.util.*;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.adapters.general.Registrations;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MapMergeTuple;
import stencil.tuple.prototype.TupleFieldDef;
import stencil.types.Converter;

/**
 * A module of misc utilities that I haven't figured out where they really belong yet.
 */
@Module
public class Temp extends BasicModule {
	
	@Operator(spec="[]")
	@Facet(memUse="FUNCTION", prototype="(Number abs)", alias={"map","query"})
	public static Number toNumber(Object v) {return Converter.toNumber(v);}

	@Operator(spec="[]")
	@Facet(memUse="FUNCTION", prototype="(Number abs)", alias={"map","query"})
	public static Long toLong(Object v) {return Converter.toLong(v);}

	
	
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

	@Description("Esseentially the inverse of a crosstab, for a single tuple. The first n arguments (set by the share parameter) are set in each result.  The remaning n are placed one-per tuple.")
	public static class Group extends AbstractOperator {
		public static final String SHARE_KEY= "share";
		
		final int share;
		protected Group(OperatorData opData, Specializer spec) {
			super(setProto(opData, spec));
			share = Converter.toInteger(spec.get(SHARE_KEY));
		}
		private static final OperatorData setProto(OperatorData opData, Specializer spec) {
			return opData;
		}
		
		@Facet(memUse="FUNCTION", alias={"map","query"})	//Prototype set by "fields"
		public MapMergeTuple query(Object... values) {
			int extra = values.length - share;
			
			Object[] base = new Object[share+1];
			Tuple[] tuples = new Tuple[extra];
			
			for (int i=0; i<tuples.length; i++) {
				Object[] result = Arrays.copyOf(base, base.length);
				result[share+1] = values[share+i];
				tuples[i] = new ArrayTuple(result);
			}
			return new MapMergeTuple(tuples);
		}
		
	}
	
	@Operator(spec="[reg:\"CENTER\"]")
	public static class Reg extends AbstractOperator {
		public static final String REG_KEY="reg";
		private static Registrations.Registration reg;
		
		public Reg(OperatorData opData, Specializer spec) {
			super(opData);
			reg = Registrations.Registration.valueOf(Converter.toString(spec.get(REG_KEY)));
		}
		
		@Facet(memUse="FUNCTION", alias={"map","query"}, prototype="(double X, double Y)")
		public Point2D query(PrototypedTuple<TupleFieldDef> t) {
			Registrations.Registration now = (Registrations.Registration) Converter.convert(t.get("REGISTRATION"), Registrations.Registration.class);
			double x = Converter.toDouble(t.get("X"));
			double y = Converter.toDouble(t.get("Y"));
			double w = Converter.toDouble(t.get("WIDTH"));
			double h = Converter.toDouble(t.get("HEIGHT"));
			Point2D topLeft = Registrations.registrationToTopLeft(now, x, y, w,h);
			
			return Registrations.topLeftToRegistration(reg, topLeft, w, h);
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
