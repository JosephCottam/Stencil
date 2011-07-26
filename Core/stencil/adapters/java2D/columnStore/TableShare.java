package stencil.adapters.java2D.columnStore;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.display.DynamicBindSource;
import stencil.display.LayerView;
import stencil.display.SchemaFieldDef;
import stencil.interpreter.tree.Freezer;
import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;

/**A share is a view of a table that can have modifications made to it.
 * If a share has been modified and locked AND the tenured generation of its source
 * table has not changed, then it may be merged back into the source table.
 * 
 * This merge-back restriction implies that data additions desired in the merge must be made before the store is created.
 *
 *
 */
public class TableShare implements ColumnStore<StoreTuple>, DynamicBindSource<StoreTuple>, LayerView<StoreTuple> {
	protected final Table source;
	protected final Column[] columns;
	protected final Map<Comparable, PrototypedTuple> updates;
	protected final int creationID;
	protected Map<Object, Integer> index;

	protected boolean locked = false;
	protected int stateID =0;
	protected final int idColumn;
	protected Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);
	
	private TableView viewpoint; 	//After all updates are done, store the values here.
	
	public TableShare(final Table source, Map<Comparable, PrototypedTuple> update) {
		this.source = source;
		this.index = source.tenured().index;
		this.updates = update;
		this.idColumn = source.tenured().idColumn;
		creationID = source.tenured().stateID;		
		columns = Arrays.copyOf(source.tenured().columns, source.tenured().columns.length);
	}
	
	/**Modify the column store based on the results of one dynamic binding.
	 * 
	 * IMPORTANT: This assumes that the new column is of a compatible type with the reads from the column.
	 *   Due lack of use context, boxing and complex type relations this is hard to check at runtime.
	 * 
	 * */
	public synchronized void setColumn(int col, Column newColumn) {
		if (locked) {throw new UnsupportedOperationException("Locked.  Updates no longer permitted.");}
		
		columns[col] = newColumn;
		stateID++;
	}	
	
	/**Merge updates from the key/value update.
	 * This must be run before dynamic bindings are executed.
	 */
	public synchronized void simpleUpdate() {
		if (updates.size() ==0) {simpleComplete(); return;}	//No updates implies no work in the simple update (dynamic bindings may still need to run)
		UpdateSet updateSet = calcUpdateSet();
		
		
		for (int i=0; i< columns.length; i++) {
			if (!columns[i].readOnly() && updateSet.values[i] != null) {
				columns[i] = columns[i].update(updateSet.values[i], updateSet.targets, updateSet.extend);
			}
		}
		
		final Column ids = columns[idColumn];
		index = new TreeMap();
		for (int i=0; i<ids.size(); i++) {
			index.put(ids.get(i), i);
		}
		
		stateID++;
		simpleComplete();
	}
	
	/**Determine the row/column updates for the passed values.**/
	private UpdateSet calcUpdateSet() {
		final List<Integer> deletes = new ArrayList();
		int extend=0;
		
		//Create list of delete locations from the tenured store
		for (Map.Entry<Comparable, PrototypedTuple> update: updates.entrySet()) {
			if (update.getValue() == Table.DELETE) {
				Integer target = source.tenured().index.get(update.getKey());
				if (target != null) {
					extend--;
					deletes.add(target);
				}
			}
		}
				
		//Determine updates at specific locations
		final int[] targets = new int[updates.size()];
		final Object[][] values = new Object[source.tenured().columns.length][];
		
		int i=0;
		for (Map.Entry<Comparable, PrototypedTuple> entry: updates.entrySet()) {
			final PrototypedTuple singleUpdate = entry.getValue();
			final Comparable id = entry.getKey();
			
			if (singleUpdate != Table.DELETE) {
				//Determine where to put the values 
				Integer target = source.tenured().index.get(id);
				if (target == null) {
					if (extend < 0) {target = deletes.remove(0);}
					else {target = source.tenured().size() + extend;}
					extend++;
				} 
				targets[i] = target;
				
				//Sift the update fields into column-related arrays				
				for (int field=0; field<singleUpdate.size(); field++) {
					String fieldName = singleUpdate.prototype().get(field).name();
					int targetColIdx = source.tenured().schema.indexOf(fieldName);
					
					Column targetCol;
					try {targetCol = columns[targetColIdx];}
					catch (ArrayIndexOutOfBoundsException ex) {throw new InvalidNameException(source.name(), fieldName, source.tenured().schema);}

					Object rawValue = singleUpdate.get(field);
					Object updateValue = 
							rawValue == Freezer.NO_UPDATE 
								? Freezer.NO_UPDATE 
								: Converter.convert(rawValue, targetCol.type(), targetCol.getDefaultValue());

					if (updateValue == Freezer.NO_UPDATE && target > source.tenured().size()) {updateValue = Freezer.VALUE_DEFAULT;}
					
					if (values[targetColIdx] == null) {
						values[targetColIdx] = new Object[updates.size()];  
						Arrays.fill(values[targetColIdx], Freezer.VALUE_DEFAULT);	//TODO: Can this fill be skipped after FillCoColumns has been added?
					}
					
					values[targetColIdx][i] = updateValue;
				}
			}
			
			i++;
		}
		
		return new UpdateSet(values, targets, extend);
	}
	
	public Table source() {return source;}

	public Column[] columns() {return columns;}
		
	public int creationID() {return creationID;}

	@Override
	public StoreTuple find(Comparable name) {
		waitForSimple();
		return new StoreTuple(columns, source.tenured().schema, index.get(name));
	}

	@Override
	public StoreTuple get(int i) {return new StoreTuple(columns, source.tenured().schema, i);}
	
	@Override
	public TuplePrototype<SchemaFieldDef> schema() {return source.tenured().schema;}

	/**Counter of the number of updates that have been made to the column store.
	 * Other internal state changes are not tracked.
	 */
	@Override
	public int stateID() {return stateID;}
	
	/**Indicate that some state-changing operation occurred.
	 * This method is to support entities not tracking StateID themselves.
	 * It will return true exactly when the stateID has moved off of its original value.*/
	public boolean unchanged() {return stateID == 0;}

	@Override
	public int size() {
		int more =0;
		if (!simpleUpdateComplete.get()) {
			for (Object val: updates.values()) {
				if (val == Table.DELETE) {more--;}
				else {more++;}
			}
		}
		return index.size()+more;
	}

	@Override
	public TableView viewpoint() {
		waitForDynamic();
		if (viewpoint == null) {viewpoint = new TableView(source.name(), columns, Collections.unmodifiableMap(index), source.tenured().schema, creationID+1, fullBounds);}
		return viewpoint;
	}
	
	public Rectangle2D getBoundsReference() {return source.tenured().getBoundsReference();}

	
	/**Not reliable until after the simple updates are complete...**/
	@Override
	public Iterator<StoreTuple> iterator() {
		return new TupleIterator(this);
	}
	
	
	/**Representation of a bulk-update of rows.**/
	private final class UpdateSet {
		final Object[][] values;	//Set of new column values
		final int[] targets;	//What row will an update go into
		final int extend;		//How many new row indices are being introduced (may be negative if there is a net loss)
		
		public UpdateSet(Object[][] values, int[] targets, int extend) {
			this.values = values;
			this.targets = targets;
			this.extend = extend;
		}
	}

	@Override
	public Map<String, Tuple> getSourceData() {return new DynamicValuesView(this);}

	//Coordinating access to phases ----------------------------------------------------------
	private final AtomicBoolean simpleUpdateComplete = new AtomicBoolean(false);
	public void waitForSimple() {waitUntil(simpleUpdateComplete);}
	private void simpleComplete() {complete(simpleUpdateComplete);}
	
	private final AtomicBoolean dynamicUpdateComplete = new AtomicBoolean(false);
	public void waitForDynamic() {waitUntil(dynamicUpdateComplete);}
	public void dynamicComplete() {complete(dynamicUpdateComplete);}
	
	private final void waitUntil(AtomicBoolean lock) {
		if (lock.get()) {return;}
		synchronized (lock) {
			if (lock.get()) {return;}
			try {lock.wait();} 
			catch (Exception e) {throw new Error("Error coordinating share.",e);}
		}		
	}
	private final void complete(AtomicBoolean lock) {
		if (lock.get()) {return;}
		lock.set(true);
		synchronized (lock) {lock.notifyAll();}		
	}

	@Override
	public String getName() {return source.tenured().name;}

	@Override
	public Collection<Integer> renderOrder() {throw new UnsupportedOperationException();}
	
	public boolean contains(Comparable ID) {
		if (updates.containsKey(ID)) {return updates.get(ID) != Table.DELETE;}
		return source.tenured().contains(ID);
	}
	
    /**Change the screen bounds of the table.**/
    public void setBounds(Rectangle2D fullBounds) {this.fullBounds = fullBounds;}
}
