package stencil.adapters.java2D.columnStore;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.List;
import java.util.ArrayList;

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
	private List<PrototypedTuple> updates = new ArrayList();
	private List<PrototypedTuple> transfer = new ArrayList();
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
    	synchronized(tableLock) {
    		transfer = updates;
    		updates = new ArrayList();
    		currentShare = new TableShare(this, transfer);
    	}
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
		    	tenured = newTenured;
		    	transfer= null;
    		}
    	}
    	currentShare = null;
    } 

    @Override
    public TableShare viewpoint() {
    	if (currentShare == null) {changeGenerations();}
    	return currentShare;
    }  

    
    
	@Override
	public void update(PrototypedTuple update) {
		assert update.prototype().indexOf(ParserConstants.IDENTIFIER_FIELD) ==0 : "Passed tuple without ID in position 0"; 
		
		Object idu = update.get(0);	//Assumed to be the identifier (enforced in grammar)
		if (idu == null || !(idu instanceof Comparable)) {
			throw new IDException(idu, tenured.name);
		}

		updates.add(update);
		stateID++;
	}
		
	
	@Override
	public void remove(Comparable ID) {
		updates.add(DeleteTuple.with(ID));
		stateID++;
	}	

	@Override
	public boolean contains(Comparable ID) {
		for (int i=updates.size(); i>=0;i--) {		//Work backwards in case a new add came in after a delete.
			PrototypedTuple t = updates.get(i);
			if (t.get(0).equals(ID)) {return t instanceof DeleteTuple;}
		}
		return tenured.contains(ID);
	}

	@Override
	public Glyph find(Comparable ID) {
		//Gather update parts (stop if any are delete)
		boolean deleted = false;
		List<PrototypedTuple> components = new ArrayList();
		for (PrototypedTuple t: updates) {
			if (t.get(0).equals(ID)) {
				if (t instanceof DeleteTuple) {components.clear(); deleted= true; continue;}	//Nothing before the delete matters
				deleted = false;
				components.add(t);
			}
		}

		if (deleted) {return null;} //return nothing if the last time the ID was seen, it was a delete

		//Return the composite of any found values
		if (components.size() > 0) {
			PrototypedTuple pt = components.get(0); 
			for (int i=1; i< components.size(); i++) {
				Tuples.merge(pt, components.get(i), Freezer.NO_UPDATE);
			}
			return ColumnStore.Util.fillAndGlyph(pt, tenured.schema, tenured.idColumn, tenured.visibleColumn);
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
	public int size() {
		int ts = transfer == null ? 0 : transfer.size();
		int us = updates == null  ? 0 : updates.size();
		
		return tenured.size() + ts + us;}	//HACK: This is an upper-bound, not necessarily the actual size;  TODO: Remove from all but tenured...


	@Override
	public Rectangle2D getBoundsReference() {return tenured.getBoundsReference();}
	public RectangleTuple bounds() {return new RectangleTuple(tenured.getBoundsReference().getBounds2D());}
	
	@Override
	public Glyph nearest(Point2D p) {return Table.Util.nearest(p, tenured);}
} // end of class Table
