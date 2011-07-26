package stencil.display;

/**Error adding an element to a layer based on ID issues.
 * ID issues are:
 *   1) Duplicate ID
 *   2) Null ID
 * @author jcottam
 *
 */
public class IDException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IDException(String layerName) {
		super("Could not add item with null id to layer " + layerName + ".");
	}
 	
	public IDException(Object ID, String layerName) {
		super("Could not add item with ID '" + ID + "' to layer " + layerName + ".");
	}
}
