package stencil.adapters.java2D.columnStore;

import static stencil.parser.ParserConstants.IDENTIFIER_FIELD;
import static stencil.parser.ParserConstants.VISIBLE_FIELD;
import static stencil.parser.ParserConstants.BOUNDS_FIELD;
import static stencil.parser.ParserConstants.RENDER_ORDER_FIELD;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.column.ColumnUtils;
import stencil.adapters.java2D.columnStore.util.SimpleSequence;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.display.Glyph;
import stencil.display.LayerView;
import stencil.display.SchemaFieldDef;
import stencil.tuple.TupleSorter;
import stencil.tuple.prototype.TuplePrototype;


/**Persistent, immutable view of a collection of columns.
 * Will not reflect future updates.
 */
public class TableView implements LayerView<StoreTuple>, ColumnStore<StoreTuple> {
	private final Rectangle2D bounds;
	protected final String name;
	protected final TuplePrototype<SchemaFieldDef> schema;
	protected final Column[] columns;
	protected final Map<Object, Integer> index;
	protected final int stateID;

	protected final int idColumn;
	protected final int boundsColumn;
	protected final int visibleColumn;
	protected final int sortColumn;
	
	public TableView(String name, TuplePrototype<SchemaFieldDef> schema) {
    	this(name, schemaToColumns(schema), new HashMap(), schema, 0, new Rectangle2D.Double());
	}	
	
	public TableView(String name, Column[] columns, Map<Object, Integer> index, TuplePrototype<SchemaFieldDef>  schema, int stateID, Rectangle2D bounds) {
		this.name = name;
		this.columns = Arrays.copyOf(columns, columns.length);
		this.stateID= stateID;
		this.index = index;
		this.schema = schema;
		this.idColumn = schema.indexOf(IDENTIFIER_FIELD);
		this.visibleColumn = schema.indexOf(VISIBLE_FIELD);
		this.boundsColumn = schema.indexOf(BOUNDS_FIELD);
		this.sortColumn = schema.indexOf(RENDER_ORDER_FIELD);
		this.bounds = bounds;
	}

	@Override
	public Rectangle2D getBoundsReference() {return bounds;}

	@Override
	public Iterable<Integer> renderOrder() {
		if (sortColumn < 0 || columns[sortColumn].readOnly()) {
			if (index.size() < columns[idColumn].size()) {
				return index.values();
			} else {
				return new SimpleSequence(this.size());
			}
		} else {
			ArrayList<StoreTuple> tuples = new ArrayList(size());
			for (StoreTuple t: new TupleIterator(this, index.values())) {
				tuples.add(t);
			}
			Collections.sort(tuples, new TupleSorter(sortColumn));
			ArrayList<Integer> indices = new ArrayList(size());
			for (StoreTuple t:tuples) {indices.add(t.row());}
			return indices;
		}
	}
	
	public boolean contains(Comparable ID) {return index.containsKey(ID);}
	
	@Override
	public int size() {return index.size();}
	
	@Override
	public String getName() {return name;}
	
	@Override
	public TuplePrototype<SchemaFieldDef> schema() {return schema;}
	
	@Override
	public int stateID() {return stateID;}
	
	@Override
	public StoreTuple find(Comparable name) {
		Integer idx = index.get(name);
		if (idx == null) {return null;}
		return get(idx);}

	@Override
	public StoreTuple get(int i) {
		return new StoreTuple(columns, schema, i, idColumn, visibleColumn, boundsColumn);
	}
	
	@Override
	public Iterator iterator() {
		if (index.size() < columns[idColumn].size()) {
			return new TupleIterator(this, index.values());
		} else {
			return new TupleIterator(this);
		}
	}

	public Column[] columns() {return columns;}

	@Override
	public ColumnStore viewpoint() {return this;}
	
    protected static Column[] schemaToColumns(TuplePrototype<SchemaFieldDef> schema) {
    	Column[] columns = new Column[schema.size()];
    	for (int i=0; i< schema.size(); i++) {
	   		Class type = schema.get(i).type();
	   		boolean constant = schema.get(i).isConstant();
	   		Object defVal = schema.get(i).defaultValue();
	   		Column c;
	   		
	   		if (!constant) {c = ColumnUtils.getColumn(type, defVal);}
	   		else {c = ColumnUtils.getConstantColumn(type, defVal);}
	   		columns[i] = c;
	   	}    	
	   	return columns;
    }
    
	/**Calculate the bounds of all glyphs stored in this layer.**/
	protected Rectangle2D calcBounds() {
		Rectangle2D bounds = new Rectangle(0,0,-1,-1);
		if (index.size() >0) {
			for (StoreTuple g: new TupleIterator(this, true)) {
				Rectangle2D bound = g.getBoundsReference();
				if (bound != null) {ShapeUtils.add(bounds, g.getBoundsReference());}
			}
		}
		return bounds;
	}
	
	
	/**Subset the basis view so its index only "contains" the passed ids.**/
	public static final TableView mask(TableView basis, List<Comparable> ids) {
		Map<Object, Integer> index = new HashMap();
		for (Comparable id: ids) {
			Integer at = basis.index.get(id);
			index.put(id, at);
		}
		return new TableView(basis.name, basis.columns, index, basis.schema, basis.stateID, basis.bounds);
	}
	
	public Glyph nearest(Point2D p) {return Table.Util.nearest(p, this);}
}
