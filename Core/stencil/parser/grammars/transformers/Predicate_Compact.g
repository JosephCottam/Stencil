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
  import stencil.parser.string.util.TreeRewriteSequence;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public StencilTree downup(Object p) {
     StencilTree r = downup(p, this, "flatPreds");
     r = downup(r, this, "flatFilters");
     return r;
  }
}

//Remove the rule furniture from pedicates
flatPreds: ^(PREDICATE ^(RULE . ^(CALL_CHAIN ^(f=FUNCTION ^(OP_NAME . n=. .) spec=. args=. .+)))) -> ^(PREDICATE[$n.getText()] $args); 

//Remove the nesting from filter predicates (they are just anded by the interpreter anyway)
//TODO: Remove this step if OR is introduced into the filters list
flatFilters: ^(LIST_FILTERS preds+=predList*) -> ^(LIST_FILTERS $preds*);
predList: ^(LIST_PREDICATES preds+=.*) -> $preds*;

