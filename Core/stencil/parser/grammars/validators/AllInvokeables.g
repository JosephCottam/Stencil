tree grammar AllInvokeables;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
/* Verifies that all AST_INVOKEABLE nodes have an invokeable set.*/ 
  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.ValidationException;
  
}

@members {
  private static final class OperatorMissingException extends ValidationException {
  	public OperatorMissingException(String name) {
  		super("Operator missing for \%1\$s.", name);
  	}
  }  

  private void validate(AstInvokeable i) {
      if (i.getInvokeable() == null) {throw new OperatorMissingException("AST Invokeable.");}
  }
}

topdown: i = AST_INVOKEABLE {validate((AstInvokeable) i);};
  
  