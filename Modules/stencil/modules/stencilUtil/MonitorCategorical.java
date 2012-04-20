package stencil.modules.stencilUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.module.SpecializationException;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

import static stencil.interpreter.guide.SampleSeed.SeedType.*;

/**Returns exactly what it was passed, but
 * records elements seen (for categorization).*/
@Operator(defaultFacet="map")	
public final class MonitorCategorical extends MonitorBase<MonitorCategorical> {
	public static final String NAME = MonitorCategorical.class.getSimpleName();
	
	private final List<Object[]> seen = new CopyOnWriteArrayList();
			
	public MonitorCategorical(OperatorData opData) {super(opData);}
	public MonitorCategorical(OperatorData opData, Specializer s) throws SpecializationException {super(opData);}
	@Override
	public MonitorCategorical duplicate() throws UnsupportedOperationException {return new MonitorCategorical(operatorData);}
	
	@Override
	public SampleSeed getSeed() {return new SampleSeed(CATEGORICAL, seen);}

	@Facet(memUse="OPAQUE", prototype="()", counterpart="query")
	public Tuple map(Object... args) {
		if (!deepContains(seen, args)) {seen.add(args);}
		return Tuples.EMPTY_TUPLE;
	}
	
	@Facet(memUse="READER", prototype="(VALUE)")
	public Tuple query(Object... args) {return Tuples.EMPTY_TUPLE;}
	
	private static final boolean deepContains(List<Object[]> list, Object[] candidate) {
		for (Object[] e: list) {if (Arrays.deepEquals(e, candidate)) {return true;}}
		return false;
	}
	
	@Override
	@Facet(memUse="READER", prototype="(int VALUE)")		
	public int stateID() {return seen.size();}
}