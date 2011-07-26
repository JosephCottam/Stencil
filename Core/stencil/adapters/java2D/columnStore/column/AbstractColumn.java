package stencil.adapters.java2D.columnStore.column;

/**
 * Abstract base class for Column implementations. 
 * Provides default implementations of column methods.
 * Based on Prefuse's AbstractColumn.
 */
public abstract class AbstractColumn<T> implements Column<T> {

    protected final Class<T> m_columnType;
    protected T m_defaultValue;
    
    /**
     * Create a new AbstractColumn of type Object.
     */
    public AbstractColumn() {
    	this((Class<T>) Object.class, null);
    }

    /**
     * Create a new AbstractColumn of a given type.
     * @param columnType the data type stored by this column
     */
    public AbstractColumn(Class<T> columnType) {
        this(columnType, null);
    }

    /**
     * Create a new AbstractColumn of a given type.
     * @param columnType the data type stored by this column
     * @param defaultValue the default data value to use
     */
    public AbstractColumn(Class<T> columnType, T defaultValue) {
        m_columnType = columnType;
        m_defaultValue = defaultValue;
    }
    
    // ------------------------------------------------------------------------
    // Column Metadata
    /**
     * Returns the most specific superclass for the values in the column
     * @return the Class of the column's data values
     */
    public Class type() {return m_columnType;}

    public boolean readOnly() {return false;}
    
    // ------------------------------------------------------------------------
    // Data Access Methods
    
    /**
     * Returns the default value for rows that have not been set explicitly. 
     */
    public Object getDefaultValue() {return m_defaultValue;}
    
} // end of abstract class AbstractColumn
