tree grammar ReplaceTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Replace references according to the mapping passed.**/
   
  package stencil.parser.string;

  import java.util.Map;
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.TreeRewriteSequence;
}

@members {
  private static Map<StencilTree, StencilTree> subst;

  public synchronized static StencilTree apply (StencilTree t, Map<StencilTree, StencilTree> subst) {
     return TreeRewriteSequence.apply(t, subst);
  }
  
  protected void setup(Object... args) {
     subst = (Map<StencilTree, StencilTree>) args[0];
  }
  
  private StencilTree substitute(StencilTree t) {
     StencilTree ref = (StencilTree) adaptor.dupTree(t);
     while (ref.getChildCount() >0) {
        if (subst.containsKey(ref)) {
            StencilTree r = (StencilTree) adaptor.dupTree(subst.get(ref));  //Get the base substitution
            for (int i=r.getChildCount(); i<t.getChildCount(); i++) { //Add the rest of the original reference back on
               adaptor.addChild(r, adaptor.dupTree(t.getChild(i)));
            }
            return r;                                                 //Return the compound
        } else {
            adaptor.deleteChild(ref, ref.getChildCount()-1);          //Reduce the size of the part being considered
        }
     }
     return (StencilTree) adaptor.dupTree(t);
  }
}

topdown: ^(t=TUPLE_REF .*) -> {substitute(t)};
