package stencil.modules.stencilUtil;

import java.util.List;
import java.util.ArrayList;

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
@Operator()	
public final class MonitorConst extends MonitorBase<MonitorConst> {
	public static final String NAME = MonitorConst.class.getSimpleName();
	
	private final List<Object[]> seen = new ArrayList();
			
	public MonitorConst(OperatorData opData) {super(opData);}
	public MonitorConst(OperatorData opData, Specializer s) throws SpecializationException {super(opData);}
	public MonitorConst duplicate() throws UnsupportedOperationException {return new MonitorConst(operatorData);}
	
	public SampleSeed getSeed() {return new SampleSeed(CATEGORICAL, seen);}

	@Facet(memUse="OPAQUE", prototype="()")
	public Tuple map(Object... args) {
		if (seen.size() ==0) {addAll(args);}
		return Tuples.EMPTY_TUPLE;
	}
	
	private void addAll(Object... args) {
		for (int i=0; i<args.length; i++) {
			seen.add(new Object[]{args[i]});
		}
	}
	
	@Facet(memUse="READER", prototype="(int VALUE)")		
	public int stateID() {return seen.size();}
}