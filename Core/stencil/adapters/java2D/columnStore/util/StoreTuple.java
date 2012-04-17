package stencil.adapters.java2D.columnStore.util;

import static stencil.parser.ParserConstants.IDENTIFIER_FIELD;
import static stencil.parser.ParserConstants.VISIBLE_FIELD;
import static stencil.parser.ParserConstants.BOUNDS_FIELD;

import java.awt.geom.Rectangle2D;

import stencil.adapters.java2D.columnStore.column.Column;
import stencil.display.Glyph;
import stencil.display.SchemaFieldDef;
import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;


/**Tuple representing a row in a table.
 * This is still tied to the table  itself, but using clone will make all storage independent of the table.
 */
public class StoreTuple implements Tuple, Cloneable, Glyph {
	private final Column[] columns;
	private int row;
	private final int idCol;
	private final int visibleCol;
	private final int boundsCol;
	private final TuplePrototype<SchemaFieldDef> schema;
	
	public StoreTuple(Column[] columns, TuplePrototype<SchemaFieldDef> schema, int row) {
		this(columns, schema, row,
				schema.indexOf(IDENTIFIER_FIELD),
				schema.indexOf(VISIBLE_FIELD),
				schema.indexOf(BOUNDS_FIELD));
	}
	
	public StoreTuple(Column[] columns, TuplePrototype<SchemaFieldDef> schema, int row,
						int idCol, int visibleCol, int boundsCol) {
		this.columns = columns;
		this.row = row;
		this.schema = schema;
		this.idCol = idCol;
		this.visibleCol = visibleCol;
		this.boundsCol = boundsCol;
	}
	
	public int row() {return row;}
	
	@Override
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}

	@Override
	public Object get(int idx) throws TupleBoundsException {return columns[idx].get(row);}

	@Override
	public TuplePrototype prototype() {return schema;}

	@Override
	public int size() {return columns.length;}
	
	/**Mutate the row this tuple points at.  Generally unsafe, but used to optimize iteration when only one tuple will be "in flight" at a time.**/
	protected void setRow(int row) {this.row = row;}
	
	/**Create a tuple that is independent of the backing store.
	 * This is advised whenever a tuple leaves the Stencil system or is stored
	 * in a data structure.  Otherwise, old versions of a table cannot be garbage collected.
	 * **/
	@Override
	public Tuple clone() {
		Object[] values = Tuples.toArray(this);
		Tuple t = new PrototypedArrayTuple(schema, values);
		return t;
	}

	@Override
	public Comparable getID() {return (Comparable) columns[idCol].get(row);}
	@Override
	public boolean isVisible() {return (Boolean) columns[visibleCol].get(row);}	
	@Override
	public String toString() {return Tuples.toString(this);}
	
	
	@Override
	public Rectangle2D getBoundsReference() {return ((Rectangle2D) columns[boundsCol].get(row));}
}
