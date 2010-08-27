tree grammar Predicate_Compact;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
	filter = true;
	output = AST;
  superClass = TreeRewriteSequence;
}

@header {
 /**Optimizes filter rules to be simple invokeables.  
  * Must be done after operator instantiation.
  */

  package stencil.parser.string;
  
  import stencil.parser.tree.Program;
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
}

topdown: ^(PREDICATE ^(RULE . ^(CALL_CHAIN ^(f=FUNCTION inv=. spec=. args=. .+) size=.))) -> ^(PREDICATE[$f.text] $inv $args); 
  		