package stencil.adapters.java2D.data;

import stencil.streams.Tuple;

public interface Glyph2D extends stencil.adapters.Glyph {
	/** Calculate an updated version of this 
	 * glyph based upon the field and value passed.
	 * The returned glyph may be the same glyph
	 * if it represents no change, otherwise it 
	 * is a new glyph.
	 */
	public Glyph2D update(String field, Object value);
	
	/**Calculate an updated version of this glyph based on the passed tuple.
	 * The returned glyph will have field values equal to the passed tuple
	 * for any field that appears in the passed tuple and current value
	 * for any field that does not.  All fields in the passed
	 * tuple must appear in the current tuple or an illegalArugmentException is thrown.
	 */
	public Glyph2D update(Tuple t) throws IllegalArgumentException;
	
	/**Accessor method used in updates, must be provided to reduce indirection costs.*/
	public String getID();
	
	/**Accessor method used in updates, must be provided to reduce indirection costs.*/
	public Table getLayer();
		
	public void setLayer(Table t);
}
