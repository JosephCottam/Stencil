tree grammar OperatorStateQuery;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
  output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
/** Fill in operator state query for synthetic operators.*/
  package stencil.parser.string;

  import stencil.parser.tree.*;
  import static stencil.parser.string.util.Utilities.*;
}

@members {
  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }}

//Extend the operator definition to include the required facets 
topdown
  : ^(sq=STATE_QUERY .*) 
      {sq.getAncestor(OPERATOR) != null}? -> {stateQueryList(adaptor, $sq.getAncestor(OPERATOR))}; 
