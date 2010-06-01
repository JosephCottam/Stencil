package stencil.operator.module.provided.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;


final class FRLayout extends GraphOperator.StepOperator {
	public FRLayout(OperatorData opData, Specializer spec) {
		super(opData, spec); 
	}
	
	protected void resetLayout() {
		edu.uci.ics.jung.algorithms.layout.FRLayout l = new edu.uci.ics.jung.algorithms.layout.FRLayout(new DelegateForest(graph));
		l.setMaxIterations(maxIterations);
		l.setSize(size);
		l.initialize();
		super.setLayout(l);
	}
}
