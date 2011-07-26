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
  import static stencil.parser.string.util.Utilities.genSym;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
  
}

@members {  
  /**Old name to new name mapping.*/
  private static Map<String, String> subst = new HashMap();

  public static Tree apply (Tree t) {
     subst.clear();
     return (Tree) TreeRewriteSequence.apply(t);
  }
}

topdown
  : dy=DIRECT_YIELD {subst.put($dy.text, genSym(FRAME_SYM_PREFIX));} 
        -> DIRECT_YIELD[subst.get($dy.text)]
  | ^(t=TUPLE_REF frame=. rest+=.*)
        -> {subst.containsKey($frame.getText())}? ^(TUPLE_REF ID[subst.get($frame.getText())] $rest*)
        -> ^(TUPLE_REF $frame $rest*)
  ;