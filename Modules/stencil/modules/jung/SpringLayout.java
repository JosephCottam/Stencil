package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Operator;
import stencil.interpreter.tree.Specializer;

@Operator(spec="[range: ALL, split: 0, width: 500, height: 500, steps: 50]")
public final class SpringLayout extends GraphOperator.StepOperator {
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
