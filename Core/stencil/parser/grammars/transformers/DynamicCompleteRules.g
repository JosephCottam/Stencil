tree grammar DynamicCompleteRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
  /** Modifies dynamic binding query and update rules to use the correct operator facets. **/
  package stencil.parser.string;
	
  import stencil.parser.tree.util.*;
  import stencil.parser.tree.*;
  import static stencil.parser.string.util.Utilities.stateQueryList;
  import static stencil.parser.ParserConstants.QUERY_FACET;
  import static stencil.parser.ParserConstants.INVOKEABLE;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public StencilTree downup(Object t) {
    downup(t, this, "changeType");
    downup(t, this, "convert");
    downup(t, this, "toQuery");
    return (StencilTree) t;
  }  
   private String queryName(String name) {return new MultiPartName(name).modSuffix(QUERY_FACET).toString();}       
}

changeType: ^(CONSUMES f=. pf=. l=. r=. v=. c=. ^(RULES_DYNAMIC toDynamic*));
toDynamic:  ^(r=RULE rest+=.*) -> ^(DYNAMIC_RULE {adaptor.dupTree($r)} {adaptor.dupTree($r)});
            
convert: ^(DYNAMIC_RULE r=. sq=.) -> ^(DYNAMIC_RULE $r {stateQueryList(adaptor, $sq)});  

toQuery 
  @after{
    AstInvokeable inv= $toQuery.tree.find(INVOKEABLE);
    inv.changeFacet(QUERY_FACET);
  }
  : ^(f=FUNCTION rest+=.*) 
          {$f.getAncestor(DYNAMIC_RULE) != null}? ->
          ^(FUNCTION[queryName($f.getText())]  $rest*);
