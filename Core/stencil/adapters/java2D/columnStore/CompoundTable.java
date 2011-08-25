package stencil.adapters.java2D.columnStore;


import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.adapters.java2D.columnStore.column.ReferenceColumn;
import stencil.adapters.java2D.render.CompoundRenderer;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.Glyph;
import stencil.display.IDException;
import stencil.display.SchemaFieldDef;
import stencil.parser.ParserConstants;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;
import stencil.util.collections.ListSet;
import stencil.util.collections.SingletonList;

/**Collection of tables for a scene graph; internal nodes are always compound tables.
 * 
 * Basic structure is a root table: ID,X,Y, VISIBLE, <reference lists>+
 * 
 *  The reference lists contain IDs for entities in the child tables
 * */ 
public class CompoundTable implements Table {    
	protected final List<Table> children ;
	protected final List<Table> components;
	protected SimpleTable root;

	
    public CompoundTable(String name, Table... children) {this(name, Arrays.asList(children));}

    /**
     * Create a new Table with a given number of rows, and the starting capacity for a given number of columns.
     * @param nrows the starting number of table rows
     * @param ncols the starting capacity for columns 
     */
    public CompoundTable(String name, List<Table> children) {
    	ArrayList childs = new ArrayList();
    	childs.addAll(children);
    	this.children = Collections.unmodifiableList(childs); 
    	
    	List<SchemaFieldDef> refs = new ArrayList();
    	for (Table child: children) {refs.add(Renderer.CHILDREN.modify(child.name(), child.prototype()));}
    	
    	//Construct root table, including setting the targets on the reference columns
    	root = new SimpleTable(name, TuplePrototypes.extend(CompoundRenderer.SCHEMA_BASIS, refs));
    	for (Table child: children) {
    		int idx = root.prototype().indexOf(child.name());
    		root.tenured().columns[idx] = new ReferenceColumn(child);
    	}
    	
    	ArrayList comps = new ArrayList();
    	
    	comps.add(root);
    	comps.addAll(children);
    	components = Collections.unmodifiableList(comps);
    }
    
    /**No effect on compound table; all updates are handled in the component tables.*/
    public TableShare changeGenerations() {throw new UnsupportedOperationException();}
    
    /**No effect on compound table; all updates are handled in the component tables.*/
    public void merge(TableShare share) {return;}
    	        
    /**What is ready to render, right now?**/
    public TableView tenured() {return root.tenured();}  

    @Override
    public TableShare viewpoint() {return root.viewpoint();}
        
	@Override
	public void update(PrototypedTuple update) {		
		//sift out updates
		Map<Table, List<PrototypedTuple>> childUpdates = new HashMap();
		List<Integer> childUpdateIdxs = new ListSet();
		
		for (Table child: children) {
			int idx = update.prototype().indexOf(child.name());
			if (idx <0) {continue;}

			List<PrototypedTuple> updates;
			Object value = update.get(idx);
			if (value instanceof List) {updates = (List<PrototypedTuple>) value;}
			else if (value instanceof Tuple) {updates = new SingletonList(value);}
			else {updates = new SingletonList(Converter.toTuple(value));}
			
			childUpdates.put(child, updates);
			childUpdateIdxs.add(idx);
		}

		//Modify the root update to have references to child update IDs
		PrototypedTuple rootUpdate = update;
		rootUpdate = (PrototypedTuple) Tuples.delete(update, ArrayUtil.intArray(childUpdateIdxs));		//Delete references to children
		Map<String, List<Comparable>> changedChildren = changedChildren(childUpdates); 	//Make a list of child Ids
		rootUpdate = Tuples.merge(rootUpdate, new PrototypedArrayTuple(changedChildren.keySet(), changedChildren.values()));	//Place the child ids in to the tuple
		
		//Update each component table
		root.update(rootUpdate);
		for (Map.Entry<Table, List<PrototypedTuple>> childUpdate: childUpdates.entrySet()) {
			Table child = childUpdate.getKey();
			for (PrototypedTuple t: childUpdate.getValue()) {child.update(t);}
		}
	}
	
	/**Get all of the ID fields for each child in updates.  */
	private Map<String, List<Comparable>> changedChildren(Map<Table, List<PrototypedTuple>> childUpdates) {
		Map<String, List<Comparable>> ids = new HashMap();
		for (Map.Entry<Table, List<PrototypedTuple>> update: childUpdates.entrySet()) {
			String tableName = update.getKey().name();
			ids.put(tableName , ids(tableName , update.getValue()));
		}
		return ids;
	}
	
	//Collect the ids out of the updates for a single child
	private List<Comparable> ids(String tableName, List<PrototypedTuple> childUpdates) {
		List<Comparable> ids = new ListSet();
		for (PrototypedTuple update:childUpdates){
			Object idu = update.get(ParserConstants.IDENTIFIER_FIELD);
			if (idu == null) {throw new IDException(root.name());}		
			if (!(idu instanceof Comparable)) {throw new IDException(idu, root.name());}
			Comparable id = (Comparable) idu;
			ids.add(id);
		}		
		return ids;

	}
	
	@Override
	public void remove(Comparable ID) {root.remove(ID);}

	@Override
	public boolean contains(Comparable ID) {return root.contains(ID);}

	@Override
	public Glyph find(Comparable ID) { 
		//TODO: Wrap in multi-part tuple before return so the whole tree comes back; don't forget to handle deletes in components...ARG!!!
		Glyph base = root.find(ID);
		return base;
	}
	
	
	@Override
	public String name() {return root.name();}

	@Override
	public TuplePrototype<SchemaFieldDef> prototype() {return root.prototype();}

	@Override
	public int stateID() {return root.stateID();}

	@Override
	public void updatePrototype(TuplePrototype<SchemaFieldDef> schema) {
		throw new UnsupportedOperationException();
	}
	
	public int size() {return root.size();}

	/**What are the component tables of this compound?
	 * This is children and root.  The root is at position zero in the list.
	 */
	public List<Table> components() {return components;}

	public Rectangle2D getBoundsReference() {return root.getBoundsReference();}
	
	/**Create a tuple that can be the first argument to a merge, after which all non-constant fields will be properly set.**/ 
	public PrototypedTuple updateMaskTuple() {
		PrototypedTuple mask = SchemaFieldDef.asTuple(root.prototype());
		for(Table child: children) {
			PrototypedTuple t;
			if (child instanceof CompoundTable) {t = ((CompoundTable) child).updateMaskTuple();}
			else {t = SchemaFieldDef.asTuple(child.prototype());}
			t = Tuples.prefix(t, child.name());
			mask = Tuples.merge(mask, t);
		}
		return mask;
	}
	
	/**Perform the full generation change and merge loop for a compound table and all its children.**/
	static void fullGenChange(CompoundTable table, CompoundRenderer renderer, AffineTransform viewTransform) {
		for (Table t: table.children) {
			if (t instanceof CompoundTable) {
				fullGenChange((CompoundTable) t, (CompoundRenderer) renderer.rendererFor(t.name()), viewTransform);
			} else {
				TableShare share = t.changeGenerations();
				share.simpleUpdate();		//TODO: Dynamic bindings ever in the axis?  Probably yes...but badness right now
				renderer.rendererFor(t.name()).calcFields(share, viewTransform);
				t.merge(share);
			}
		}

		TableShare share = table.root.viewpoint();
		share.simpleUpdate();
		renderer.calcFields(share, viewTransform);
		table.root.merge(share);
	}
} 
