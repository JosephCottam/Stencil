package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Operator;
import stencil.interpreter.tree.Specializer;

@Operator(spec="[width: 500, height: 500, steps: 50]", defaultFacet="mapVertex")
public final class FRLayout extends GraphOperator.StepOperator {
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
