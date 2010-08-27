tree grammar GuideDefaultSelector;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Determines the default selector attribute.*/

	package stencil.parser.string;

	import stencil.parser.tree.*;
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
}

topdown: 
  ^(SELECTOR DEFAULT p=path) -> ^(SELECTOR[$p.att] ID[$p.att] $p);

path returns [String att]: ^(LIST i=ID+) {$att=$i.text;};