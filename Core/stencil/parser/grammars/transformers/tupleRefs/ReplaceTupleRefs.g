tree grammar ReplaceTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Replace references according to the mapping passed.**/
   
  package stencil.parser.string;

  import java.util.Map;
  import stencil.parser.tree.TupleRef;
  import stencil.parser.tree.Value;
  import stencil.parser.tree.Program;
  import stencil.parser.ParseStencil;
}

@members {
  private static Map<TupleRef, Value> subst;

  public synchronized static Tree apply (Tree t, Map<TupleRef, Value> subst) {
     return TreeRewriteSequence.apply(t, subst);
  }
  
  protected void setup(Object... args) {
     subst = (Map<TupleRef, Value>) args[0];
  }
  
  private TupleRef substitute(TupleRef t) {
     TupleRef ref = (TupleRef) adaptor.dupTree(t);
     while (ref.getChildCount() >0) {
        if (subst.containsKey(ref)) {
            TupleRef r = (TupleRef) adaptor.dupTree(subst.get(ref));  //Get the base substitution
            for (int i=r.getChildCount(); i<t.getChildCount(); i++) { //Add the rest of the original reference back on
               adaptor.addChild(r, adaptor.dupTree(t.getChild(i)));
            }
            return r;                                                 //Return the compound
        } else {
            adaptor.deleteChild(ref, ref.getChildCount()-1);          //Reduce the size of the part being considered
        }
     }
     return (TupleRef) adaptor.dupTree(t);
  }
}

topdown: ^(t=TUPLE_REF .*) -> {substitute((TupleRef) t)};
