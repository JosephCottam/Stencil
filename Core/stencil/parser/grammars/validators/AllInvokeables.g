tree grammar AllInvokeables;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
/* Verifies that all AST_INVOKEABLE nodes have an invokeable set.*/ 
  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.ValidationException;
  import stencil.parser.ParseStencil;
  import stencil.parser.string.TreeFilterSequence;
}

@members {
  private static final class OperatorMissingException extends ValidationException {
  	public OperatorMissingException(String name) {
  		super("Operator missing for \%1\$s.", name);
  	}
  }  

  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown: i = AST_INVOKEABLE 
      {if (((AstInvokeable) i).getInvokeable() == null) {throw new OperatorMissingException("AST Invokeable.");}};  