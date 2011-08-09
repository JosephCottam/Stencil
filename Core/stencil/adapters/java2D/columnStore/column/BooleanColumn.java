package stencil.adapters.java2D.columnStore.column;

import java.lang.reflect.Array;
import java.util.BitSet;

/**
 * Column implementation storing boolean values. Uses a BitSet representation
 * for space efficient storage.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class BooleanColumn extends AbstractColumn<Boolean> {

    private final BitSet m_bits;  
    
    /**
     * Create a new BooleanColumn. 
     * @param nrows the initial size of the column
     * @param capacity the initial capacity of the column
     * @param defaultValue the default value for the column
     */
    public BooleanColumn(Boolean defaultValue) {this (new BitSet(0), defaultValue);}

    private BooleanColumn(BitSet store, Boolean defaultValue) {
    	super(boolean.class, defaultValue);
    	m_bits = store;
    }
    
    // ------------------------------------------------------------------------
    // Column Metadata
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#getRowCount()
     */
    public int size() {return m_bits.length();}
    
    // ------------------------------------------------------------------------
    // Data Access Methods    
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#get(int)
     */
    public Boolean get(int row) {
        return new Boolean(getBoolean(row));
    }

    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#set(java.lang.Object, int)
     */
    public void set(Boolean val, int row) {
    	if ( val != null ) {
            setBoolean(((Boolean)val).booleanValue(), row);
        } else {
            throw new NullPointerException("Column does not accept null values");
        }
    }

    // ------------------------------------------------------------------------
    // Data Type specific methods
    
    private boolean getBoolean(int row) {
        if ( row < 0 || row > size()) {
            throw new IllegalArgumentException("Row index out of bounds: "+row);
        }
        return m_bits.get(row);
    }

    private void setBoolean(boolean val, int row) {
        if ( row < 0 || row >= size()) {
            throw new IllegalArgumentException("Row index out of bounds: "+row);
        }
        // get the previous value
        boolean prev = m_bits.get(row);
        
        // exit early if no change
        if ( prev == val ) return;
        
        // set the new value
        m_bits.set(row, val);        
    }

	@Override
	public Column update(Object[] vals, int[] targets, int extend) {
		BitSet bits = new BitSet(m_bits.length() + extend);

		if (extend < 0) { //There were deletes, more than there were updates!
			throw new Error("Can't do big deletes yet!");
		} else {			//The bistset is the same size or grew
			bits.or(m_bits);
			for (int i=0; i< vals.length; i++) {
				bits.set(targets[i], (Boolean) vals[i]);
			}
		}

		return new BooleanColumn(bits, m_defaultValue);
	}

	@Override
	public Column replaceAll(Object values) {
		assert values.getClass().isArray();
		assert values.getClass().getComponentType().equals(Boolean.class) || values.getClass().getComponentType().equals(boolean.class);
		
		final int len = Array.getLength(values);
		final BitSet b = new BitSet(len);
		for (int i=0; i< len; i++) {b.set(i, (Boolean) Array.get(values,i));}
			
		return new BooleanColumn(b, m_defaultValue);
	}

} // end of class BooleanColumn
