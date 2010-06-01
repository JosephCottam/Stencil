package stencil.operator.module.provided.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;


final class KKLayout extends GraphOperator.StepOperator {
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
