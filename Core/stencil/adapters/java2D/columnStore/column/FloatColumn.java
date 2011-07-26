package stencil.adapters.java2D.columnStore.column;

/**
 * Column instance for sotring flaot values.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class FloatColumn extends AbstractColumn<Float> {
    private final float[] m_values;
        
    /**
     * Create a new FloatColumn. 
     * @param nrows the initial size of the column
     * @param capacity the initial capacity of the column
     * @param defaultValue the default value for the column
     */
    public FloatColumn(float defaultValue) {
        super(float.class, defaultValue);
        m_values = new float[0];
    }
    
    private FloatColumn(float[] values, float defaultValue) {
    	super(float.class, defaultValue);
    	this.m_values = values;
    }

    // ------------------------------------------------------------------------
    // Column Metadata
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#getRowCount()
     */
    public int size() {return m_values.length;}
    

    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#get(int)
     */
    public Float get(int row) {
        return new Float(getFloat(row));
    }

    // ------------------------------------------------------------------------
    // Data Type Convenience Methods
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.AbstractColumn#getFloat(int)
     */
    private float getFloat(int row) {
        if ( row < 0 || row >= m_values.length) {
            throw new IllegalArgumentException("Row index out of bounds: "+row);
        }
        return m_values[row];
    }
    
	@Override
	public FloatColumn update(Object[] values, int[] targets, int extend) {
		return new FloatColumn((float[]) ColumnUtils.extend(m_defaultValue, m_values, values, targets, extend), super.m_defaultValue);
	}
	
	@Override
	public FloatColumn replaceAll(Object values) {
		assert values.getClass().isArray();
		assert float.class.isAssignableFrom(values.getClass().getComponentType());
		
		return new FloatColumn((float[]) values, m_defaultValue);
	}

} // end of class FloatColumn
