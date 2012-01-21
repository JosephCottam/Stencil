package stencil.modules.stencilUtil;

import static stencil.interpreter.guide.SampleSeed.SeedType.*;

import java.util.ArrayList;
import java.util.List;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.module.SpecializationException;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.MultiResultTuple;
import stencil.types.Converter;

/**Returns what was passed in, but records the range of elements
 * seen.  Used for continuous operators and should probably never
 * be used directly.*/
@Operator(defaultFacet="map")
public final class MonitorContinuous extends MonitorBase<MonitorContinuous> {
	public static final String NAME = MonitorContinuous.class.getSimpleName();
	public static final String MAX_KEY = "max";
	public static final String MIN_KEY = "min";
	public static final String LOCK_KEY = "lock";

	private double max = Double.NEGATIVE_INFINITY;	/**Largest value in last reporting cycle*/
	private double min = Double.POSITIVE_INFINITY;	/**Smallest value in last reporting cycle*/
	private final boolean rangeLock;

	public MonitorContinuous(OperatorData opData, boolean lock) {super(opData); this.rangeLock=lock;}
	public MonitorContinuous(OperatorData opData, Specializer spec) throws SpecializationException {
		super(opData);
		
		if (spec.containsKey(MAX_KEY)) {max = Converter.toDouble(spec.get(MAX_KEY));}
		if (spec.containsKey(MIN_KEY)) {min = Converter.toDouble(spec.get(MIN_KEY));}
		rangeLock = spec.containsKey(LOCK_KEY) && !Converter.toBoolean(spec.get(LOCK_KEY));
	}
	
	@Override
	public MonitorContinuous duplicate() {return new MonitorContinuous(operatorData, rangeLock);}

	@Override
	public SampleSeed getSeed() {
		List l = new ArrayList(2);
		if (!Double.isInfinite(min)) {l.add(min);}
		if (!Double.isInfinite(max)) {l.add(max);}
		return new SampleSeed(CONTINUOUS, l);
	}
	
	@Facet(memUse="OPAQUE", prototype="()", counterpart="query")
	public Tuple map(Object value) {
		if (value instanceof MultiResultTuple) {
			mapAll((MultiResultTuple) value);
		} else {
			mapOne(Converter.toDouble(value));
		}
		return Tuples.EMPTY_TUPLE;
	}
	
	@Facet(memUse="READER", prototype="(VALUE)")
	public Tuple query(Object... args) {return Tuples.EMPTY_TUPLE;}
	
	private void mapAll(final MultiResultTuple values) {
		for (int i=0; i< values.size(); i++) {
			Tuple v = values.getTuple(i);
			mapOne(Converter.toDouble(v.get(0)));	//HACK: Relies on only needing the first field...
		}
	}
	
	private void mapOne(double value) {
		if (!rangeLock) {
			double oldMax = max;
			double oldMin = min;
			
			synchronized(this) {
				max = Double.isNaN(value) ? max : Math.max(value, max);
				min = Double.isNaN(value) ? min : Math.min(value, min);
			}
			if ((max != oldMax) || (min != oldMin)) {
				stateID++;
			}
		}
	}
	
}