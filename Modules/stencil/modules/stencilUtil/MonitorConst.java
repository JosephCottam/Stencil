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
import stencil.tuple.instances.MultiResultTuple;
import stencil.tuple.instances.Singleton;

import static stencil.interpreter.guide.SampleSeed.SeedType.*;

/**Returns exactly what it was passed, but
 * records elements seen (for categorization).*/
@Operator()	
public final class MonitorConst extends MonitorBase<MonitorConst> {
	public static final String NAME = MonitorConst.class.getSimpleName();
	
	private final List seen = new ArrayList();
			
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
		Tuple[] ts = new Tuple[args.length];
		for (int i=0; i<args.length; i++) {
			ts[i] = Singleton.from(args[i]);
		}
		seen.add(new MultiResultTuple(ts));
	}
	
	@Facet(memUse="READER", prototype="(int VALUE)")		
	public int stateID() {return seen.size();}
}