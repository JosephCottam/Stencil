tree grammar TupleRefUnchain;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
/* Converts the list format tuple-refs into a chain format tuple ref.*/

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import stencil.parser.ParseStencil;
}

@members {
  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }
}

bottomup:
  ^(TUPLE_REF ns=NUMBER* ^(TUPLE_REF nn=NUMBER)) -> ^(TUPLE_REF $ns $nn);	 
	 
	 