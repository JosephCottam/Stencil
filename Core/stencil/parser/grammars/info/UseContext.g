tree grammar UseContext;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /**Gets the usage context of a given operator instance.**/
   

  package stencil.parser.string.info;
  
  import stencil.parser.string.TreeFilterSequence;
  import stencil.parser.string.util.Context;
  import stencil.parser.tree.util.MultiPartName;
  import stencil.parser.tree.StencilTree;
}

@members{
  private static Context inProgress;
  
  public static Context apply (Tree t, String target) {
     inProgress = new Context(target);
     TreeFilterSequence.apply(t);
     return inProgress;
  }
  
  public void downup(Object p) {
    downup(p, this, "argCount");
    downup(p, this, "highOrderArg");
  }
  

  
}

//TODO: Remove if statement when numeralize is done in return values as well
argCount
 : ^(f=FUNCTION .*)
    { MultiPartName n = new MultiPartName(f.getText());
      if (n.getName().equals(inProgress.target())) {
        inProgress.update(f.find(LIST_ARGS).getChildCount());
        }
    };
    

highOrderArg
  : opArg=OP_AS_ARG
    {MultiPartName n = new MultiPartName(opArg.getText());
      if (n.getName().equals(inProgress.target())) {
           StencilTree base = opArg.getAncestor(OPERATOR_REFERENCE);
           String type = base.find(OPERATOR_BASE).getText();
           inProgress.addHighOrderUse(type, base);
      }
    }; 
  
  