tree grammar SpecializerValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
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
  import stencil.parser.ParseStencil;
  import stencil.parser.string.TreeFilterSequence;
  import stencil.module.operator.util.Range;
  import stencil.module.operator.util.Split;
  import stencil.parser.tree.Specializer;
  import stencil.parser.tree.Value;
  import stencil.parser.tree.Function;
  import static stencil.parser.tree.Specializer.SPLIT;
  import static stencil.parser.tree.Specializer.RANGE;
}

@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown
	: ^(s=SPECIALIZER DEFAULT) {throw new ValidationException("Default specializer found after all supposedly removed.");}
	| id=ID {if ($id.getAncestor(SPECIALIZER) != null) {throw new ValidationException("Non-constant ID found in specializer: " + $id.text);}}
	| ^(s=SPECIALIZER .*) 
	     {
	       Specializer spec = (Specializer) s;
	       //Check the range
	       if (spec.containsKey(RANGE)) {
	         Range r = new Range(spec.get(RANGE));  //Instantiation triggers validation
         }
         
         //Check the split
         if (spec.containsKey(SPLIT)) {
            Split split = new Split(spec.get(SPLIT));
            Function f = (Function) spec.getAncestor(FUNCTION);
            if (f == null) {return;}

            List<Value> args = f.getArguments(); 
            
            if (split.getFields() <0) {throw new ValidationException("Negative split size indicated.");}
            if (split.getFields() > args.size()) {throw new ValidationException("Split indicates more fields than are passed as parameters.");}
            for (int i=0; i< split.getFields(); i++) {
               if (args.get(i).getType() != TUPLE_REF) {throw new ValidationException("Split over non-tuple refs not permitted.");}
             }   
         }
       };
