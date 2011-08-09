tree grammar GuideDistinguish;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
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
  import stencil.parser.tree.StencilTree;
}

@members {
  //TODO: Get the list of direct types from the adaptor
  public static List<String> DIRECT_TYPES = Arrays.asList("axis", "legend","gridlines");

  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
    
  private boolean isDirect(Tree t) {return DIRECT_TYPES.contains(t.getText());}
  
  private Tree taggedType(Tree t) {
     if (isDirect(t)) {
       return (Tree) adaptor.create(GUIDE_DIRECT, "");
     } else {
       return (Tree) adaptor.create(GUIDE_SUMMARIZATION, "");
     }
  }
}

bottomup: ^(GUIDE t=. s=. p=. r=.) -> ^({taggedType(t)} ^(GUIDE $t $s $p $r));
