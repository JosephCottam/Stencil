package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;


public final class ISOMLayout extends GraphOperator.StepOperator {
	public ISOMLayout(OperatorData opData, Specializer spec) {
		super(opData, spec); 
	}
	
	
	protected void resetLayout() {
		edu.uci.ics.jung.algorithms.layout.ISOMLayout l = new edu.uci.ics.jung.algorithms.layout.ISOMLayout(new DelegateForest(graph));
		l.setSize(size);
		l.initialize();
		super.setLayout(l);
	}
}
