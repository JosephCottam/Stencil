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

    private final BitSet bits; 
    private final int size;
    
    /**
     * Create a new BooleanColumn. 
     * @param nrows the initial size of the column
     * @param capacity the initial capacity of the column
     * @param defaultValue the default value for the column
     */
    public BooleanColumn(Boolean defaultValue) {this (new BitSet(0), 0, defaultValue);}

    private BooleanColumn(BitSet bits, int size, Boolean defaultValue) {
    	super(boolean.class, defaultValue);
    	this.bits = bits;
    	this.size = size;
    }
    
    // ------------------------------------------------------------------------
    // Column Metadata
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#getRowCount()
     */
    public int size() {return size;}
    
    // ------------------------------------------------------------------------
    // Data Access Methods    
    
    /**
     * @see stencil.adapters.java2D.columnStore.column.Column#get(int)
     */
    public Boolean get(int row) {
        return new Boolean(getBoolean(row));
    }

    // ------------------------------------------------------------------------
    // Data Type specific methods
    
    private boolean getBoolean(int row) {
        if ( row < 0 || row > size()) {
            throw new IllegalArgumentException("Row index out of bounds: "+row);
        }
        return bits.get(row);
    }

	@Override
	public Column update(Object[] vals, int[] targets, int extend) {
		BitSet newBits = new BitSet(size() + extend);
		
		if (extend < 0) { //There were deletes, more than there were updates!
			throw new Error("Can't do big deletes yet!");
		} else {			//The bistset is the same size or grew
			newBits.or(bits);
			for (int i=0; i< vals.length; i++) {
				newBits.set(targets[i], (Boolean) vals[i]);
			}
		}

		return new BooleanColumn(newBits, size() + extend, m_defaultValue);
	}

	@Override
	public Column replaceAll(Object values) {
		assert values.getClass().isArray();
		assert values.getClass().getComponentType().equals(Boolean.class) || values.getClass().getComponentType().equals(boolean.class);
		
		final int len = Array.getLength(values);
		final BitSet b = new BitSet(len);
		for (int i=0; i< len; i++) {b.set(i, (Boolean) Array.get(values,i));}
			
		return new BooleanColumn(b, len, m_defaultValue);
	}

} // end of class BooleanColumn
