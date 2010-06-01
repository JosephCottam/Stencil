package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;


public final class TreeLayout extends GraphOperator {
	private static final String DIST_X = "distX";
	private static final String DIST_Y = "distY";
	private final int distX, distY;
	
	
	public TreeLayout(OperatorData opData, Specializer spec) {
		super(opData);

		distX = Converter.toInteger(spec.get(DIST_X));
		distY = Converter.toInteger(spec.get(DIST_Y));
	}

	protected void resetLayout() {
		setLayout(new edu.uci.ics.jung.algorithms.layout.TreeLayout(new DelegateForest(graph), distX, distY));
	}	
}
