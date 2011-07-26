tree grammar NumeralizeTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Converts all tuple references to numeric references.
   **/
   
  package stencil.parser.string;
  
  import stencil.parser.tree.*;
  import stencil.module.*;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.parser.string.util.EnvironmentUtil;
  import stencil.parser.string.util.GlobalsTuple;
  import stencil.util.collections.ArrayUtil;
  import stencil.interpreter.tree.Freezer;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.module.util.OperatorData;

  import static stencil.interpreter.Environment.*;
}

@members {
  private static final class NumeralizationException extends RuntimeException {
      public NumeralizationException(StencilTree node) {
         super("Error numeralizing " + node.toStringTree() + " at " + node.getToken().getLine());
      }
  }

  public static StencilTree apply (StencilTree t) {
     return (StencilTree) TreeRewriteSequence.apply(t);
  }
    
  //What will the frame offset be at runtime?
  private int frameNumber(StencilTree frameRef) {
      if (frameRef.getType() == NUMBER) {return Integer.parseInt(frameRef.getText());}
      
      StencilTree func = frameRef.getAncestor(FUNCTION);
      while (func != null) {
          StencilTree yield = func.find(DIRECT_YIELD);
          if (yield.getText().equals(frameRef.getText())) {
            return DEFAULT_SIZE+EnvironmentUtil.countPriorFuncs(yield);
          } else {
              func = func.getAncestor(FUNCTION);
          }
          
      }
      int idx = ArrayUtil.indexOf(frameRef.getText(), DEFAULT_FRAME_NAMES);
      if (idx <0) {throw new NumeralizationException(frameRef);}
      return idx;
  }

  private int fieldNumber(StencilTree oldRef, int frame, StencilTree ref) {
     if (ref.getType() == NUMBER) {return Integer.parseInt(ref.getText());}
     
     TuplePrototype proto = EnvironmentUtil.framePrototypeFor(oldRef, frame);
     int idx = proto.indexOf(ref.getText());
     if (idx <0) {throw new NumeralizationException(oldRef);}
     return idx;
  }  
  
  public StencilTree numeralize(StencilTree oldRef) {
      StencilTree newRef = (StencilTree) adaptor.create(TUPLE_REF, "TUPLE_REF");
      
      //Numeralize the frame
      int frameNum = frameNumber(oldRef.getChild(0));
      adaptor.addChild(newRef, (StencilTree) adaptor.create(NUMBER, Integer.toString(frameNum))); 

      if (oldRef.getChildCount() ==1) {return newRef;} //Stop if we have a frame-ref

      //Numeralize the first value ref      
      int valNum = fieldNumber(oldRef, frameNum, oldRef.getChild(1));
      adaptor.addChild(newRef, (StencilTree) adaptor.create(NUMBER, Integer.toString(valNum)));
      
      //Copy other refs blindly...hope they are all NUMBER
      //TODO: Numeralize sub-refs... (and fold prior value-ref numeralize in; requires typing of values to get sub-tuple types
      for (int i=2; i<oldRef.getChildCount(); i++) {
         StencilTree ref = oldRef.getChild(i);
         adaptor.addChild(newRef, adaptor.dupTree(ref));
      }
      return newRef;
  }
  
}

topdown: ^(r=TUPLE_REF .+) -> {numeralize($r)};