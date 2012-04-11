tree grammar RulesProvided;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Ensures that all rules contexts have rules in them.
      This is a precondition for full compilation but, for interactivity reasons, lack of rules is not a syntax error
  **/
  package stencil.parser.string.validators;
  
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.ValidationException;
  import stencil.parser.string.util.TreeFilterSequence;
  import java.util.ArrayList;
  import java.util.HashSet;
  import java.util.Set;
}

@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}


  public void buildError(StencilTree emptyList) {
      StencilTree layer = emptyList.getAncestor(LAYER);
      if (layer != null) {
      		  throw new ValidationException("Layer " + layer.getText() + " has no rules.");
	  }
	  throw new ValidationException("Consumes without rules.");
  }
}

topdown: ^(CONSUMES filters=. lr=LIST_RULES) {if (lr.getChildCount()==0) {buildError($lr);}}
		| ^(OPERATOR_RULE predicate=. lr=LIST_RULES) {if (lr.getChildCount()==0) {throw new ValidationException("Operator without rules.");}}
		| lc=LIST_CONSUMES {if (lc.getAncestor(LAYER) != null && lc.getChildCount()==0) {throw new ValidationException("Layer without any consumes blocks.");}};