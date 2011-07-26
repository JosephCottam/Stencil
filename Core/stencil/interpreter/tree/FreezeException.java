package stencil.interpreter.tree;

import org.antlr.runtime.tree.Tree;

import stencil.parser.tree.Path;

public class FreezeException extends RuntimeException {
	public FreezeException(Tree root, Exception e) {
		super("Error freezing: " + Path.toString(root) , e);
	}
	
	public FreezeException(Exception e) {
		super("Error freezing. ", e);
	}
	public FreezeException(Tree root, String message) {
		super(message + " -- " + Path.toString(root));
	}
}
