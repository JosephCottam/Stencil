package stencil.adapters;

import stencil.streams.MutableTuple;

/**Tagging interface for the glyph representation of an adapter.
 * Must be a mutable tuple as well so it can be used in the interpreter,
 * but provides no additional functionality. It is suggested that each 
 * adapter have a particular class implement glyph, and all other glyphs
 * derive from that adapter-specific class. 
 */
public interface Glyph extends MutableTuple {
	/*Tagging interface, no methods defined.*/
}
