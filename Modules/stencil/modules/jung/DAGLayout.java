package stencil.modules.jung;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.interpreter.tree.Specializer;

@Operator(spec="[width: 500, height: 500, steps: 50]", defaultFacet="mapVertex")
public final class DAGLayout extends GraphOperator.StepOperator {
	public DAGLayout(OperatorData opData, Specializer spec) {
		super(opData, spec); 
	}
	
	protected void resetLayout() {
		edu.uci.ics.jung.algorithms.layout.DAGLayout l = new edu.uci.ics.jung.algorithms.layout.DAGLayout(new DelegateForest(graph));
		l.setSize(size);
		l.initialize();
		super.setLayout(l);
	}
	
	@Description("Set the root vertex of the DAG.")
	@Facet(memUse="OPAQUE", prototype="()")
	public void setRoot(Object v) {
		((edu.uci.ics.jung.algorithms.layout.DAGLayout) super.layout).setRoot(v);
	}
}
