tree grammar OperatorPrefilter;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Verify that the target in the operator prefilter block is always prefilter.**/
   

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.ValidationException;
  import stencil.parser.string.StencilParser;
  import stencil.parser.ParseStencil;
  import stencil.parser.string.TreeFilterSequence;
}

@members {
  private static final class TargetPackMismatchException extends ValidationException {
      public TargetPackMismatchException() {super("");}
  }  
  
  public static void apply (Tree t) {
     apply(t, new Object(){}.getClass().getEnclosingClass());
  }
  
}

topdown: ^(OPERATOR . ^(LIST prefilter) .*);
prefilter
	: (RULE PREFILTER) => ^(RULE PREFILTER .*) 
	| v=. {throw new RuntimeException("Non-prefilter in prefilter block for operator " + v.getAncestor(StencilParser.OPERATOR).getText());}; 


  