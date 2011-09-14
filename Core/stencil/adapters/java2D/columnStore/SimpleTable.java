package stencil.adapters.java2D.columnStore;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pcollections.Empty;
import org.pcollections.PMap;

import stencil.adapters.general.ShapeUtils;
import stencil.display.Glyph;
import stencil.display.IDException;
import stencil.display.SchemaFieldDef;
import stencil.interpreter.tree.Freezer;
import stencil.parser.ParserConstants;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.geometry.RectangleTuple;

/**Leaf nodes in a scene graph are always of the type simple table.**/
public final class SimpleTable implements Table {    
	private final Object tableLock = new Object();
	
	private TableView tenured;
	private PMap<Comparable, PrototypedTuple> update = Empty.map();
	private TableShare currentShare;
	
	private int stateID;
	
    /**
     * Create a new Table with a given number of rows, and the starting
     * capacity for a given number of columns.
     * @param nrows the starting number of table rows
     * @param ncols the starting capacity for columns 
     */
    public SimpleTable(String name, TuplePrototype<SchemaFieldDef> schema) {
    	tenured = new TableView(name, schema);
    }
    
    
    /**Creates a table share that can asynchronously calculate the update (including column merges).
     * 
     * A share is a view that can be edited and merged back in to the table.
     * Any early created view or share can still be read, but no merge-back is possible.
     */
    public TableShare changeGenerations() {
    	synchronized(tableLock) {currentShare = new TableShare(this, update);}
    	return currentShare;
    }
    
    /**Takes a table share and merges its updates with the current table.
     * This is mutative on the current table, but does not affect any TableViews.
     * Only consistent shares may be used.
     * 
     * @param col
     * @param update
     */
    public void merge(TableShare share) {
    	if (share.source() != this) {throw new TableMergeException(tenured.name, "Can only merge with shares made from the target table.");}
    	if (share != currentShare) {throw new TableMergeException(tenured.name, "Can only merge with most recent share.");}
    	share.dynamicComplete();	//Wait for dynamic bindings to complete before merging
    	
    	if (!share.unchanged()) {
    		TableView newTenured = share.viewpoint();

    		synchronized(tableLock) {
	    		stateID = Math.max(newTenured.stateID, stateID);
		    	for (Comparable id: share.updates.keySet()) {
		    		if (update.get(id).equals(share.updates.get(id))) {
		    			update = update.minus(id);
		    		}
		    	}
		    	
		    	tenured = newTenured;
    		}
    	}
    	currentShare = null;
    } 

    @Override
    public TableShare viewpoint() {
    	synchronized(tableLock) {
	    	if (currentShare == null) {changeGenerations();}
	    	return currentShare;
    	}
    }  

    
    
	@Override
	public void update(PrototypedTuple updates) {
		assert updates.prototype().indexOf(ParserConstants.IDENTIFIER_FIELD) ==0 : "Passed tuple without ID in position 0"; 
		
		Object idu = updates.get(0);	//Assumed to be the identifier (enforced in grammar)
		if (idu == null || !(idu instanceof Comparable)) {
			throw new IDException(idu, tenured.name);
		}
		Comparable id = (Comparable) idu;

		synchronized(tableLock){		//TODO: This synchronization is a bottleneck (sometimes 60% of runtime); can it be removed???
			if (update.containsKey(id)) {
				PrototypedTuple prior = update.get(id);				
				updates = Tuples.merge(prior, updates, Freezer.NO_UPDATE);
			} 
			update = update.plus(id, updates);
			stateID++;
		}
	}
		
	
	@Override
	public void remove(Comparable ID) {
		synchronized (tableLock) {
			update = update.plus(ID, DELETE);
			stateID++;
		}
	}	

	@Override
	public boolean contains(Comparable ID) {
		if (update.containsKey(ID)) {return update.get(ID) != DELETE;}
		return tenured.contains(ID);
	}

	@Override
	public Glyph find(Comparable ID) {
		if (update.containsKey(ID)) {
			PrototypedTuple values = update.get(ID);
			if (values == DELETE) {
				return null;
			}else {
				return ColumnStore.Util.fillAndGlyph(values, tenured.schema, tenured.idColumn, tenured.visibleColumn);
			}
		} else {
			return tenured.find(ID);
		}
	}
	
	@Override
	public TableView tenured() {return tenured;}
	
	@Override
	public String name() {return tenured.name;}

	@Override
	public TuplePrototype<SchemaFieldDef> prototype() {return tenured.schema;}

	@Override
	public int stateID() {return stateID;}

	@Override
	public void updatePrototype(TuplePrototype<SchemaFieldDef> schema) {
		tenured = new TableView(tenured.name, schema);
	}
	
	@Override
	public int size() {return tenured.size() + update.size();}


	@Override
	public Rectangle2D getBoundsReference() {return tenured.getBoundsReference();}
	public RectangleTuple bounds() {
		return new RectangleTuple(tenured.getBoundsReference().getBounds2D());
	}
	
	public Glyph nearest(double x, double y) {
		Glyph nearest=null;
		double distance = Double.POSITIVE_INFINITY;
		Point2D p = new Point2D.Double(x,y);
		
		for (Glyph g: tenured) {
			Rectangle2D b = g.getBoundsReference();
			double dist = ShapeUtils.distance(p, b);
			if (dist < distance) {nearest = g; distance=dist;}
		}
		
		return nearest;
	}
} // end of class Table
