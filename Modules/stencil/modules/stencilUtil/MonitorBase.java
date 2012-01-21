/**
 * 
 */
package stencil.modules.stencilUtil;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.MonitorOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.OperatorData;

public abstract class MonitorBase<T extends MonitorBase> extends AbstractOperator.Statefull<T> implements MonitorOperator<T>, Cloneable {
	protected MonitorBase(OperatorData opData) {super(opData);}

	@Override
	public abstract SampleSeed getSeed();
}