package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Operator;
import stencil.interpreter.tree.Specializer;

@Operator(spec="[width: 500, height: 500, steps: 50]")
public final class KKLayout extends GraphOperator.StepOperator {
	public KKLayout(OperatorData opData, Specializer spec) {
		super(opData, spec);
	}
	
	protected void resetLayout() {
		edu.uci.ics.jung.algorithms.layout.KKLayout l = new edu.uci.ics.jung.algorithms.layout.KKLayout(new DelegateForest(graph));
		l.setMaxIterations(maxIterations);
		l.setSize(size);
		l.initialize();
		super.setLayout(l);
	}
}
