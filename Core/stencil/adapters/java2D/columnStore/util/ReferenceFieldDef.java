package stencil.adapters.java2D.columnStore.util;

import java.util.ArrayList;
import java.util.List;

import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.TuplePrototype;


/**Column type information for reference columns.
 * Reference columns are special because they need to be able to report the prototype of the column referred to.
 */
public class ReferenceFieldDef extends SchemaFieldDef {
	private final TuplePrototype prototype;

	public ReferenceFieldDef(String name, TuplePrototype prototype) {
		super(name, new ArrayList(), List.class, false);
		this.prototype = prototype;
	}

	public TuplePrototype prototype() {return prototype;}
	
	
	public ReferenceFieldDef rename(String newName) {return new ReferenceFieldDef(newName, prototype);}
	public ReferenceFieldDef rePrototype(TuplePrototype prototype) {return new ReferenceFieldDef(name(), prototype);}
	public ReferenceFieldDef modify(String name, TuplePrototype prototype) {return new ReferenceFieldDef(name, prototype);}
}
