package stencil.modules.stencilUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

/**Returns exactly what it was passed, but
 * records elements seen (for categorization).*/
@Operator()	
public final class SeedCategorize extends SeedBase {
	public static final String NAME = SeedCategorize.class.getSimpleName();
	
	private final List<Object[]> seen = new CopyOnWriteArrayList();
			
	public SeedCategorize(OperatorData opData) {super(opData);}
	public SeedCategorize(OperatorData opData, Specializer s) throws SpecializationException {super(opData);}
	public StencilOperator duplicate() throws UnsupportedOperationException {return new SeedCategorize(operatorData);}
	
	public synchronized SampleSeed getSeed() {return new SampleSeed(false, new ArrayList(seen));}

	@Facet(memUse="OPAQUE", prototype="(VALUE)")
	public Tuple map(Object... args) {
		if (!deepContains(seen, args)) {seen.add(args);}
		return Tuples.EMPTY_TUPLE;
	}
	
	private static final boolean deepContains(List<Object[]> list, Object[] candidate) {
		for (Object[] e: list) {if (Arrays.deepEquals(e, candidate)) {return true;}}
		return false;
	}
	
	@Facet(memUse="READER", prototype="(int VALUE)")		
	public int stateID() {return seen.size();}
	
	public SeedCategorize viewpoint() {
		try {return (SeedCategorize) clone();}
		catch (Exception e) {throw new Error("Error making viewpoint of seed operator.");}
	}
}