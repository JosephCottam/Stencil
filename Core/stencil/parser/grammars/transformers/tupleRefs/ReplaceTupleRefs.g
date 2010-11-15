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
  import stencil.parser.tree.Program;
  import stencil.parser.ParseStencil;
}

@members {
  public static Tree apply (Tree t, Map<TupleRef, TupleRef> subst) {
     return TreeRewriteSequence.apply(t, subst);
  }
  
  protected void setup(Object... args) {
     subst = (Map<TupleRef, TupleRef>) args[0];
  }
  
  private Map<TupleRef, TupleRef> subst;
}

topdown
  : t=TUPLE_REF -> {subst.containsKey($t)}? {adaptor.dupTree(subst.get($t))}
                -> {adaptor.dupTree($t)}
  ;
