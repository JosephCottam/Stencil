package stencil.modules.stencilUtil;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.module.SpecializationException;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.tuple.Tuple;

/**Can monitor a continuous mixed with a discrete space.
 * All numeric values are assumed to be part of the same continuous space,
 * all other values are assumed to be distinct categories.
 * 
 * @author jcottam
 *
 */
@Operator()
public class MonitorFlex extends MonitorBase<MonitorFlex> {
	private final MonitorContinuous cont;
	private final MonitorCategorical cat;
	
	
	private MonitorFlex(OperatorData opData, MonitorContinuous cont, MonitorCategorical cat) {
		super(opData);
		this.cat = cat;
		this.cont = cont;
	}
	
	public MonitorFlex(OperatorData opData, Specializer spec) throws SpecializationException {
		super(opData);
		cont = new MonitorContinuous(opData, spec);
		cat = new MonitorCategorical(opData, spec);
	}
	
	@Facet(memUse="OPAQUE", prototype="()")
	public Tuple map(Object... args) {
		try {
			return cont.map(args);
		} catch (NumberFormatException e) {
			return cat.map(args);
		}
	}
	
	@Override 
	public MonitorFlex duplicate() {return new MonitorFlex(operatorData, cont.duplicate(), cat.duplicate());}
	
	@Override
	public SampleSeed getSeed() {return new SampleSeed(cont.getSeed(), cat.getSeed());}

	@Override
	public int stateID() {return cont.stateID()^cat.stateID();}

	@Override
	public MonitorFlex viewpoint() {
		return new MonitorFlex(operatorData, cont.viewpoint(), cat.viewpoint());
	}

}
