package stencil.parser;

import java.util.Collection;

public final class ProgramParseException extends Exception {
	private Collection<String> errors;
	
	public ProgramParseException(String message, Collection<String> errors) {
		super(message);
		this.errors = errors;
	}
	
	public ProgramParseException(String message, Exception e) {
		super(message, e);
	}
	
	/**Error message includes the standard message, plus a listing of all included errors.*/
	public String getMessage() {
		StringBuilder m = new StringBuilder(super.getMessage());
		if (errors != null) {
			for (String error: errors) {
				m.append("\n\t");
				m.append(error);
			}
		}
		
		return m.toString();
	}
	public Collection<String> getErrors() {return errors;}
}
