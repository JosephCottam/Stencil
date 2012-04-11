tree grammar ViewCanvasSingleDef;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Verifies that at most one view/canvas def is provided.*/

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.ValidationException;
  import stencil.parser.string.util.TreeFilterSequence;
}


@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}


topdown
  : ^(LIST_VIEW . .+) {throw new ValidationException("More than one view definition found.");}
  | ^(LIST_CANVAS . .+) {throw new ValidationException("More than one canvas definition found.");};
