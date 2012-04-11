tree grammar OperatorProxiesPopulated;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
/* Verifies that all OPERATOR_PROXY nodes have an operator set.*/ 
  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.util.ValidationException;
  import stencil.parser.string.util.TreeFilterSequence;
}

@members {
  private static final class OperatorMissingException extends ValidationException {
  	public OperatorMissingException(String name) {
  		super("Operator missing for \%1\$s.", name);
  	}
  }  

  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown: i = OPERATOR_PROXY 
      {if (((OperatorProxy) i).getOperator() == null) {throw new OperatorMissingException($i.getText());}};  