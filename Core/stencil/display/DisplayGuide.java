package stencil.display;

import java.util.List;

import stencil.util.AutoguidePair;

/**Conforming to this interface is required for guides used as automatic generation targets.
 * 
 * Additionally, if a custom default specializer is desired, a public static field DEFAULT_ARGUMENTS
 * should be defined (otherwise, a zero-argument list will be used).
 */
public interface DisplayGuide {
	/**Given a set of autoguide pairs, set the appropriate fields in the guide itself.*/
	
	public void setElements(List<AutoguidePair> elements);
}
