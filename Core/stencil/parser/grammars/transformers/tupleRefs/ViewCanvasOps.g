tree grammar ViewCanvasOps;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Manipulate view and canvas operators according to the 
   *   tuple references that are found in call chains.
   *  Any rule with a reference to view or canvas is pre-pended
   *  with a zero-argument operator call that returns the approprite
   *  property tuple.
   **/
   
  package stencil.parser.string;

  import java.util.Map;
  import stencil.parser.tree.StencilTree;
  import static stencil.parser.ParserConstants.VIEW_FRAME;
  import static stencil.parser.ParserConstants.CANVAS_FRAME;
  import stencil.interpreter.tree.Freezer;
}

@members {
  private static enum Direction {ADD, REMOVE, NONE};
  private Direction direction = Direction.NONE;

  public synchronized static StencilTree add(Tree t) {
     return (StencilTree) TreeRewriteSequence.apply(t, Direction.ADD);
  }

  public synchronized static StencilTree remove(Tree t) {
     return (StencilTree) TreeRewriteSequence.apply(t, Direction.REMOVE);
  }
  
  protected void setup(Object... args) {
     direction = (Direction) args[0];
  }

  public StencilTree downup(Object t) {
     switch(direction) {
       case ADD: return (StencilTree) downup(t, this, "add");
       case REMOVE: return (StencilTree) downup(t, this, "remove");
       default: throw new Error("Incorrectly configured transformation requested.");
     }
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

  private StencilTree trimCall(StencilTree original) {
      assert original.getType() == CALL_CHAIN;
  
      StencilTree func = original.find(FUNCTION);
      if (func == null) {return original;}
     
      String funcName = Freezer.multiName(func.find(OP_NAME)).name();
      if (funcName.equals(VIEW_FRAME) || funcName.equals(CANVAS_FRAME)) {
        StencilTree chain = (StencilTree) adaptor.create(CALL_CHAIN, "");
        adaptor.addChild(chain,adaptor.dupTree(func.find(FUNCTION, PACK)));
        return chain;
      } else {
        return original;
      }
   }
  
  private StencilTree stripPresentOps(StencilTree before) {
    StencilTree trimmed = before;
    do {
      before = trimmed;
      trimmed = trimCall(trimmed);
    } while (before != trimmed);
    return trimmed.getChild(0);
  }
}

add: ^(c=CALL_CHAIN call=.)
    -> {hasViewRef($c) && hasCanvasRef($c)}? 
        ^(CALL_CHAIN 
            ^(FUNCTION ^(OP_NAME DEFAULT ID[VIEW_FRAME] ID["query"]) SPECIALIZER LIST_ARGS DIRECT_YIELD[VIEW_FRAME]
                ^(FUNCTION ^(OP_NAME DEFAULT ID[CANVAS_FRAME] ID["query"]) SPECIALIZER LIST_ARGS DIRECT_YIELD[CANVAS_FRAME]
                  $call)))
    -> {hasViewRef($c)}?
        ^(CALL_CHAIN 
           ^(FUNCTION ^(OP_NAME DEFAULT ID[VIEW_FRAME] ID["query"]) SPECIALIZER LIST_ARGS DIRECT_YIELD[VIEW_FRAME]
               $call))
    
    -> {hasCanvasRef($c)}?
        ^(CALL_CHAIN 
           ^(FUNCTION ^(OP_NAME DEFAULT ID[CANVAS_FRAME] ID["query"]) SPECIALIZER LIST_ARGS DIRECT_YIELD[CANVAS_FRAME]
               $call))

    -> ^(CALL_CHAIN $call);

remove: ^(c=CALL_CHAIN .*) -> ^(CALL_CHAIN {stripPresentOps($c)});
