tree grammar SpecializerValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /**Verify that specializers are correct:
    *  (1) No default  tags remain
    *  (2) TODO: Map argument labels all match a an argument key pattern from the meta-data.
    *
    * TODO: Implement #2 above...
  **/
  
  package stencil.parser.string.validators;
  
  import stencil.parser.string.util.ValidationException;
  import stencil.parser.string.util.TreeFilterSequence;
  import stencil.interpreter.tree.Specializer;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.tree.StencilTree;
}

@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown
	: ^(s=SPECIALIZER DEFAULT) {throw new ValidationException("Default specializer found after all supposedly removed:" +  $s.getParent().toStringTree());}
	| id=ID {if ($id.getAncestor(SPECIALIZER) != null) {throw new ValidationException("Non-constant ID found in specializer: " + $id.text);}}
	| ^(s=SPECIALIZER .*) 
	     {
	       try {Freezer.specializer(s);}
  	     catch (Exception e) {throw new ValidationException("Error validating specializer by freezing.", e);}
       };
