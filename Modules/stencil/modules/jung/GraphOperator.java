package stencil.modules.jung;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.OperatorData;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;
import stencil.module.util.ann.*;


/**Utility class for building JUNG graph operators.  This covers 
 * graph creation/storage, layout storage and position queries.
 */
public abstract class GraphOperator extends AbstractOperator.Statefull {
	protected final DirectedGraph graph = new DirectedSparseGraph();
	protected Layout<Object, Object> layout;	
	
	protected GraphOperator(OperatorData opData) {super(opData);}

	/**Recalculate the layout.
	 * Implementations of this method must call 'setLayout'.
	 */
	protected abstract void resetLayout();
	
	protected void setLayout(Layout layout) {this.layout = layout;}
	
	///---------------------- Edge Methods ----------------------------------------------------------------------------------------
	
	@Description("Add an edge to graph.  Must supply start and end; optionally include an identifier for the edge.")
	@Facet(memUse="WRITER", prototype="(boolean contained)", counterpart="hasEdge")
	public boolean addEdge(Object... values) {
		Object start = values[0];
		Object end = values[1];

		Object id;
		if (values.length == 2) {id = generateEdgeID(start, end);}
		else {id = values[2];}
		
		graph.addEdge(id, start,end, EdgeType.DIRECTED);

		stateID++;
		layout = null;

		return true;	
	}
	
	@Description("Is there an edge in the graph.  Identified with two values as from v1 to v2  OR one value as the ID")
	@Facet(memUse="READER", prototype="(boolean contained)")
	public boolean hasEdge(Object... values) {
		if (values.length == 2) {
			Object start = values[0];
			Object end = values[1];
			return graph.containsEdge(generateEdgeID(start, end));
		} else {
			return graph.containsEdge(values[0]);
		}
	}
	
	@Description("Standard ID generator")
	@Facet(memUse="FUNCTION", prototype="(String ID)")
	public String generateEdgeID(Object start, Object end) {
		return String.format("%1$s <-> %2$s", start, end);
	}
	
	//TODO: Add edge map/query: return a pair of points
	
	///---------------------- Vertex Methods ---------------------------------------------------------------------------------------- 
	
	@Description("Add a vertext to the graph.")
	@Facet(memUse="READER", prototype="(boolean contained)", counterpart="hasVertex")
	public boolean addVertex(Object v) {
        graph.addVertex(v);
		stateID++;
		layout = null;
	
        return true;
	}

	@Description("Is there an vertex in the graph.")
	@Facet(memUse="READER", prototype="(boolean contained)")
	public boolean hasVertex(Object v) {
		return graph.containsVertex(v);
	}

	

	@Facet(memUse="WRITER", prototype="(double X, double Y)", counterpart="queryVertex")
	public Point2D mapVertex(Object... values) {
		addVertex(values);
		return queryVertex(values);
	}

	@Description("What is the position of the vertex with the given ID")
	@Facet(memUse="READER", prototype="(double X, double Y)", counterpart="query")
	public Point2D queryVertex(Object id) {
		if (layout == null) {resetLayout();}
		return layout.transform(id);
	}
	
	
	
	/**Utility class for graph operators covering layouts that use a size factor.
	 * @author jcottam
	 *
	 */
	public static abstract class SizedOperator extends GraphOperator {
		private static final String WIDTH_KEY = "width";
		private static final String HEIGHT_KEY = "height";	
		protected final Dimension size;
		
		protected SizedOperator(OperatorData opData, Specializer spec) {
			super(opData);
			
			int width = Converter.toInteger(spec.get(WIDTH_KEY));
			int height = Converter.toInteger(spec.get(HEIGHT_KEY));
			size = new Dimension(width, height);
		}
	}
	
	
	@Override
	public GraphOperator duplicate() {
		GraphOperator l;
		try {l = (GraphOperator ) this.clone();
		} catch (CloneNotSupportedException e) {throw new UnsupportedOperationException();}
		l.resetLayout();
		return l;
	}


	
	/**Utility class for layout operators that use step-wise refinement.
	 * Supports retrieval of max-iterations, but may ignore it (depending on the layout's definition of 'done')
	 * 
	 * TODO: something about iterating until no "real" changes are made.
	 * 
	 * @author jcottam
	 *
	 */
	public static abstract class StepOperator extends SizedOperator {
		private static final String STEPS_KEY = "steps";
		protected IterativeContext layout;
		protected final int maxIterations;
		
		protected StepOperator(OperatorData opData, Specializer spec) {
			super(opData, spec);
			maxIterations = Converter.toInteger(spec.get(STEPS_KEY));
		}
		
		public void setLayout(Layout layout) {
			this.layout = (IterativeContext) layout;	
			super.setLayout(layout);
		}
		
		public Point2D queryVertex(String id) {
			if (super.layout == null) {resetLayout();}
			return super.queryVertex(id);
		}
		
		
		@Facet(memUse="READER", prototype="(int VALUE)")
		public int stateID() {
			if (layout == null) {resetLayout();}
//			if (!layout.done()) {
//				layout.step();
//				stateID++;
//			}
			return stateID++;
		}
		
		@Override
		public StepOperator viewpoint() {
			if (layout == null) {resetLayout();}
			return (StepOperator) super.viewpoint();
		}
	}

}
