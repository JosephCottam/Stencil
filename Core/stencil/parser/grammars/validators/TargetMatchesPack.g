tree grammar TargetMatchesPack;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Verifies that all packs have the same number of outputs as the target definition.*/

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.ParseStencil;
  import stencil.parser.string.ValidationException;
  import stencil.parser.string.TreeFilterSequence;
}

@members {
  private static final class TargetPackMismatchException extends ValidationException {
      public TargetPackMismatchException() {
        super("");
      }
  }

  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown: ^(RULE target=. chain[((Target) target).getPrototype().size()] .*);
chain[int size] : ^(CALL_CHAIN callTargets[size]);
callTargets[int size]
     : ^(FUNCTION . . . chain[size])
     | ^(p=PACK .*)
        {if (size != p.getChildCount()) {throw new TargetPackMismatchException();}};

  