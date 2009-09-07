package stencil.adapters.java2D.data;

import stencil.streams.Tuple;

public interface Glyph2D extends stencil.adapters.Glyph {
	/**Mark the current as being of concern to an 
	 * updater.  The updater provides the marker to be
	 * used. Updaters need to provide a unique marker.
	 * Marker distribution is provided through the Panel.
	 * **/
	public void markForUpdate(int marker);
	
	/**Does this glyph have the given marker?*/
	public boolean hasMarker(int marker);
	
	/** Calculate an updated version of this 
	 * glyph based upon the field and value passed.
	 * The returned glyph may be a (mutated) copy
	 * of the current glyph or a new glyph.**/
	public Glyph2D update(String field, Object value);
	
	/**Calculate an updated version of this glyph based on the passed tuple.
	 * The returned glyph will have field values equal to the passed tuple
	 * for any field that appears in the passed tuple and current value
	 * for any field that does not.  All fields in the passed
	 * tuple must appear in the current tuple or an illegalArugmentException is thrown.
	 *  
	 */
	public Glyph2D update(Tuple t) throws IllegalArgumentException;
	
	/**Accessor method used in updates, must be provided to reduce indirection costs.*/
	public String getID();
	
	/**Accessor method used in updates, must be provided to reduce indirection costs.*/
	public Table getLayer();
		
	public void setLayer(Table t);
}
