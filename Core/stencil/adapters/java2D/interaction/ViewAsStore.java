package stencil.adapters.java2D.interaction;

import stencil.display.Display;
import stencil.interpreter.TupleStore;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TupleFieldDef;

public class ViewAsStore implements TupleStore {

	@Override
	public boolean canStore(Tuple t) {return t!= null && t instanceof PrototypedTuple;}

	@Override
	public String getName() {return "View";}

	@Override
	public void store(Tuple t) {
		ViewTuple view = (ViewTuple) Display.view;
		PrototypedTuple<TupleFieldDef> pt = (PrototypedTuple) t;
		
		for (TupleFieldDef field: pt.prototype()) {
			if (view.prototype().contains(field.name())) {
				view.set(field.name(), pt.get(field.name()));
			}
		}
	}
	
	public stencil.display.ViewTuple viewTuple() {return Display.view;}
}
