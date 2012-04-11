tree grammar LayerAlign;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree; 
    output = AST;
    filter = true;
  superClass = TreeRewriteSequence;
}

@header{
/**Ensures that layer stores have ID in the first position.
 * Must be run after rules are combined.
 */

  package stencil.parser.string;
    
  import stencil.parser.tree.*;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.string.util.TreeRewriteSequence;
  import static stencil.parser.ParserConstants.IDENTIFIER_FIELD;
  
}

@members{
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  private int moveToZero;
  
  public StencilTree reorder(StencilTree targetTree) {
     TuplePrototype target = Freezer.targetTuple(targetTree.find(TARGET_TUPLE)).asPrototype();
     moveToZero = -1;
          
     for (int i=0; i< target.size(); i++) {
        if (target.get(i).name().equals(IDENTIFIER_FIELD)) {
           moveToZero = i;
        }
     }
     
     StencilTree tt = shuffle(moveToZero, targetTree.find(TARGET_TUPLE));
     StencilTree t  = (StencilTree) adaptor.create(TARGET, "");
     adaptor.addChild(t,tt);
     return  t;
  }

  public StencilTree reorder(StencilTree target, StencilTree chain) {
     chain =(StencilTree) adaptor.dupTree(chain);
     StencilTree parent = chain.findDescendant(PACK).getParent();
     StencilTree pack = shuffle(moveToZero, parent.findDescendant(PACK));
     adaptor.setChild(parent, parent.findDescendant(PACK).getChildIndex(), pack);
     return chain; 
  }
  
  
  
  /**Shuffles the children of source the thing at index i is pulled from source[permute[i]].**/
  private StencilTree shuffle(int moveToZero, StencilTree source) {
      if (moveToZero == -1) {throw new IllegalArgumentException("Attempting to transform layer WITHOUT an ID field set.");}
      StencilTree root = (StencilTree) adaptor.dupNode(source);
      adaptor.addChild(root, source.getChild(moveToZero));
      
      for (int i=0; i<source.getChildCount(); i++) {
         if (i != moveToZero) {
            adaptor.addChild(root, adaptor.dupTree(source.getChild(i)));
         }
      }
      return root;
  } 	
}

topdown:  ^(RULE t=. cc=. d=.) 
		{$t.getAncestor(RULES_RESULT)!=null && $t.getAncestor(LAYER)!=null}? ->  ^(RULE {reorder($t)} {reorder($t, $cc)} $d);
                 
