package stencil.adapters.java2D.data;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

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
	
	/**Accessor method used in updates; provided to reduce indirection costs.*/
	public String getID();
	
	/**Accessor method used in updates; provided to reduce indirection costs.*/
	public Table getLayer();

	/**Accessor method used in drawing; provided to reduce indirection costs.*/
	public boolean isVisible();
	
	/**Get the bounding box of the glyph.  The bounding box returned from
	 * this method MAY BE (but is not necessarily) a direct pointer to an
	 * internally maintained bounding box.  It is generally not safe to modify
	 * the value returned by this method.
	 *  
	 * @return
	 */
	public Rectangle2D getBoundsReference();
	
	/**Render the glyph to the given graphics object.  
	 * Before returning, the graphics' transform should
	 * be the same as the passed transform.
	 * 
	 * @param g Graphics to render on
	 * @param viewTransform Required value of the graphics transform at method termination
	 */
	public void render(Graphics2D g, AffineTransform viewTransform);
}
