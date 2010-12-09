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

  public synchronzied static Tree apply (Tree t, Map<TupleRef, Value> subst) {
     return TreeRewriteSequence.apply(t, subst);
  }
  
  protected void setup(Object... args) {
     subst = (Map<TupleRef, Value>) args[0];
  }
  
}

topdown
  : t=TUPLE_REF -> {subst.containsKey($t)}? {adaptor.dupTree(subst.get($t))}
                -> {adaptor.dupTree($t)}
  ;
