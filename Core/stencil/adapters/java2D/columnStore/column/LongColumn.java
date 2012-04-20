package stencil.adapters.java2D.columnStore.column;

/**
 * Column implementation for storing long values.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class LongColumn extends AbstractColumn<Long> {
    private final long[] m_values;
    
    /**
     * Create a new LongColumn. 
     * @param nrows the initial size of the column
     * @param capacity the initial capacity of the column
     * @param defaultValue the default value for the column
     */
    public LongColumn(long defaultValue) {
        super(long.class, defaultValue);
        m_values = new long[0];
    }
    
    private LongColumn(long[] values, long defaultValue) {
    	super(long.class, defaultValue);
    	this.m_values = values;
    }

    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#getRowCount()
     */
    @Override
	public int size() {return m_values.length;}
    
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#get(int)
     */
    @Override
	public Long get(int row) {return new Long(getLong(row));}


    /**
     * @see stencil.adapters.java2D.columnStore.column.AbstractColumn#getLong(int)
     */
    private long getLong(int row) {
        if ( row < 0 || row >= m_values.length) {throw new IllegalArgumentException("Row index out of bounds: "+row);}
        return m_values[row];
    }
    
	@Override
	public LongColumn update(Object[] values, int[] targets, int extend) {
		return new LongColumn((long[]) ColumnUtils.extend(m_defaultValue, m_values, values, targets, extend), super.m_defaultValue);
	}
	
	@Override
	public LongColumn replaceAll(Object values) {
		assert values.getClass().isArray();
		assert long.class.isAssignableFrom(values.getClass().getComponentType());
		
		return new LongColumn((long[]) values, m_defaultValue);
	}


} // end of class LongColumn
