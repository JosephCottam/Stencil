package stencil.adapters.java2D.interaction;

import stencil.interpreter.TupleStore;
import stencil.module.util.ApplyView;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TupleFieldDef;

public class ViewAsStore implements TupleStore, ApplyView {
	private ViewTuple view;
	
	@Override
	public boolean canStore(Tuple t) {return t!= null && t instanceof PrototypedTuple;}

	@Override
	public String getName() {return "View";}

	@Override
	public void store(Tuple t) {
		PrototypedTuple<TupleFieldDef> pt = (PrototypedTuple) t;
		
		for (TupleFieldDef field: pt.prototype()) {
			if (view.prototype().contains(field.name())) {
				view.set(field.name(), pt.get(field.name()));
			}
		}
	}
	
	public stencil.display.ViewTuple viewTuple() {return view;}

	@Override
	public void setView(stencil.display.ViewTuple view) {this.view = (ViewTuple) view;}
	
}
