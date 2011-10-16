package stencil.modules;

import java.awt.geom.Point2D;
import java.util.*;

import stencil.module.OperatorInstanceException;
import stencil.module.ModuleCache;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.modules.stencilUtil.MonitorSegments;
import stencil.modules.stencilUtil.MonitorSegments.Segment;
import stencil.parser.string.util.Context;
import stencil.adapters.general.Registrations;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MultiResultTuple;
import stencil.tuple.instances.NumericSingleton;
import stencil.tuple.prototype.TupleFieldDef;
import stencil.types.Converter;

import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
import static stencil.parser.ParserConstants.OP_ARG_PREFIX;

/**
 * A module of misc utilities that I haven't figured out where they really belong yet.
 */
@Module
public class Temp extends BasicModule {
		
	@Operator(spec="[]")
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	public static Object[] echo(Object... vs) {return vs;}
	
	@Operator(spec="[]")
	@Facet(memUse="FUNCTION", prototype="(Number abs)", alias={"map","query"})
	public static Number toNumber(Object v) {return Converter.toNumber(v);}

	@Operator(spec="[]")
	@Facet(memUse="FUNCTION", prototype="(Number abs)", alias={"map","query"})
	public static Long toLong(Object v) {return Converter.toLong(v);}

	@Operator(spec="[margin:1]")
	public static final class DiscreteMetricRank extends AbstractOperator.Statefull {
		public static final String MARGIN_KEY = "margin";
		private final List<Segment> segments = new ArrayList();
		private final double gapSize;

		public DiscreteMetricRank(OperatorData opData, double gapSize) {super(opData); this.gapSize = gapSize;}
		public DiscreteMetricRank(OperatorData opData, Specializer spec) throws SpecializationException {
			super(opData);
			gapSize = Converter.toDouble(spec.get(MARGIN_KEY));
		}

		@Override
		public MonitorSegments duplicate() {return new MonitorSegments(operatorData, gapSize);}

		@Facet(memUse="WRITER", prototype="(int rank)")
		public long map(long value) {return rank(true, value);} 
		
		@Facet(memUse="READER", prototype="(int rank)")
		public long query(long value) {return rank(false, value);}
		
		@Override
		public DiscreteMetricRank viewpoint() {
			DiscreteMetricRank dmRank = new DiscreteMetricRank(operatorData, gapSize);
			dmRank.segments.addAll(segments);
			return dmRank;
		}
		
		private long rank(boolean add, long value) {
			int index;
			Segment inSegment=null;
			for (index=0; index<segments.size(); index++) {
				Segment s = segments.get(index);
				if (s.contains(value, gapSize)) {inSegment = s; break;}
				if (s.follows(value)) {break;}
			}

			if (add) {
				if (inSegment != null) {				//Fell within the tolerance of an existing range
					Segment newSegment = inSegment.extend(value);
					if (newSegment != inSegment) {
						stateID++;
						segments.set(index, newSegment);
						inSegment = newSegment;
						
						//Cleanup list, merging overlapping neighbors
						int segmentIdx = segments.indexOf(inSegment);
						Segment before = segmentIdx < 1 ? null : segments.get(segmentIdx-1);
						Segment after = segmentIdx > segments.size() -2 ? null : segments.get(segmentIdx+1);
						if (Segment.shouldMerge(inSegment, after, gapSize)) {
							inSegment = inSegment.merge(after);
							segments.set(segmentIdx, inSegment);
							segments.remove(segmentIdx+1);
						}
						if (Segment.shouldMerge(before, inSegment, gapSize)) {
							inSegment = inSegment.merge(before);
							segments.set(segmentIdx-1, inSegment);
							segments.remove(segmentIdx);
						}
					}
					
				} else if (index >=0) { //New value after all prior values
					inSegment = new Segment(value, value);
					segments.add(index, inSegment);
					stateID++;
				} else {
					throw new Error(String.format("Unhandled case in discrete metric rank-- index: %1$s, segments: %2$s, oldSegment: %3$s: ", index, segments.size(), inSegment));
				}
			}
			
			if (inSegment ==null) {return -1;}
			else {
				long rank=-1;
				for (Segment s: segments) {
					rank++;
					if (s==inSegment) {rank = rank + (value - (long) s.start); break;}
					rank = rank + (long) (s.end-s.start);
				}
				return rank;
			}
			
		}
		
	}
	
	
	@Operator(spec="[]")
	public static final class AutoID extends AbstractOperator.Statefull {
		private int id=0;
		
		public AutoID(OperatorData opData) {super(opData);}
		
		@Facet(memUse="WRITER", prototype="(Integer id)")
		public MultiResultTuple map(int count) {
			MultiResultTuple  rs = query(count);
			id += count;
			return rs;
		}
		
		@Facet(memUse="READER", prototype="(Integer id)")
		public MultiResultTuple query(int count) {
			if (count <1) {throw new IllegalArgumentException("Invalid argument for auto id, must be a positive value.");}
			Tuple[] rs = new  Tuple[count];
			for (int i=0; i<count; i++) {
				rs[i] = new NumericSingleton(id+i);	//Don't increment id: this is the query facet
			}
			return new MultiResultTuple(rs);
		}
	}
	

	@Description("Applies multiple functions to the values passed.  Each function is invoked once, taking all values at once.\n"
					+ "So MapApply(@Sum, @Mult, 1 2 3 4) results in applciations of Sum(1,2,3,4) and Mult(1,2,3,4) producing the tuple\n"
					+ "(9,24)")
	@Operator(spec="[]", tags=stencil.module.util.OperatorData.HIGHER_ORDER_TAG)
	public static class MapApply extends AbstractOperator {
		final StencilOperator[] ops;
		
		protected MapApply(OperatorData opData, Specializer spec, List<StencilOperator> opArgs) {
			super(opData);
			ops = opArgs.toArray(new StencilOperator[opArgs.size()]);
		}
		
		//TODO: Inherit statefullness from specializer (any look at facet applications)
		@Facet(memUse="OPAQUE", prototype="()", alias={"map","query"})
		public MultiResultTuple map(Object... values) {
			Tuple[] results = new Tuple[ops.length];
			for (int i=0; i<ops.length; i++) {
				results[i] = ops[i].getFacet("map").tupleInvoke(values);
			}
			return new MultiResultTuple(results);
		}
		
	}
	
	
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
		public MultiResultTuple query(Object... values) {
			int extra = values.length - share;
			
			Object[] base = new Object[share+1];
			Tuple[] tuples = new Tuple[extra];
			
			for (int i=0; i<tuples.length; i++) {
				Object[] result = Arrays.copyOf(base, base.length);
				result[share+1] = values[share+i];
				tuples[i] = new ArrayTuple(result);
			}
			return new MultiResultTuple(tuples);
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


	public StencilOperator instance(String name, Context context, Specializer specializer, ModuleCache modules) throws SpecializationException {
		List<StencilOperator> opArgs = new ArrayList();

		
		//TODO: This is a horrible way to resolve things, have the operator as value in a CONST in the specializer instead of the name
		for (String key: specializer.keySet()) {
			if (key.startsWith(OP_ARG_PREFIX)) {
				StencilOperator op;
				try {
					op = modules.instance((MultiPartName) specializer.get(key), null, EMPTY_SPECIALIZER, false);
					opArgs.add(op);
				} catch (OperatorInstanceException e) {throw new IllegalArgumentException("Error instantiate operator-as-argument " + specializer.get(key), e);}
			}
		}
		
		
		if (name.equals("MapApply")) {
			return new MapApply(getOperatorData(name, specializer), specializer, opArgs);
		} else {
			return super.instance(name, context, specializer, modules);
		}
	}
}
