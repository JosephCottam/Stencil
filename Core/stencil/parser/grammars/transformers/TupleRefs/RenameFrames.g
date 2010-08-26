tree grammar RenameFrames;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Rename all frames in the passed tree.  Updates references as well.*/   
  package stencil.parser.string;

  import java.util.Map;
  import java.util.HashMap;
  import stencil.parser.tree.TupleRef;
  import stencil.parser.tree.Program;
  import stencil.parser.ParseStencil;
  import static stencil.parser.ParseStencil.TREE_ADAPTOR;
  import static stencil.parser.string.util.Utilities.genSym;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
  
}

@members {  
  /**Old name to new name mapping.*/
  private static Map<String, String> subst = new HashMap();
  public static Program apply (Tree t) {
     subst.clear();
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }
}

topdown
  : dy=DIRECT_YIELD {String newName = genSym(FRAME_SYM_PREFIX); subst.put($dy.text, newName);} -> DIRECT_YIELD[subst.get($dy.text)]
  | t=TUPLE_REF -> {subst.containsKey($t.text) && t.getParent().getType() != TUPLE_REF}? TUPLE_REF[subst.get($t)]
                -> TUPLE_REF
  ;