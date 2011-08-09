tree grammar GuideSetID;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Set a descriptive name for the guide.**/

  package stencil.parser.string;

  import java.util.Arrays;
  import stencil.parser.tree.StencilTree;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  private String flatten(StencilTree root) {
     StringBuilder b = new StringBuilder(root.getAncestor(LAYER).getText());
     b.append(" ");
     b.append(root.getAncestor(GUIDE).find(ID).getText());
     b.append(": ");
     for(StencilTree sel: root) {       
        b.append(sel.getText());
        b.append(", ");
     }
     b.deleteCharAt(b.length()-1);
     b.deleteCharAt(b.length()-1);
     return b.toString();
  }
}

topdown: ^(g=GUIDE rest+=.*) -> ^(GUIDE[flatten($g.find(LIST_SELECTORS))] $rest+);
