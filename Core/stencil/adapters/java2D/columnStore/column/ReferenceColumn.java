package stencil.adapters.java2D.columnStore.column;

import java.util.List;

import stencil.adapters.java2D.columnStore.Table;
import stencil.util.collections.ListSet;

/**
 * Column to store references to entities in another table.
 */
public class ReferenceColumn extends AbstractColumn<ListSet> {
    private final ListSet<Comparable>[] values;
    private final Table target;
    
    /**
     * Create a new ObjectColumn.
     * @param type the data type of Objects in this column 
     */
    public ReferenceColumn(Table target) {
    	super(ListSet.class, new ListSet());
    	this.target = target;
    	values = new ListSet[0];
    }
    
    private ReferenceColumn(Table target, ListSet[] values) {
    	super(ListSet.class, new ListSet());
    	this.target = target;
    	this.values = values;
    }

    @Override
    public int size() {return values.length;}
    
    public Table target() {return target;}
    
    /**
     * Get the data value at the specified row
     * @param row the row from which to retrieve the value
     * @return the data value
     */
    public ListSet get(int row) {
        if ( row < 0 || row >= values.length ) {
            throw new IllegalArgumentException(
                "Row index out of bounds: "+row);
        }
        return values[row];
    }    
    
    
    /**Update the reference column.  This involves MERGING values, not over-writing them as occurs in other columns.*/
	@Override
	public ReferenceColumn update(Object[] update, int[] targets, int extend) {
		final ListSet<Comparable>[] newValues = new ListSet[this.values.length + extend];
		System.arraycopy(values, 0, newValues, 0, Math.min(values.length, newValues.length));

		
		//Reference update semantics dictate a merge of ID lists, not an overwrite.
		//That merge is handled in this try/catch block
		try {
			//TODO: handle deletes by passing something of a different type in update and deleting ids from the target table
			if (extend <0) {throw new Error("Cant do big deletes yet.");}
			else {
				for (int i=0; i< update.length; i++) {
					ListSet union;
					if (targets[i] >= values.length) {union = new ListSet();}
					else {union = new ListSet(values[targets[i]]);}
					List more = (List) update[i];

					union.addUnique(more);
					newValues[targets[i]] = union;
				}
			}
		} catch (Exception e) {throw new Error("Error extending column", e);}
		
		
		//Remove references to deleted
		//TODO: Only do this if the target table has changed since it was last done (even better, if it was changed with deletes)
		for (ListSet references: newValues) {
			for (Object id: references) {
				if (!target.viewpoint().contains((Comparable) id)) {references.remove(id);}
			}
		}
		
		return new ReferenceColumn(target, newValues);
	}

	@Override
	public ReferenceColumn replaceAll(Object values) {
		assert values.getClass().isArray();
		assert super.m_columnType.isAssignableFrom(values.getClass().getComponentType());
		
		return new ReferenceColumn(target, (ListSet[]) values);
	}


} // end of class ObjectColumn
