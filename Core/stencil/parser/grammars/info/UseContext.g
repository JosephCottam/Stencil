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
  
  import stencil.parser.ProgramCompileException;
  import stencil.parser.string.util.TreeFilterSequence;
  import stencil.parser.string.util.Context;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.tree.StencilTree;
}

@members{
  private static Context inProgress;
  
  /**@param t Tree to search in 
   * @param target Operator name to search for
   **/
  public static Context apply (Tree t, String target) {
     inProgress = new Context(target);
     TreeFilterSequence.apply(t);
     return inProgress;
  }
  
  public void downup(Object p) {
    downup(p, this, "args");
  }
  

  
}

args
 : ^(f=FUNCTION r=OP_NAME . . . .)
    { MultiPartName n = Freezer.multiName(r);
      if (n.name().equals(inProgress.target())) {
        StencilTree args = f.find(LIST_ARGS);
        inProgress = inProgress.maxArgCount(args.getChildCount());
        inProgress = inProgress.args(args);
        inProgress = inProgress.addCallSite(f);
      }
    }
 | ^(a=OP_AS_ARG ref=.)
    {MultiPartName n = Freezer.multiName(ref);
      if (n.name().equals(inProgress.target())) {
          inProgress = inProgress.addCallSite(a);
      }
    };
    

  