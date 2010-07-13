package stencil.display;

import stencil.tuple.Tuple;

/**A glyph is a tuple that will eventually be rendered.
 * The graphics adapter is primarily concerned with
 * instantiating glyphs based on interpreter results
 * and later rendering those glyphs.
 */
public interface Glyph extends Tuple {
	/**Get an identifier for this glyph.*/
	public String getID();
	
	/**Update a glyph given a set of values.
	 * 
	 * The returned glyph MAY be originally provided glyph,
	 * depending on adapter implementation.
	 * It is suggested that glyphs be immutable, and this
	 * method on return the original glyph if the values
	 * passed do not represent a change.
	 * */
	public Glyph update(Tuple values);
}
