tree grammar OperatorAlign;
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
    
  import stencil.parser.tree.*;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.parser.string.util.ValidationException;
  
}

@members{
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  private int[] permute;
  
  public StencilTree reorder(StencilTree targetTree) {
     String operatorName =  targetTree.getAncestor(OPERATOR).getText();
     StencilTree facet = targetTree.getAncestor(OPERATOR_FACET);
     TuplePrototype result = Freezer.prototype(facet.find(YIELDS).getChild(1));
     TuplePrototype target = Freezer.targetTuple(targetTree.find(TARGET_TUPLE)).asPrototype();
     permute = new int[result.size()];
     
     if (target.size() != result.size()) {
         String message = String.format("Operator \%1\$s has incorrect number of results in some rule.", operatorName);
         throw new ValidationException(message);
     }
     
     for (int i=0; i< result.size(); i++) {
        int at = result.indexOf(target.get(i).name());
        if (at <0) {throw new ValidationException(String.format("Operation \%1\$s includes result for \%2\$s not in the prototype.", operatorName, target.get(i).name()));}
        permute[i] = at;
     }
     
     StencilTree tt = shuffle(permute, targetTree.find(TARGET_TUPLE));
     StencilTree t  = (StencilTree) adaptor.create(TARGET, "");
     adaptor.addChild(t,tt);
     return  t;
  }

  public StencilTree reorder(StencilTree target, StencilTree chain) {
     chain =(StencilTree) adaptor.dupTree(chain);
     StencilTree parent = chain.findDescendant(PACK).getParent();
     StencilTree pack = shuffle(permute, parent.findDescendant(PACK));
     adaptor.setChild(parent, parent.findDescendant(PACK).getChildIndex(), pack);
     return chain; 
  }
  
  
  
  /**Shuffles the children of source the thing at index i is pulled from source[permute[i]].**/
  private StencilTree shuffle(int[] permute, StencilTree source) {
      StencilTree root = (StencilTree) adaptor.dupNode(source);
      for (int i=0; i<permute.length; i++) {
         adaptor.addChild(root, adaptor.dupTree(source.getChild(permute[i])));
      }
      return root;
  } 

  
}

topdown:  ^(RULE t=. cc=. d=.) 
	{$t.getAncestor(RULES_OPERATOR)!=null}? ->  ^(RULE {reorder($t)} {reorder($t, $cc)} $d);
                 
