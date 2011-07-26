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
      public TargetPackMismatchException(StencilTree chain) {super("Target/Pack mismatch for: " + chain.toStringTree());}
  }

  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown: (p=PACK .*)
    {StencilTree rule = p.getAncestor(RULE);
     if (rule != null && rule.findDescendant(TARGET_TUPLE).getChildCount() != $p.getChildCount()) {
        throw new TargetPackMismatchException(rule);
     }
    };