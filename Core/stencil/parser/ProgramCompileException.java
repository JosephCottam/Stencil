package stencil.parser;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;

import stencil.parser.tree.StencilTree;

/**Error that indicates issue with the semantic structure of the program.
 * These occur during AST manipulation, but only after the basic AST checks have been made.
 */
public class ProgramCompileException extends RuntimeException {

	private final Token location;
	
	public ProgramCompileException(String message) {this(message, null, null);}
	public ProgramCompileException(String message, StencilTree location) {this(message, location, null);}	
	public ProgramCompileException(String message, Exception e) {this(message, null, e);}
	public ProgramCompileException(String message, StencilTree location, Exception e) {
		super(message, e);
		Token tn = location == null ? null : location.getToken();
		this.location = tn == null ? UNKNOWN_LOCATION : tn;
	}

	/**Error message includes the standard message, plus a listing of all included errors.*/
	@Override
	public String getMessage() {
		StringBuilder b = new StringBuilder();
		if (location != UNKNOWN_LOCATION) {
			b.append("Line ");
			b.append(location.getLine());
			b.append(" : ");
			b.append(location.getCharPositionInLine()+1);
			b.append(" (approx) -- ");
		}
		b.append(super.getMessage());
		return b.toString();
	}
	
	private static final Token UNKNOWN_LOCATION = new Token() {
		@Override
		public int getChannel() {return -1;}
		@Override
		public int getCharPositionInLine() {return -1;}
		@Override
		public CharStream getInputStream() {return null;}
		@Override
		public int getLine() {return -1;}
		@Override
		public String getText() {return "UNKNOWN_LOCATION";}
		@Override
		public int getTokenIndex() {return -1;}
		@Override
		public int getType() {return -1;}
		@Override
		public void setChannel(int arg0) {}
		@Override
		public void setCharPositionInLine(int arg0) {}
		@Override
		public void setInputStream(CharStream arg0) {}
		@Override
		public void setLine(int arg0) {}
		@Override
		public void setText(String arg0) {}
		@Override
		public void setTokenIndex(int arg0) {}
		@Override
		public void setType(int arg0) {}
	};
	
}
