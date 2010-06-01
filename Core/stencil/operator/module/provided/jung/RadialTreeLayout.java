package stencil.operator.module.provided.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;


final class RadialTreeLayout extends GraphOperator.SizedOperator {
	public RadialTreeLayout(OperatorData opData, Specializer spec) {
		super(opData, spec); 
	}


	protected void resetLayout() {
		edu.uci.ics.jung.algorithms.layout.RadialTreeLayout cLayout = new edu.uci.ics.jung.algorithms.layout.RadialTreeLayout(new DelegateForest(graph));
		cLayout.setSize(size);
		cLayout.reset();
		setLayout(cLayout);
	}
}
