tree grammar AnimatedBinding;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
	superClass = TreeRewriteSequence;
	filter = true;
	output = AST;
}

@header {
/** Replaces animated bindings with appropriate static,dynamic and local bindings. **/

  package stencil.parser.string;
  import stencil.parser.tree.StencilTree;
}

@members {
  public static StencilTree apply (Tree t) {
     return (StencilTree) TreeRewriteSequence.apply(t);
  }
}

topdown
    : ^(RULE target=. chain=. ANIMATED) -> ^(RULE $target $chain DEFINE); 
//TODO: Add animated dynamic