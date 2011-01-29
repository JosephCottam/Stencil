tree grammar LastToAll;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Converts tuple references using LAST into 
   *  operator calls on ALL that produce a singleton tuple
   *  with the values.  This is one step towards making all tuple
   *  references numeric at runtime.
   *
   *  Semanticall ALL retrieves a tuple from the runtime as a tuple.  
   *  Whereas LAST refers to just the values in the tuple.  
   *  Therefore, LAST can be replaced with a call to ToArray(ALL).
   *
   *  TODO: Will currently only work for immediate prior tuple, not for arbitrary nesting...
   **/
   
  package stencil.parser.string;
  
  import stencil.parser.tree.StencilTree;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

}

topdown
   : ^(f=FUNCTION spec=. ^(LIST_ARGS preArgs+=.* ^(TUPLE_REF LAST) postArgs+=.*) pass=. call=.)
       -> ^(FUNCTION["ToArray.query"] ^(SPECIALIZER DEFAULT) ^(LIST_ARGS ^(TUPLE_REF ALL)) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX]
       		^(FUNCTION[$f.text] $spec ^(LIST_ARGS $preArgs* ^(TUPLE_REF NUMBER["0"]) $postArgs*) $pass $call))
   | ^(PACK preArgs+=.* ^(TUPLE_REF LAST) postArgs+=.*)
   	   -> ^(FUNCTION["ToArray.query"] ^(SPECIALIZER DEFAULT) ^(LIST_ARGS ^(TUPLE_REF ALL)) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX]
   	   		^(PACK $preArgs* ^(TUPLE_REF NUMBER["0"]) $postArgs*));