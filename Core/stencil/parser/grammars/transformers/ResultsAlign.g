tree grammar ResultsAlign;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree; 
    output = AST;
    filter = true;
  superClass = TreeRewriteSequence;
}

@header{
/**Makes synthetic operator results aligned to the tuple declaration.
 *  Therefore, the results of the synthetic operator are guaranteed to 
 *  (1) all of the declared fields (exception is thrown otherwise) and
 *  (2) have them in the order declared.
 *  
 *  This pass must be run AFTER rules are combined.
 *  TODO: Update to handle multi-part names properly
 */

  package stencil.parser.string;
  
  import java.util.Arrays;
    
  import stencil.parser.tree.*;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.parser.string.util.ValidationException;
  
}

@members{
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  
  public Object downup(Object p) {
    p=downup(p, this, "streams"); 
    p=downup(p, this, "operators");     
    return p;
  }
  
  
  //Instance var to ease re-write, gets created in reorderTarget and used in both reorderTarget and reorderPack
  private int[] permute;
  
  public StencilTree reorderOp(StencilTree targetTree) {
     String operatorName =  targetTree.getAncestor(OPERATOR).getText();
     StencilTree facet = targetTree.getAncestor(OPERATOR_FACET);
     TuplePrototype result = Freezer.prototype(facet.find(YIELDS).getChild(1));
     return reorderTarget("Operator", operatorName, result, targetTree); 

  }
  
  
  public StencilTree reorderStream(StencilTree targetTree) {
     StencilTree streamDef = targetTree.getAncestor(STREAM_DEF);
     TuplePrototype result = Freezer.prototype(streamDef.find(TUPLE_PROTOTYPE));
     return reorderTarget("Stream", streamDef.getText(), result, targetTree); 
  }

  public StencilTree reorderTarget(String type, String label, TuplePrototype result, StencilTree targetTree) {
     TuplePrototype target = Freezer.targetTuple(targetTree.find(TARGET_TUPLE)).asPrototype();
     permute = new int[result.size()];
     
     if (target.size() != result.size()) {
         String message = String.format("\%1\$s \%2\$s has incorrect number of results in some rule.", type, label);
         throw new ValidationException(message);
     }
     
     for (int i=0; i< result.size(); i++) {
        int at = result.indexOf(target.get(i).name());
        if (at <0) {throw new ValidationException(String.format("\%1\$s \%2\$s includes result for \%3\$s not in the prototype.",  type, label, target.get(i).name()));}
        permute[i] = at;
     }
     
     StencilTree tt = shuffle(permute, targetTree.find(TARGET_TUPLE));
     StencilTree t  = (StencilTree) adaptor.create(TARGET, "");
     adaptor.addChild(t,tt);
     return  t;
  
  }

  public StencilTree reorderPack(StencilTree target, StencilTree chain) {
     chain =(StencilTree) adaptor.dupTree(chain);
     StencilTree parent = chain.findDescendant(PACK).getParent();
     StencilTree pack = shuffle(permute, parent.findDescendant(PACK));
     adaptor.setChild(parent, parent.findDescendant(PACK).getChildIndex(), pack);
     return chain; 
  }
  
  
  
  /**Shuffles the children of source the thing at index i is pulled from source[permute[i]].**/
  private StencilTree shuffle(int[] permute, StencilTree source) {
  	  Object[] children = new Object[permute.length];
  	  
      for (int i=0; i<permute.length; i++) {
         children[permute[i]] = adaptor.dupTree(source.getChild(i));
      }
      StencilTree root = (StencilTree) adaptor.dupNode(source);
      root.addChildren(Arrays.asList(children));
      return root;
  } 
}

streams: ^(RULE t=. cc=. d=.) {$t.getAncestor(STREAM_DEF) != null && $t.getAncestor(RULES_RESULT) != null}?  ->  ^(RULE {reorderStream($t)} {reorderPack($t, $cc)} $d);
operators: ^(RULE t=. cc=. d=.)  {$t.getAncestor(RULES_OPERATOR)!=null}? ->  ^(RULE {reorderOp($t)} {reorderPack($t, $cc)} $d);
