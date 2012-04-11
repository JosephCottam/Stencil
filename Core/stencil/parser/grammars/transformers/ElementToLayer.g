tree grammar ElementToLayer;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
 /**Converts element declarations to layer declarations.
  * TODO: When elements get special treatment in operator creation, retain the element label a little longer.
  **/
  package stencil.parser.string;
  
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.TreeRewriteSequence;
}

@members{
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

topdown
  : ^(e=ELEMENT type=. defaults=. consumes[$e.text]*) 
      -> ^(LAYER[$e.text] $type $defaults consumes)
  ;

consumes[String name]
  : ^(LIST_CONSUMES filters=. rules[name])
  ;
  
rules[String name]
  : ^(LIST_RULES rule+=.*) -> 
  		^(LIST_RULES ^(RULE ^(TARGET_TUPLE ^(TUPLE_FIELD STRING["ID"])) ^(CALL_CHAIN ^(PACK STRING[$name]) DEFINE)) $rule+)
  ;

  
