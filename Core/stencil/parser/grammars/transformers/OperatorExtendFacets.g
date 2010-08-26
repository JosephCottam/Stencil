tree grammar OperatorExtendFacets;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
	superClass = TreeRewriteSequence;	
    output = AST;
	filter = true;
}

@header {
/** Ensure a map and query facet exist for each operator.
 * TODO: Extend to have data-state-query.
 */
 
  package stencil.parser.string;

  import stencil.parser.tree.util.*;
  import stencil.parser.tree.*;
  import stencil.module.operator.StencilOperator;
  import stencil.module.operator.util.Invokeable;
  import stencil.module.util.FacetData;
  import stencil.module.util.OperatorData;
  import static stencil.parser.string.util.Utilities.*;
  import static stencil.parser.ParserConstants.QUERY_FACET;
  import static stencil.parser.ParserConstants.MAP_FACET;
  import static stencil.parser.ParserConstants.STATE_ID_FACET;
}

@members {
  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }
    
  public Object downup(Object t) {
    downup(t, this, "replicate");     //Build a mapping from the layer/attribute names to mapping trees
    downup(t, this, "toQuery");
    return t;
  }
}

//Extend the operator definition to include the required facets 
replicate: ^(r=OPERATOR proto=. prefilter=. rules=.) 
	   -> ^(OPERATOR
	          ^(OPERATOR_FACET[MAP_FACET] $proto $prefilter $rules) 
	          ^(OPERATOR_FACET[QUERY_FACET] $proto $prefilter $rules)
	          STATE_QUERY);//Query is filled in later...
	          

//Properly construct the query facet
toQuery: ^(f=FUNCTION rest+=.*) 
          {$f.getAncestor(PREDICATE)  == null && $f.getAncestor(OPERATOR_FACET) != null && $f.getAncestor(OPERATOR_FACET).getText().equals("query")}? ->
          ^(FUNCTION[queryName($f.getText())]  $rest*);