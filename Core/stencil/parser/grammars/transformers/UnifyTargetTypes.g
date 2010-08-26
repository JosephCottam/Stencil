tree grammar UnifyTargetTypes;
options {
    tokenVocab = Stencil;
    ASTLabelType = CommonTree;	
    output = AST;
    filter = true;
    superClass = TreeRewriteSequence;
}

@header {
/** Takes all target types and replaces them with target nodes.*/

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import static stencil.parser.string.StencilParser.*;
}

@members {
  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }
}

//Remove individual types
topdown
    : ^(LOCAL s+=.*)     -> ^(TARGET["Local"] $s*)
    | ^(CANVAS s+=.*)    -> ^(TARGET["Canvas"] $s*)
    | ^(VIEW s+=.*)      -> ^(TARGET["View"] $s*)
    | ^(PREFILTER s+=.*) -> ^(TARGET["Prefilter"] $s*)
    | ^(RESULT s+=.*)    -> ^(TARGET["Result"] $s*);
    
	 