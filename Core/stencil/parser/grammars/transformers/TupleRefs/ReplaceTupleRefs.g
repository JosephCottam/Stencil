tree grammar ReplaceTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Replace references according to the mapping passed.
   **/
   
  package stencil.parser.string;

  import java.util.Map;
  import stencil.parser.tree.TupleRef;
  import stencil.parser.tree.Program;
  import stencil.parser.ParseStencil;
}

@members {
  public static Program apply (Tree t, Map<TupleRef, TupleRef> subst) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass(), subst);
  }
  
  protected void setup(Object... args) {
     subst = (Map<TupleRef, TupleRef>) args[0];
  }
  
  private Map<TupleRef, TupleRef> subst;  //TODO: Can this be moved to an argument to topdown???
}

topdown
  : t=TUPLE_REF -> {subst.containsKey($t)}? {adaptor.dupTree(subst.get($t))}
                -> TUPLE_REF
  ;