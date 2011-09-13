package stencil.modules.stencilUtil;

import static stencil.interpreter.guide.SampleSeed.SeedType.*;

import java.util.ArrayList;
import java.util.List;

import stencil.interpreter.guide.MonitorOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.module.SpecializationException;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;

/**Creates a set of continuous segments, like a hybrid between continuous and categorical.*/
@Operator(spec="[margin: 1]")
public final class MonitorSegments extends MonitorBase<MonitorSegments> {
	public static final String NAME = MonitorSegments.class.getSimpleName();
	public static final String MARGIN_KEY = "margin";
	
	/**Encapsulation of a segment.**/
	public static final class Segment  {
		/**The smaller value of the segment.**/
		public final double start;
		/**The larger value of the segment.**/
		public final double end;
		public Segment(double start, double end) {
			assert end > start;
			this.start = start;
			this.end = end;
		}
		
		
		Segment extend(double value) {
			if (value < start) {return new Segment(value, end);}
			if (value > end) {return new Segment(start, value);}
			return this;
		}
		
		
		Segment merge(Segment other) {return new Segment(Math.min(start, other.start), Math.max(end, other.end));}

		/**Should the these two segments be merged?
		 * Assumes s1 comes before s2
		 */
		static boolean shouldMerge(Segment s1, Segment s2, double tollerance) {
			return  s1.end + tollerance >= s2.start;
		}
		
		boolean contains(double value, double tollerance) {
			return (value <= (end + tollerance))
				&& (value >= (start - tollerance));
		}
		
		/**Does this segment follow the given value? (This does not take tollerance into account.)**/
		boolean follows(double value) {return value < start;}
		
		public String toString() {return start +  "->" + end;}
	}
	
	private final List<Segment> segments = new ArrayList();
	private final double gapSize;

	public MonitorSegments(OperatorData opData, double gapSize) {super(opData); this.gapSize = gapSize;}
	public MonitorSegments(OperatorData opData, Specializer spec) throws SpecializationException {
		super(opData);
		gapSize = Converter.toDouble(spec.get(MARGIN_KEY));
	}
	
	@Override
	public MonitorSegments duplicate() {return new MonitorSegments(operatorData, gapSize);}

	@Override
	public SampleSeed getSeed() {
		ArrayList segs;
		synchronized(this) {segs = new ArrayList(segments);}
		SampleSeed seed = new SampleSeed(SEGMENTS, segs);
		return seed;
	}
	
	@Facet(memUse="OPAQUE", prototype="()", alias={"map","query"})
	public Tuple map(Object... args) {
		Double[] values = MonitorOperator.Util.values(args[0], Double.class);
		
		for (double value: values) {
			int index;
			Segment oldSegment=null;
			for (index=0; index<segments.size(); index++) {
				Segment s = segments.get(index);
				if (s.contains(value, gapSize)) {oldSegment = s; break;}
				if (s.follows(value)) {break;}
			}
	
			synchronized (this) {
				if (oldSegment != null) {				//Fell within the tollerance of an existing range
					Segment newSegment = oldSegment.extend(value);
					if (newSegment != oldSegment) {
						stateID++;
						segments.set(index, newSegment);
		
						//Cleanup list, merging overlapping neighbors
						for (int i=0; i< segments.size()-1; i++) {
							Segment s1 = segments.get(i);
							Segment s2 = segments.get(i+1);
							if (Segment.shouldMerge(s1,s2, gapSize)) {
								Segment ss = s1.merge(s2);
								segments.set(i, ss);
								segments.remove(i+1);
							}
						}
					}
					
				} else if (index >=0) { //New value after all prior values
					segments.add(index, new Segment(value, value));
					stateID++;
				} else {
					throw new Error(String.format("Unhandled case in gaps monitor -- index: %1$s, segments: %2$s, oldSegment: %3$s: ", index, segments.size(), oldSegment));
				}
			}
		}
		return Tuples.EMPTY_TUPLE;
	}
}