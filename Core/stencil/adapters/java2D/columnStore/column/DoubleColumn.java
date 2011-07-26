package stencil.adapters.java2D.columnStore.column;

/**
 * Column implementation for storing double values.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DoubleColumn extends AbstractColumn<Double> {

    private final double[] m_values;
        
    /**
     * Create a new DoubleColumn. 
     * @param nrows the initial size of the column
     * @param capacity the initial capacity of the column
     * @param defaultValue the default value for the column
     */
    public DoubleColumn(double defaultValue) {
        super(double.class, defaultValue);
        m_values = new double[0];
    }
    
    private DoubleColumn(double[] values, double defaultValue) {
    	super(double.class, defaultValue);
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
    public Double get(int row) {return getDouble(row);}

    /**
     * @see stencil.adapters.java2D.columnStore.column.AbstractColumn#getDouble(int)
     */
    public double getDouble(int row) {
        if ( row < 0 || row >= m_values.length) {throw new IllegalArgumentException("Row index out of bounds: "+row);}
        return m_values[row];
    }    
    
	@Override
	public DoubleColumn update(Object[] values, int[] targets, int extend) {
		return new DoubleColumn((double[]) ColumnUtils.extend(m_defaultValue, m_values, values, targets, extend), super.m_defaultValue);
	}

	@Override
	public DoubleColumn replaceAll(Object values) {
		assert values.getClass().isArray();
		assert double.class.isAssignableFrom(values.getClass().getComponentType());
		
		return new DoubleColumn((double[]) values, m_defaultValue);
	}
} // end of class DoubleColumn
