tree grammar GuideClean;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;	
  filter = true;
  output = AST;	
}

@header {
  /**Remove compiler annotations that are of no significance for runtime.*/
  
  package stencil.parser.string; 
}

bottomup
  :  ^(GUIDE_DIRECT g=.) -> $g
  |  ^(GUIDE_SUMMARIZATION g=.) -> $g;
