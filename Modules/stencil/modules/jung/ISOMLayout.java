package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Operator;
import stencil.interpreter.tree.Specializer;

@Operator(spec="[width: 500, height: 500, steps: 50]", defaultFacet="mapVertex")
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
