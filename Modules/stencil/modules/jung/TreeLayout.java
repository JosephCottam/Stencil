package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Operator;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;

@Operator(spec="[range: ALL, split: 0, distX: 50, distY: 50]")
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
