tree grammar DynamicCompleteRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
  /** Modifies dynamic binding query and update rules to use the correct operator facets. **/
  package stencil.parser.string;
	
  import stencil.parser.tree.util.*;
  import stencil.parser.tree.*;
  import stencil.module.operator.StencilOperator;
  import stencil.module.operator.util.Invokeable;
  import stencil.module.util.FacetData;
  import stencil.module.util.OperatorData;
  import static stencil.parser.string.Utilities.stateQueryList;
  import static stencil.parser.ParserConstants.QUERY_FACET;
}

@members {
  public Object transform(Object t) {
    t = downup(t, this, "changeType");
    t = downup(t, this, "convert");
    t = downup(t, this, "toQuery");
    return t;
  } 

   private String queryName(String name) {return new MultiPartName(name).modSuffix(QUERY_FACET).toString();}       
}

changeType: ^(CONSUMES f=. pf=. l=. r=. v=. c=. ^(LIST toDynamic*));
toDynamic:  ^(r=RULE rest+=.*) -> ^(DYNAMIC_RULE {adaptor.dupTree($r)} {adaptor.dupTree($r)});
            
convert: ^(DYNAMIC_RULE r=. sq=.) -> ^(DYNAMIC_RULE $r {stateQueryList(adaptor, $sq)});  

toQuery 
  @after{
    AstInvokeable inv=((Function) $toQuery.tree).getTarget();
    inv.changeFacet(QUERY_FACET);
  }
  : ^(f=FUNCTION rest+=.*) 
          {$f.getAncestor(DYNAMIC_RULE) != null}? ->
          ^(FUNCTION[queryName($f.getText())]  $rest*);
