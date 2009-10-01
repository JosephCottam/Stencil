package stencil.adapters.java2D.data;

import stencil.streams.Tuple;

public interface Glyph2D extends stencil.adapters.Glyph, Renderable {
	/**Calculate an updated version of this glyph based on the passed tuple.
	 * The returned glyph will have field values equal to the passed tuple
	 * for any field that appears in the passed tuple and current value
	 * for any field that does not.  All fields in the passed
	 * tuple must appear in the current tuple or an illegalArugmentException is thrown.
	 */
	public Glyph2D update(Tuple t) throws IllegalArgumentException;
	
	/** Copy the current glyph, but replace its name.*/
	public Glyph2D updateID(String id);
	
	/**Accessor method used in updates; provided to reduce indirection costs.*/
	public String getID();
	
	/**Accessor method used in updates; provided to reduce indirection costs.*/
	public DisplayLayer getLayer();

	/**Accessor method used in drawing; provided to reduce indirection costs.*/
	public boolean isVisible();

}
