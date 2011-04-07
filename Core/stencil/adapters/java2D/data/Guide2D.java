package stencil.adapters.java2D.data;

import stencil.display.DisplayGuide;
import stencil.interpreter.tree.Guide;

/**Conforming to this interface is required for guides used as automatic generation targets.
 * Additionally, if a custom default specializer is desired, a public static field DEFAULT_ARGUMENTS
 * should be defined (otherwise, a zero-argument list will be used).
 */
public abstract class Guide2D implements DisplayGuide, Renderable {
	protected final String identifier;
	protected Guide2D(Guide def) {
		identifier = def.identifier();
	}
	
	/**What is this guide for?*/
	public String getIdentifier() {return identifier;}
}
