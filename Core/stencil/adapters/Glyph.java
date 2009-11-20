package stencil.adapters;

import stencil.tuple.Tuple;

/**Tagging interface for the glyph representation of an adapter.
 * It is suggested that each 
 * adapter have a particular class implement glyph, and all other glyphs
 * derive from that adapter-specific class. 
 */
public interface Glyph extends Tuple {
	/*Tagging interface, no methods defined.*/
}
