package stencil.adapters.java2D.columnStore.column;

/**
 * Column implementation for storing int values.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class IntColumn extends AbstractColumn<Integer> {

    private final int[] m_values;
    
    public IntColumn(Integer defaultValue) {
        super(int.class, defaultValue);
        m_values = new int[0];
    }
    
    private IntColumn(int[] values, int defaultValue) {
    	super(int.class, defaultValue);
    	this.m_values = values;
    }
    
    // ------------------------------------------------------------------------
    // Column Metadata
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#getRowCount()
     */
    public int size() {return m_values.length;}
    
    // ------------------------------------------------------------------------
    // Data Access Methods    
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#get(int)
     */
    public Integer get(int row) {return new Integer(getInt(row));}

    // ------------------------------------------------------------------------
    // Data Type Convenience Methods
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.AbstractColumn#getInt(int)
     */
    private int getInt(int row) {
        if ( row < 0 || row >= m_values.length) {throw new IllegalArgumentException("Row index out of bounds: "+row);}
        return m_values[row];
    }
    
	@Override
	public IntColumn update(Object[] values, int[] targets, int extend) {
		return new IntColumn((int[]) ColumnUtils.extend(m_defaultValue, m_values, values, targets, extend), super.m_defaultValue);
	}
	
	@Override
	public Column replaceAll(Object values) {
		assert values.getClass().isArray();
		assert int.class.isAssignableFrom(values.getClass().getComponentType());
		
		return new IntColumn((int[]) values, m_defaultValue);
	}

    
} // end of class IntColumn
