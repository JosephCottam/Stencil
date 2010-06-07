tree grammar GuideDefaultSelector;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
  output = AST;	
}

@header {
  /**Determines the default selector attribute.*/

	package stencil.parser.string;

	import stencil.parser.tree.*;
}

@members {

}

topdown: 
  ^(SELECTOR DEFAULT p=path) -> ^(SELECTOR[$p.att] ID[$p.att] $p);

path returns [String att]: ^(LIST i=ID+) {$att=$i.text;};