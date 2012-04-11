tree grammar GuideClean;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;	
  filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Remove compiler annotations that are of no significance for runtime.*/
  
  package stencil.parser.string;
  
  import stencil.parser.tree.StencilTree; 
  import stencil.parser.string.util.TreeRewriteSequence;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

bottomup
  :  ^(GUIDE_DIRECT g=.) -> $g
  |  ^(GUIDE_SUMMARIZATION g=.) -> $g;
