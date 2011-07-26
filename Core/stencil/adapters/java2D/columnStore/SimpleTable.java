package stencil.adapters.java2D.columnStore;


import java.awt.geom.Rectangle2D;

import org.pcollections.Empty;
import org.pcollections.PMap;

import stencil.display.Glyph;
import stencil.display.IDException;
import stencil.display.SchemaFieldDef;
import stencil.parser.ParserConstants;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;

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
    	if (share.source() != this) {throw new IllegalArgumentException("Can only merge with shares made from the target table.");}
    	if (share.creationID() != tenured.stateID) {throw new IllegalArgumentException("Can only merge with most recent share.");}
    	share.dynamicComplete();	//Wait for dynamic bindings to complete before merging
    	
    	if (!share.unchanged()) {
    		TableView newTenured = share.viewpoint();

    		synchronized(tableLock) {
	    		stateID++;
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
        
    /**What is ready to render, right now?**/
    public TableView renderSet() {return tenured;}  

    @Override
    public TableShare viewpoint() {
    	synchronized(tableLock) {
	    	if (currentShare == null) {changeGenerations();}
	    	return currentShare;
    	}
    }  

    
    
	@Override
	public void update(PrototypedTuple updates) {
		Object idu = updates.get(ParserConstants.IDENTIFIER_FIELD);
		if (idu == null || !(idu instanceof Comparable)) {
			throw new IDException(idu, tenured.name);
		}
		Comparable id = (Comparable) idu;
		
		synchronized(tableLock){
			if (update.containsKey(id)) {
				PrototypedTuple prior = update.get(id);
				updates = Tuples.merge(prior, updates);
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
} // end of class Table
