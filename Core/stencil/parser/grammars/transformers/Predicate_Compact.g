tree grammar Predicate_Compact;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
	filter = true;
	output = AST;
}

@header {
 /**Optimizes filter rules to be simple invokeables.  
  * Must be done after operator instantiation.
  */

  package stencil.parser.string;
}

@members {	 

}


topdown: ^(PREDICATE ^(RULE . ^(CALL_CHAIN ^(f=FUNCTION inv=. spec=. args=. .+) size=.))) -> ^(PREDICATE[$f.text] $inv $args); 
  		