tree grammar Predicate_Compact;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
	filter = true;
	output = AST;
  superClass = TreeRewriteSequence;
}

@header {
 /**Optimizes filter rules to be simple invokeables.  
  * Must be done after operator instantiation.
  */

  package stencil.parser.string;
  
  import stencil.parser.tree.StencilTree;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public Object downup(Object p) {
    downup(p, this, "flatPreds");
    downup(p, this, "flatFilters");
    return p;
  }
}

//Remove the rule furniture from pedicates
flatPreds: ^(PREDICATE ^(RULE . ^(CALL_CHAIN ^(f=FUNCTION inv=. spec=. args=. .+)))) -> ^(PREDICATE[$f.text] $inv $args); 

//Remove the nesting from filter predicates (they are just anded by the interpreter anyway)
//TODO: Remove this step if OR is introduced into the filters list
flatFilters: ^(LIST_FILTERS preds+=predList*) -> ^(LIST_FILTERS $preds*);
predList: ^(LIST_PREDICATES preds+=.*) -> $preds*;

