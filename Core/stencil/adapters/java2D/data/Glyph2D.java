package stencil.adapters.java2D.data;

public interface Glyph2D extends stencil.adapters.Glyph {
	/**Mark the current as being of concern to an 
	 * updater.  The updater provides the marker to be
	 * used. Updaters need to provide a unique marker.
	 * Marker distribution is provided through the Panel.
	 * **/
	public void markForUpdate(int marker);
	
	/**Does this glyph have the given marker?*/
	public boolean hasMarker(int marker);
	
	/** Calculate and updated version of this 
	 * glyph based upon the field and value passed.
	 * The returned glyph may be a (mutated) copy
	 * of the current glyph or a new glyph.**/
	public Glyph2D update(String field, Object value);
	
	/**Accessor method used in updates, must be provided to reduce indirection costs.*/
	public String getID();
	
	/**Accessor method used in updates, must be provided to reduce indirection costs.*/
	public Table getLayer();
}
