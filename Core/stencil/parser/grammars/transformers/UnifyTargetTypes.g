tree grammar UnifyTargetTypes;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree;	
    output = AST;
    filter = true;
    superClass = TreeRewriteSequence;
}

@header {
/** Takes all target types and replaces them with target nodes.*/

	package stencil.parser.string;
	
	import stencil.parser.tree.StencilTree;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

//Remove individual types
topdown
    : ^(LOCAL s+=.*)     -> ^(TARGET["Local"] $s*)
    | ^(CANVAS s+=.*)    -> ^(TARGET["Canvas"] $s*)
    | ^(VIEW s+=.*)      -> ^(TARGET["View"] $s*)
    | ^(PREFILTER s+=.*) -> ^(TARGET["Prefilter"] $s*)
    | ^(RESULT s+=.*)    -> ^(TARGET["Result"] $s*);
    
	 