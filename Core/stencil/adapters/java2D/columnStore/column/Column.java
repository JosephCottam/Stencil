package stencil.adapters.java2D.columnStore.column;

/**
 * Interface for a data column in a table.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public interface Column<T> {
    
    // ------------------------------------------------------------------------
    // Column Metadata
    
    /**
     * Returns the number of rows in this data column.
     * Returns -1 if the value is a constant.
     * @return the number of rows
     */
    public int size();

    /**Is this a read-only column?*/
    public boolean readOnly();

    // ------------------------------------------------------------------------
    // Data Access Methods
    
    /**
     * Returns the default value for rows that have not been set explicitly. 
     */
    public Object getDefaultValue();
        
    /**
     * Get the data value at the specified row
     * @param row the row from which to retrieve the value
     * @return the data value
     */
    public T get(int row);
    
    /**What is the type of the values stored in this column?*/
    public Class type();
    
    /**Update this column by placing the given values at the specified targets.
     * Extend the range of indices by the specified amount (may be negative, if deletes were incurred).
     * 
     * @param values Pointer to the array the contains the updated values
     * @param targets
     * @param extend
     * @return
     */
    public Column update(Object[] values, int[] targets, int extend);
    
    
    /**Return a new column with the same meta-data characteristics, but the
     * passed values instead of the current values.
     */
    public Column replaceAll(Object values);
} // end of interface Column
