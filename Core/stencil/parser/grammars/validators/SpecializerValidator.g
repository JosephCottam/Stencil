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
  
  import stencil.parser.string.ValidationException;
  import stencil.parser.string.TreeFilterSequence;
  import stencil.module.operator.util.Range;
  import stencil.module.operator.util.Split;
  import stencil.interpreter.tree.Specializer;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.tree.StencilTree;
  import static stencil.interpreter.tree.Specializer.SPLIT;
  import static stencil.interpreter.tree.Specializer.RANGE;
}

@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown
	: ^(s=SPECIALIZER DEFAULT) {throw new ValidationException("Default specializer found after all supposedly removed.");}
	| id=ID {if ($id.getAncestor(SPECIALIZER) != null) {throw new ValidationException("Non-constant ID found in specializer: " + $id.text);}}
	| ^(s=SPECIALIZER .*) 
	     {
	       Specializer spec = Freezer.specializer(s);
	       //Check the range
	       if (spec.containsKey(RANGE)) {
	           new Range(spec.get(RANGE));  //Instantiation triggers validation
         }
         
         //Check the split
         if (spec.containsKey(SPLIT)) {
            Split split = new Split(spec.get(SPLIT));
            StencilTree f = (StencilTree) s.getAncestor(FUNCTION);
            if (f == null) {return;}

            StencilTree args = f.find(LIST_ARGS); 
            
            if (split.getFields() <0) {throw new ValidationException("Negative split size indicated.");}
            if (split.getFields() > args.getChildCount()) {throw new ValidationException("Split indicates more fields than are passed as parameters.");}
            for (int i=0; i< split.getFields(); i++) {
               if (args.getChild(i).getType() != TUPLE_REF) {throw new ValidationException("Split over non-tuple refs not permitted.");}
             }   
         }
       };
