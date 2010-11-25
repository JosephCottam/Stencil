tree grammar UseContext;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /**Gets the usage context of a given operator instance.**/
   

  package stencil.parser.string.info;
  
  import stencil.parser.string.TreeFilterSequence;
  import static stencil.parser.string.StencilParser.LIST;
  import stencil.parser.string.util.Context;
  import stencil.parser.tree.util.MultiPartName;
}

@members{
  private static Context inProgress;
  private static String targetName;
  
  public static Context apply (Tree t, String target) {
     inProgress = new Context(target);
     TreeFilterSequence.apply(t);
     return inProgress;
  }

  
}

//TODO: Remove if statement when numeralize is done in return values as well
topdown
 : ^(f=FUNCTION .*)
    { MultiPartName n = new MultiPartName(f.getText());
      if (n.getName().equals(inProgress.target())) {
        inProgress.update(f.getFirstChildWithType(LIST).getChildCount());
        }
    };
  
  