package stencil.operator.module;

/**Indicates there was an error creating an instance of a method.*/
public class MethodInstanceException extends Exception {
	protected boolean found;

	public MethodInstanceException(String name, boolean found, Exception e) {
		super(String.format("Error creating instance of method %1$s.", name), e);
		this.found = found;
	}
	
	public MethodInstanceException(String name, boolean found) {this(name, found, null);}
	
	/**Was the exception caused by specialization?*/
	public boolean specialization() {return !found;}
	
	/**Was the method found?*/
	public boolean found() {return found;}
}
