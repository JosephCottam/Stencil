tree grammar ElementToLayer;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
 /**Converts element declarations to layer declarations.
  * TODO: When elements get special treatment in operator creation, retain the element label a little longer.
  **/
  package stencil.parser.string;
  
  import stencil.parser.tree.Program;
}

@members{
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
}

topdown
  : ^(e=ELEMENT type=. defaults=. rules[$e.text]) -> ^(LAYER[$e.text] $type $defaults rules);

  
rules[String name]
  : ^(LIST rule+=.*) -> 
  		^(LIST ^(RULE ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF STRING["ID"] DEFAULT)) ^(CALL_CHAIN ^(PACK STRING[$name]) DEFINE)) $rule+);

  
