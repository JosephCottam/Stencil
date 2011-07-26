package stencil.adapters.java2D.interaction;

import stencil.adapters.java2D.Canvas;
import stencil.display.Display;
import stencil.interpreter.TupleStore;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;

public class CanvasAsStore implements TupleStore {

	@Override
	public boolean canStore(Tuple t) {return t!= null && t instanceof PrototypedTuple;}

	@Override
	public String getName() {return "Canvas";}

	@Override
	public void store(Tuple t) {
		PrototypedTuple pt = (PrototypedTuple) t;
		int idx = pt.prototype().indexOf(CanvasTuple.BACKGROUND_COLOR);
		if (idx >=0) {
			((CanvasTuple) Display.canvas).set(CanvasTuple.BACKGROUND_COLOR, t.get(idx));
		}
	}
	
	public Canvas canvas() {return (Canvas) Display.canvas.getComponent();}
}
