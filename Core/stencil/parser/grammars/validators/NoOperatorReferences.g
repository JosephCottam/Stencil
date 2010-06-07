tree grammar NoOperatorReferences;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
/* Verifies that all operator references have been removed.*/
  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.ValidationException;
  
}

@members {
  private static final class ReferenceNotRemovedException extends ValidationException {
  	public ReferenceNotRemovedException(String name, String base) {
  		super(String.format("Operator \%1\$s not instantiated (base \%2\$s).", name, base));
  	}
  }  
}



topdown
 : ^(r=OPERATOR_REFERENCE base=. spec=.) {throw new ReferenceNotRemovedException($r.getText(), $base.getText());};
  
  