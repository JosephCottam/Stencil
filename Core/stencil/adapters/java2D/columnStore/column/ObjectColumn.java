package stencil.adapters.java2D.columnStore.column;

import java.lang.reflect.Array;

/**
 * Column implementation for storing arbitrary Object values.
 * Based on a prefuse implementation.
 */
public class ObjectColumn<T> extends AbstractColumn<T> {

    private final T[] values;				
    
    /**
     * Create a new ObjectColumn.
     * @param type the data type of Objects in this column 
     */
    public ObjectColumn(Class<T> type) {
        this(type, null);
    }
        
    /**
     * Create a new ObjectColumn.
     * @param type the data type of Objects in this column 
     * @param nrows the initial size of the column
     * @param capacity the initial capacity of the column
     * @param defaultValue the default value for the column. If this value
     * is cloneable, it will be cloned when assigned as defaultValue, otherwise
     * the input reference will be used for every default value.
     */
    public ObjectColumn(Class<T> type,T defaultValue) {
        super(type, defaultValue);
        this.values = (T[]) Array.newInstance(type, 0);
    }
    
    private ObjectColumn(T[] values, Class<T> type, T defaultValue) {
    	super(type, defaultValue);
    	this.values = values;
    }
    
    // ------------------------------------------------------------------------
    // Column Metadata
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#getRowCount()
     */
    @Override
	public int size() {return values.length;}
        // ------------------------------------------------------------------------
    // Data Access Methods
    
    /**
     * Get the data value at the specified row
     * @param row the row from which to retrieve the value
     * @return the data value
     */
    @Override
	public T get(int row) {
        if ( row < 0 || row >= values.length ) {
            throw new IllegalArgumentException(
                "Row index out of bounds: "+row);
        }
        return values[row];
    }    
    
	@Override
	public ObjectColumn update(Object[] values, int[] targets, int extend) {
		return new ObjectColumn((T[]) ColumnUtils.extend(m_defaultValue, this.values, values, targets, extend), super.m_columnType, super.m_defaultValue);
	}

	@Override
	public ObjectColumn replaceAll(Object values) {
		assert values.getClass().isArray();
		assert super.m_columnType.isAssignableFrom(values.getClass().getComponentType()) :"Attempt to assign to column of type " + m_columnType.getSimpleName() + " with values of type " + values.getClass().getComponentType().getSimpleName();
		
		return new ObjectColumn((T[]) values, m_columnType, m_defaultValue);
	}


} // end of class ObjectColumn
