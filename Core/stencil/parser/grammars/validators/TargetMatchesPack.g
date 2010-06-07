tree grammar TargetMatchesPack;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
  /** Verifies that python blocks contain valid python code.
   * Corrects python block indentation for blocks that have are
   * indented on their first non-blank line. 
   */

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.ValidationException;
}

@members {
  private static final class TargetPackMismatchException extends ValidationException {
      public TargetPackMismatchException() {
        super("");
      }
  }  
}

topdown: ^(RULE target=. chain[((Target) target).getPrototype().size()] .*);
chain[int size] : ^(CALL_CHAIN callTargets[size]);
callTargets[int size]
     : ^(FUNCTION . . . chain[size])
     | ^(p=PACK .*)
        {if (size != p.getChildCount()) {throw new TargetPackMismatchException();}};

  