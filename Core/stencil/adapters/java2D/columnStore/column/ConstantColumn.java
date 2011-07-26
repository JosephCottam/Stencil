package stencil.adapters.java2D.columnStore.column;

/**
 * Column implementation holding a single, constant value for all rows.
 * 
 * This is not an immutable column!   
 * You may use replaceAll to generate a new column with possibly distinct values for every row.
 * This is an optimization so columns with just repeats of the same value are compact. 
 */
public class ConstantColumn<T> extends AbstractColumn<T> {
    /**
     * Create a new ConstantColumn.
     * @param type the data type of this column
     * @param defaultValue the default value used for all rows
     */
    public ConstantColumn(Class<T> type, T defaultValue) {
        super(type, defaultValue);
    }
    
    @Override
    public boolean readOnly() {return true;}
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#getRowCount()
     */
    public int size() {throw new UnsupportedOperationException();}


    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#get(int)
     */
    public T get(int row) {return super.m_defaultValue;}

	@Override
	public Column update(Object[] values, int[] targets, int extend) {throw new UnsupportedOperationException("Cannot extend constant column");}

	@Override
	public Column replaceAll(Object values) {
		return ColumnUtils.getColumn(this.m_columnType, this.m_defaultValue).replaceAll(values);
	}
} // end of class Constant Column
