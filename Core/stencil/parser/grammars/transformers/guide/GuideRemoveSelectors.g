tree grammar GuideRemoveSelectors;
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
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  private String flatten(StencilTree root) {
     StringBuilder b = new StringBuilder(root.getAncestor(LAYER).getText());
     for(StencilTree sel: root) {
        b.append("->");       
        b.append(sel.getText());
     }
     return b.toString();
  }
}

bottomup: ^(l=LIST_SELECTORS .*) -> SELECTOR[flatten($l)];
