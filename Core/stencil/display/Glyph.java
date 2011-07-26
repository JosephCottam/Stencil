package stencil.display;

import java.awt.geom.Rectangle2D;

import stencil.tuple.PrototypedTuple;

/**A glyph is a tuple that will eventually be rendered.
 * The graphics adapter is primarily concerned with
 * instantiating glyphs based on interpreter results
 * and later rendering those glyphs.
 * 
 * This is principally a tagging interface, but the ID
 * allows the glyph to be tied back to source data.
 */
public interface Glyph extends PrototypedTuple {
	/**Get an identifier for this glyph.*/
	public Comparable getID();
	
	public boolean isVisible();
	
	public Rectangle2D getBoundsReference();
}
