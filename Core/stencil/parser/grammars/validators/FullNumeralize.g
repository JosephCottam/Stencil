tree grammar FullNumeralize;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
  /**Verifies that all tuple-refs inside of operator calls are numeric (not names).**/
   

  package stencil.parser.string.validators;

  import stencil.parser.string.StencilParser;
  import stencil.parser.string.ValidationException;
}


//TODO: Remove if statement when numeralize is done in return values as well
topdown
 : ^(t=TUPLE_REF ID .*) 
    {if (t.getAncestor(StencilParser.FUNCTION) != null
          || t.getAncestor(StencilParser.PACK) != null
          || t.getAncestor(StencilParser.PREDICATE) != null) {
        throw new ValidationException("Non-numeralized tuple ref outside of target: " + $t.toStringTree());
      }};
  
  