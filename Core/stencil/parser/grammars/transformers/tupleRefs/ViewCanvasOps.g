tree grammar ViewCanvasOps;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Add reference to the view and canvas operators if they are used in the call chains. 
   *  Any rule with a reference to view or canvas is pre-pended
   *  with a zero-argument operator call that returns the approprite
   *  property tuple.
   **/
   
  package stencil.parser.string;

  import java.util.Map;

  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.parser.tree.StencilTree;
  import static stencil.parser.ParserConstants.VIEW_FRAME;
  import static stencil.parser.ParserConstants.CANVAS_FRAME;
  import stencil.interpreter.tree.Freezer;
  
  import static stencil.modules.stencilUtil.SpecialTuples.VIEW_TUPLE_OP;
  import static stencil.modules.stencilUtil.SpecialTuples.CANVAS_TUPLE_OP;
}

@members {
  public static StencilTree apply (StencilTree t) {
     return (StencilTree) TreeRewriteSequence.apply(t);
  }
  
  private boolean hasRefTo(StencilTree t, String frameName) {
       List<StencilTree> refs = t.findAllDescendants(TUPLE_REF);
       for (StencilTree ref: refs) {
          if (ref.getChild(0).is(ID) 
                && ref.getChild(0).getText().equals(frameName)) {
             return true;
           }
       }
       return false;
  }
  
  private boolean hasViewRef(StencilTree t) {return hasRefTo(t, VIEW_FRAME);}
  private boolean hasCanvasRef(StencilTree t) {return hasRefTo(t, CANVAS_FRAME);}

}

//Add a view and/or canvas tuple producer call in any chain that refers to a view or canvas attribute
topdown: ^(c=CALL_CHAIN call=.)
    -> {hasViewRef($c) && hasCanvasRef($c)}? 
        ^(CALL_CHAIN 
            ^(FUNCTION ^(OP_NAME DEFAULT ID[VIEW_TUPLE_OP] DEFAULT_FACET) SPECIALIZER LIST_ARGS DIRECT_YIELD[VIEW_FRAME]
                ^(FUNCTION ^(OP_NAME DEFAULT ID[CANVAS_TUPLE_OP] DEFAULT_FACET) SPECIALIZER LIST_ARGS DIRECT_YIELD[CANVAS_FRAME]
                  $call)))
    -> {hasViewRef($c)}?
        ^(CALL_CHAIN 
           ^(FUNCTION ^(OP_NAME DEFAULT ID[VIEW_TUPLE_OP] DEFAULT_FACET) SPECIALIZER LIST_ARGS DIRECT_YIELD[VIEW_FRAME]
               $call))
    
    -> {hasCanvasRef($c)}?
        ^(CALL_CHAIN 
           ^(FUNCTION ^(OP_NAME DEFAULT ID[CANVAS_TUPLE_OP] DEFAULT_FACET) SPECIALIZER LIST_ARGS DIRECT_YIELD[CANVAS_FRAME]
               $call))

    -> ^(CALL_CHAIN $call);
