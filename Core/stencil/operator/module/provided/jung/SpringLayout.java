package stencil.operator.module.provided.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;


final class SpringLayout extends GraphOperator.StepOperator {
	public SpringLayout(OperatorData opData, Specializer spec) {
		super(opData, spec); 
	}
	
	protected void resetLayout() {
		edu.uci.ics.jung.algorithms.layout.SpringLayout l = new edu.uci.ics.jung.algorithms.layout.SpringLayout(new DelegateForest(graph));
		l.setSize(size);
		l.initialize();
		super.setLayout(l);
	}
	
}
