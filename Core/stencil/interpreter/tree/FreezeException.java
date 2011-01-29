package stencil.interpreter.tree;

import org.antlr.runtime.tree.Tree;

import stencil.parser.tree.util.Path;

public class FreezeException extends RuntimeException {
	public FreezeException(Tree root, Exception e) {
		super("Error freezing: " + Path.toString(root) , e);
	}
}
