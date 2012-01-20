tree grammar OperatorExtendFacets;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
	superClass = TreeRewriteSequence;	
    output = AST;
	filter = true;
}

@header {
/** Ensure a default and counterpart facet exist for synthetic operators.*/
 
  package stencil.parser.string;

  import stencil.parser.tree.StencilTree;  
  import stencil.module.operator.wrappers.SyntheticOperator;
  import static stencil.parser.string.util.Utilities.*;
}

@members {  
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
    
  public Object downup(Object t) {
    downup(t, this, "replicate");     //Build a mapping from the layer/attribute names to mapping trees
    downup(t, this, "toCounterpart");
    return t;
  }
}

//Extend the operator definition to include the required facets 
replicate: ^(r=OPERATOR proto=. prefilter=. rules=.) 
	   -> ^(OPERATOR
	          ^(OPERATOR_FACET[SyntheticOperator.DEFAULT_FACET] $proto $prefilter $rules) 
	          ^(OPERATOR_FACET[SyntheticOperator.COUNTERPART_FACET] $proto $prefilter $rules)
	          STATE_QUERY);//Query is filled in later...
	          

//Properly construct the counterpart facet
toCounterpart: ^(f=FUNCTION ^(opName=OP_NAME pre=. base=. facet=.) rest+=.*) 
          {$f.getAncestor(PREDICATE)  == null                                   //No predicate specified 
            && $f.getAncestor(OPERATOR_FACET) != null                           //Facet is specified
            && $f.getAncestor(OPERATOR_FACET).getText().equals(SyntheticOperator.COUNTERPART_FACET)}? ->    //And the facet group being transformed is a query THEN...
          ^(FUNCTION ^(OP_NAME $pre $base COUNTERPART_FACET[facet.getText()])  $rest*);