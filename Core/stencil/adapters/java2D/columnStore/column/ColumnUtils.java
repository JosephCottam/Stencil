package stencil.adapters.java2D.columnStore.column;

import java.lang.reflect.Array;

import stencil.adapters.java2D.columnStore.Table;
import static stencil.adapters.java2D.render.Renderer.CHILDREN;

/** Utilities for  working with columns.*/
public class ColumnUtils {	
	/**
     * Get a new column of the given type.  Used by tables during construction.
     * 
     * Note: If creating a ReferenceColumn, 
     * ReferenceColumn.CONTENT and the returned column will need to have its target table set before use.
     * 
     * @param type the column data type
     * @param defaultValue the default value for the column
     * @return the new column
     */
    public static final <T> Column getColumn(Class<T> type, T defaultValue)
    {
        if ( type == int.class || type == Integer.class) {
            if (defaultValue == null ) {defaultValue = (T) new Integer(0);}
            return new IntColumn((Integer) defaultValue);
        }
        else if ( type == long.class || type == Long.class)
        {
            if ( defaultValue == null ) {defaultValue = (T) new Long(0);}
            return new LongColumn((Long) defaultValue);
        }
        else if (type == float.class || type == Float.class)
        {
            if (defaultValue == null ) {defaultValue = (T) new Float(0);}
            return new FloatColumn((Float) defaultValue);
        }
        else if (type == double.class || type == Double.class)
        {
            if ( defaultValue == null ) {defaultValue = (T) new Double(0);}
            return new DoubleColumn((Double) defaultValue);
        }
        else if ( type == boolean.class || type == Boolean.class)
        {
            if ( defaultValue == null ) {defaultValue = (T) new Boolean(true);}
            return new BooleanColumn((Boolean) defaultValue);
        } 
        else if (type == CHILDREN.type()) {
        	return new ReferenceColumn(null);
        }
        else
        {
            return new ObjectColumn(type, defaultValue);
        }
    }
    
    /**
     * Get a new column of a constant value.
     * @param type the column data type
     * @param dflt the default constant value for the column
     * @return the new column
     */
    public static final Column getConstantColumn(Class type, Object dflt) {
        return new ConstantColumn(type, dflt);
    }
    

    /**General method for extending arrays from the arguments for the Columns interface extend method.
     * 
     * @param defVal Value to use if when the column default is needed
     * @param basis Current values
     * @param values New values
     * @param mask  Where the new values need to be put
     * @param extend The number of new rows required
     * **/
    public static Object extend(Object defVal, Object basis, Object[] values, int[] mask, int extend) {
    	Class clazz = basis.getClass();
    	assert clazz.isArray() : "Only accepts arrays to extend with, recieved: " + clazz.toString();
    	clazz = clazz.getComponentType();
    	
    	
    	try {
			Object array = Array.newInstance(clazz, Array.getLength(basis) + extend);
			System.arraycopy(basis, 0, array, 0, Math.min(Array.getLength(basis), Array.getLength(array)));				//Copy old values
			
			if (extend < 0) { //There were deletes, more than there were updates!
				throw new Error("Can't do big deletes yet!");							//TODO: Implement deletes so that it copies tailing items over deleted items (needs to munge the index elsewhere after that though)
			} else {			//The array is the same size or grew
				final int length = values.length;
				for (int i=0; i< length; i++) {
					Object val = values[i];
					Array.set(array, mask[i], val);
				}
			}
			return array;
		} catch (Exception e) {throw new Error("Error extending column", e);}
    }
    
    public static String toString(String name, Column c) {
    	if (c instanceof ConstantColumn) {
    		return name + " (const): " + c.getDefaultValue();
    	} else {
    		StringBuilder b = new StringBuilder();
    		b.append(name);
    		b.append(": ");
    		for (int i=0;i < c.size(); i++) {
    			b.append(c.get(i));
    		}
    		b.deleteCharAt(b.length()-1);
    		return b.toString();
    	}
    }
    
    public static void printTable(Table source) {
		System.err.println("Size reported: " + source.tenured().size());
		for (int col=0;col<source.tenured().columns().length;col++) {
			String name = source.prototype().get(col).name();
			Column column = source.tenured().columns()[col];
			System.err.println(ColumnUtils.toString(name, column));
		}
    }
}
