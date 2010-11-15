tree grammar LastToAll;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
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
  
  import stencil.parser.tree.Program;
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}

}

topdown
   : ^(f=FUNCTION spec=. ^(LIST preArgs+=.* ^(TUPLE_REF LAST) postArgs+=.*) pass=. call=.)
       -> ^(FUNCTION["ToArray.query"] ^(SPECIALIZER DEFAULT) ^(LIST ^(TUPLE_REF ALL)) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX]
       		^(FUNCTION[$f.text] $spec ^(LIST $preArgs* ^(TUPLE_REF NUMBER["0"]) $postArgs*) $pass $call))
   | ^(PACK preArgs+=.* ^(TUPLE_REF LAST) postArgs+=.*)
   	   -> ^(FUNCTION["ToArray.query"] ^(SPECIALIZER DEFAULT) ^(LIST ^(TUPLE_REF ALL)) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX]
   	   		^(PACK $preArgs* ^(TUPLE_REF NUMBER["0"]) $postArgs*));