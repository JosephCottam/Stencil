package stencil.parser.tree;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

public class ErrorNode extends StencilTree {
	final Token start;
	final Token end;
	final RecognitionException e;
	
	public ErrorNode(Token start, Token end,
			RecognitionException e) {
		super(null);
		this.start = start;
		this.end = end;
		this.e = e;
	}
	
	@Override
	public String toString() {
		return "Error '" + e.getMessage() + "'"
			+ " at text " + start.getText()
			+ "(line " + start.getLine()
			+ ":" + start.getCharPositionInLine() + ")";
	}
	
	

}
