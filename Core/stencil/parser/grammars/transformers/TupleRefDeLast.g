tree grammar TupleRefDeLast;
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
   *  TODO: Will currently only work for immediate prior tuple, not for arbitrary nesting...
   *  TODO: Will currently mess up numeric references into the prior frame.  Need to name all frames, frame tuple refs by name THEN do this transform.  Later, numeralize the tuple refs.  Would also make it so that echo operators no longer need to echo, they can just sample (and be named samplers!)
   **/
   
  package stencil.parser.string;
  
  import stencil.parser.tree.Program;
}

@members {
   public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
   }
}

topdown
   : ^(f=FUNCTION spec=. ^(LIST preArgs+=.* ^(TUPLE_REF LAST) postArgs+=.*) pass=. call=.)
       -> ^(FUNCTION["ToArray.query"] ^(SPECIALIZER DEFAULT) ^(LIST ^(TUPLE_REF ALL)) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX]
       		^(FUNCTION[$f.text] $spec ^(LIST $preArgs* ^(TUPLE_REF NUMBER["0"]) $postArgs*) $pass $call))
   | ^(PACK preArgs+=.* ^(TUPLE_REF LAST) postArgs+=.*)
   	   -> ^(FUNCTION["ToArray.query"] ^(SPECIALIZER DEFAULT) ^(LIST ^(TUPLE_REF ALL)) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX]
   	   		^(PACK $preArgs* ^(TUPLE_REF NUMBER["0"]) $postArgs*));