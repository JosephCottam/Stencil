tree grammar OperatorStateQuery;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
  output = AST;
	filter = true;
}

@header {
/** Fill in operator state query for synthetic operators.*/
  package stencil.parser.string;

  import stencil.parser.tree.*;
  import static stencil.parser.string.Utilities.*;
}

//Extend the operator definition to include the required facets 
topdown
  : ^(sq=STATE_QUERY .*) 
      {sq.getAncestor(OPERATOR) != null}? -> {stateQueryList(adaptor, $sq.getAncestor(OPERATOR))}; 
