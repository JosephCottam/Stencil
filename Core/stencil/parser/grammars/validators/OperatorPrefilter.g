tree grammar OperatorPrefilter;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Verify that the target in the operator prefilter block is always prefilter.**/
   

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.ValidationException;
  import stencil.parser.ParseStencil;
  import stencil.parser.string.TreeFilterSequence;
}

@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown: ^(OPERATOR . ^(LIST_PREFILTERS prefilter) .*);
prefilter
	: (RULE PREFILTER) => ^(RULE PREFILTER .*) 
	| v=. {throw new RuntimeException("Non-prefilter in prefilter block for operator " + v.getAncestor(OPERATOR).getText());}; 


  