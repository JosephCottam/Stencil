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
   Places that definition in a standard location.
   If there are no layers defined and the view/canvas were not directly defined, does not generate a new one. 
   
   */
    package stencil.parser.string;

   import stencil.parser.tree.StencilTree;
   import stencil.parser.string.util.TreeRewriteSequence;	
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public static int layerCount(StencilTree t) {
    return t.getAncestor(PROGRAM).findAllDescendants(LAYER).size();
  } 
}

topdown
    : ^(LIST_CANVAS def=.) -> $def
    | ^(LIST_VIEW def=.) -> $def
    | lc=LIST_CANVAS 
        -> {layerCount($lc) > 0}? ^(CANVAS["default"] ^(SPECIALIZER DEFAULT) LIST_CONSUMES)
        -> LIST_CANVAS
    | lv=LIST_VIEW 
        -> {layerCount($lv) > 0}? ^(VIEW["default"] ^(SPECIALIZER DEFAULT) LIST_CONSUMES)
        -> LIST_VIEW;
    
	 