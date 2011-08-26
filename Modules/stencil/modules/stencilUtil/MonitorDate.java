package stencil.modules.stencilUtil;

import static stencil.interpreter.guide.SampleSeed.SeedType.*;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import stencil.interpreter.guide.SampleSeed;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;


/**TODO:Generalize this with MonitorContinuous and MonitorSegments;
 * 		probably involves moving the synthetic max/min/rangelock to the sampler instead of the monitor...*/
@Operator()
public final class MonitorDate extends MonitorBase<MonitorDate> {
	public static final String NAME = MonitorDate.class.getSimpleName();
	public static final String MAX_KEY = "max";
	public static final String MIN_KEY = "min";
	public static final String LOCK_KEY = "lock";

	private Date max = null;
	private Date min = null;
	
	public MonitorDate(OperatorData opData) {
		super(opData);
	}
	
	@Override
	public MonitorDate duplicate() {return new MonitorDate(operatorData);}

	@Override
	public SampleSeed getSeed() {
		List l = new ArrayList(2);
		if (min != null) {l.add(min);}
		if (max != null) {l.add(max);}
		return new SampleSeed(CONTINUOUS, l);
	}
	
	@Facet(memUse="OPAQUE", prototype="()")
	public Tuple map(Object... args) {
		assert args.length == 1;
		Date value = (Date) Converter.convert(args[0], Date.class);
		
		if (max ==  null) {max = value; stateID++;}
		else if (max.compareTo(value) < 0) {max = value; stateID++;}
		
		if (min == null) {min=value; stateID++;}
		else if (min.compareTo(value) > 0) {min = value; stateID++;}
		
		return Tuples.EMPTY_TUPLE;
	}
}