tree grammar SimplifyViewCanvas;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree;	
    output = AST;
    filter = true;
    superClass = TreeRewriteSequence;
}

@header {
/**Ensures there is only one view and one canvas declaration.
   Places that definition in a standard location.*/
    package stencil.parser.string;

	import stencil.parser.tree.StencilTree;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

topdown
    : ^(LIST_CANVAS def=.) -> $def
    | ^(LIST_VIEW def=.) -> $def
    | LIST_CANVAS -> ^(CANVAS["default"] ^(SPECIALIZER DEFAULT) LIST_CONSUMES)
    | LIST_VIEW -> ^(VIEW["default"] ^(SPECIALIZER DEFAULT) LIST_CONSUMES);
    
	 