tree grammar OperatorStateQuery;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
  output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
/** Fill in operator state query for synthetic operators.*/
  package stencil.parser.string;

  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.TreeRewriteSequence;
  import static stencil.parser.string.util.Utilities.*;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

//Extend the operator definition to include the state query facet
topdown
  : ^(sq=STATE_QUERY .*) 
      {sq.getAncestor(OPERATOR) != null}? -> {stateQueryList(adaptor, $sq.getAncestor(OPERATOR))}; 
