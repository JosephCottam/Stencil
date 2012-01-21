package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Operator;
import stencil.interpreter.tree.Specializer;

@Operator(spec="[width: 500, height: 500]", defaultFacet="mapVertex")
public final class BalloonLayout extends GraphOperator.SizedOperator {
	private final edu.uci.ics.jung.algorithms.layout.BalloonLayout storedLayout;
	
	public BalloonLayout(OperatorData opData, Specializer spec) {
		super(opData, spec);
		storedLayout = new edu.uci.ics.jung.algorithms.layout.BalloonLayout(new DelegateForest());
	}

	
	protected void resetLayout() {
		storedLayout.setGraph(new DelegateForest(graph));
		storedLayout.reset();
		storedLayout.setSize(size);
		setLayout(storedLayout);
	}
}
