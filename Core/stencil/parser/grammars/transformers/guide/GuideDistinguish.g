tree grammar GuideDistinguish;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Distinguish between summariziation and direct guides structurally
   * so future passes can use tree pattern matching.
   */

  package stencil.parser.string;

  import java.util.Arrays;
  import stencil.parser.tree.Program;
}

@members {
  //TODO: Get the list of direct types from the adaptor
  private static List<String> DIRECT_TYPES = Arrays.asList("AXIS", "SIDEBAR");

  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }
    
  private boolean isDirect(Tree t) {
    String type = t.getText().toUpperCase();
    return DIRECT_TYPES.contains(type);
  }
  
  private Tree taggedType(Tree t) {
     if (isDirect(t)) {
       return (Tree) adaptor.create(GUIDE_DIRECT, "");
     } else {
       return (Tree) adaptor.create(GUIDE_SUMMARIZATION, "");
     }
  }
}

bottomup: ^(GUIDE t=. s=. p=. r=.) -> ^({taggedType(t)} ^(GUIDE $t $s $p $r));
