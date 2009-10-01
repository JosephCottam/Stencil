package stencil.adapters.java2D.data;

import java.util.List;
import stencil.display.DisplayGuide;
import stencil.util.AutoguidePair;

/**Conforming to this interface is required for guides used as automatic generation targets.
 * Additionally, if a custom default specializer is desired, a public static field DEFAULT_ARGUMENTS
 * should be defined (otherwise, a zero-argument list will be used).
 */
public interface Guide2D extends DisplayGuide, Renderable {
	
	/**Given a set of autoguide pairs, create an appropraite axis representation.
	 * 
	 * @param elements
	 */
	public void setElements(List<AutoguidePair> elements);


}
