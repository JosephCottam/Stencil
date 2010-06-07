tree grammar SpecializerValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
  /**Verify that specializers are correct:
    *  (1) No default  tags remain
    *  (2) TODO: Map argument labels all match a an argument key pattern from the meta-data.
    *
    * TODO: Implement #2 above...
  **/
  
  package stencil.parser.string.validators;
  
  import stencil.parser.string.ValidationException;
}

topdown: ^(s=SPECIALIZER DEFAULT) {throw new ValidationException("Default specializer found after all supposedly removed.");};
