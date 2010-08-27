tree grammar GuideClean;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;	
  filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Remove compiler annotations that are of no significance for runtime.*/
  
  package stencil.parser.string;
  
  import stencil.parser.tree.Program; 
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
}

bottomup
  :  ^(GUIDE_DIRECT g=.) -> $g
  |  ^(GUIDE_SUMMARIZATION g=.) -> $g;
