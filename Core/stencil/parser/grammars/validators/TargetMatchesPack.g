tree grammar TargetMatchesPack;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Verifies that all packs have the same number of outputs as the target definition.*/

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.ValidationException;
  import stencil.parser.string.TreeFilterSequence;
}

@members {
  public static final class TargetPackMismatchException extends ValidationException {
      public TargetPackMismatchException() {super("");}
  }

  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown: ^(RULE target=. chain[target.getFirstChildWithType(TUPLE_PROTOTYPE).getChildCount()] .*);
chain[int size] : ^(CALL_CHAIN callTargets[size]);
callTargets[int size]
     : ^(FUNCTION . . . chain[size])
     | ^(p=PACK .*)
        {if (size != p.getChildCount()) {throw new TargetPackMismatchException();}};

  