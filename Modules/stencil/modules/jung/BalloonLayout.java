package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;


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
