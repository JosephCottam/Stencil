package stencil.explore.util;

import java.io.PrintStream;

/**Basic wrapper of a StringBuilder as target for message reports*/
public class StringMessageReporter implements MessageReporter {
	private StringBuilder b = new StringBuilder();
	
	/**Echo a message to System.err.*/
	@Override
	public void addError(String message, Object...args) {
		if (message == null) {return;}
		b.append(String.format(message, args));
	}	
	/**Echo a message to System.out*/
	@Override
	public void addMessage(String message, Object...args) {
		if (message ==null) {return;}
		b.append(String.format(message, args));
	}
	

	@Override
	public void clear() {b = new StringBuilder();}
	public String messages() {return b.toString();}
	

	
	@Override
	public PrintStream errorAsStream() {
		return new PrintStream(new ReporterAsStream(this));
	}
}
