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
	
  import stencil.util.MultiPartName;
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
    t = changeType(t);
    t = convertAll(t);
    t = toQuery(t);
    return t;
  } 
  
   private Object changeType(Object t) {
     return downup(t, this, "changeType");
   }

   private Object convertAll(Object t) {
     return downup(t, this, "convert");
   }
   private Object toQuery(Object t) {
     return downup(t, this, "toQuery");
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

