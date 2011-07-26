tree grammar UseContext;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /**Gets the usage context of a given operator instance.**/
   

  package stencil.parser.string;
  
  import stencil.parser.string.TreeFilterSequence;
  import stencil.parser.string.util.Context;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.interpreter.tree.Freezer;
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
 : ^(f=FUNCTION r=OP_NAME . . . .)
    { MultiPartName n = Freezer.multiName(r);
      if (n.name().equals(inProgress.target())) {
        inProgress.update(f.find(LIST_ARGS).getChildCount());
        }
    };
    

highOrderArg
  : opArg=OP_AS_ARG
    {MultiPartName n = Freezer.multiName(opArg.find(OP_NAME));
      if (n.name().equals(inProgress.target())) {
           StencilTree base = opArg.getAncestor(OPERATOR_REFERENCE);
           String type = Freezer.multiName(base.find(OPERATOR_BASE)).toString();
           inProgress.addHighOrderUse(type, base);
      }
    }; 
  
  