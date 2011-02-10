package stencil.modules.stencilUtil;

import static stencil.parser.ParserConstants.FALSE_STRING;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;

/**Returns what was passed in, but records the range of elements
 * seen.  Used for continuous operators and should probably never
 * be used directly.*/
@Operator()
public final class SeedContinuous extends SeedBase {
	public static final String NAME = SeedContinuous.class.getSimpleName();
	public static final String MAX_KEY = "max";
	public static final String MIN_KEY = "min";
	public static final String LOCK_KEY = "lock";

	private double max = Double.MIN_VALUE;	/**Largest value in last reporting cycle*/
	private double min = Double.MAX_VALUE;	/**Smallest value in last reporting cycle*/
	private final boolean rangeLock;

	public SeedContinuous(OperatorData opData, boolean lock) {super(opData); this.rangeLock=lock;}
	public SeedContinuous(OperatorData opData, Specializer spec) throws SpecializationException {
		super(opData);
		
		if (spec.containsKey(MAX_KEY)) {max = Converter.toDouble(spec.get(MAX_KEY));}
		if (spec.containsKey(MIN_KEY)) {min = Converter.toDouble(spec.get(MIN_KEY));}
		rangeLock = spec.containsKey(LOCK_KEY) && spec.get(LOCK_KEY).equals(FALSE_STRING);
	}
	
	public StencilOperator duplicate() {return new SeedContinuous(operatorData, rangeLock);}

	public SampleSeed getSeed() {
		synchronized(this) {return new SampleSeed(true, min, max);}
	}

	@Facet(memUse="OPAQUE", prototype="(VALUE)")
	public Tuple map(Object... args) {
		assert args.length == 1;
		double value = Converter.toNumber(args[0]).doubleValue();
		
		if (!rangeLock) {
		
			double oldMax = max;
			double oldMin = min;
			
			synchronized(this) {
				max = Math.max(value, max);
				min = Math.min(value, min);
			}
			if ((max != oldMax) || (min != oldMin)) {
				stateID++;
			}
		}
		
		return Tuples.EMPTY_TUPLE;
	}
	
	public SeedContinuous viewpoint() {
		SeedContinuous rv = new SeedContinuous(operatorData, rangeLock);
		rv.max = this.max;
		rv.min = this.min;
		try {return (SeedContinuous) clone();}
		catch (Exception e) {throw new Error("Error making viewpoint of seed operator.");}
	}
}