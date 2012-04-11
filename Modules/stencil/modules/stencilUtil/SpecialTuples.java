package stencil.modules.stencilUtil;

import stencil.display.CanvasTuple;
import stencil.display.Display;
import stencil.display.ViewTuple;
import stencil.module.operator.util.DirectOperator;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.tuple.Tuple;
/**Holder for operators to access Stencil-provided tuples.
 * This includes View and Canvas.
 */
public abstract class SpecialTuples {
	public static final String VIEW_TUPLE_OP = "ViewTuple";
	public static final String CANVAS_TUPLE_OP = "CanvasTuple";
	
	@Operator(name=VIEW_TUPLE_OP, defaultFacet="query") 
	public static final class View extends DirectOperator {
		public View(OperatorData od) {super(od);}

		@Facet(memUse="READER", prototype=ViewTuple.PROTOTYPE_STRING, alias={"map", "query"})
		public Tuple op() {return Display.view;}
		
		public Tuple invoke(Object[] args) {return op();}
		public Tuple tupleInvoke(Object[] args) {return op();}
	}
	
	//TODO: Add specializer arg to force pre-render or not (add detection for needed/not to the insertion pass)
	//Is still be consistent with out it, but not what people expect
	@Operator(name=CANVAS_TUPLE_OP, defaultFacet="query")
	public static final class Canvas extends DirectOperator {
		public Canvas(OperatorData od) {super(od);}

		@Facet(memUse="READER", prototype=CanvasTuple.PROTOTYPE_STRING, alias={"map", "query"})
		public Tuple op() {
			return Display.canvas;
		}
		
		public Tuple invoke(Object[] args) {return op();}
		public Tuple tupleInvoke(Object[] args) {return op();}
	}
}
