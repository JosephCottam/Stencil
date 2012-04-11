tree grammar StreamDeclarationValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Validates that all stream declarations include the 
    * standard source-indicator field in the prototype,
    * that all field names in the declaration are unique
    * and that no field name is the same as the stream name.
   **/
   

  package stencil.parser.string.validators;
  
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.string.util.TreeFilterSequence;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.parser.string.util.ValidationException;
  import static java.lang.String.format;
  import stencil.parser.tree.StencilTree;
}

@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}

  public void uniqueFieldNames(StencilTree stream, StencilTree prototype) {
     try {
       TuplePrototype proto = Freezer.prototype(prototype);
       //TODO: Is this really an error still (now that * as a tuple ref exists)???
       if (proto.contains(stream.getText())) {
         throw new ValidationException(format("Field with same name as containing stream: \%1\$s", stream));
       }
     } catch (ValidationException v) {throw v;}
     catch (Exception e) {
       throw new ValidationException("Error validating prototype def: " + prototype.toStringTree(), e);
     }
  } 
  
}

topdown: ^(s=EXTERNAL_STREAM ^(p=TUPLE_PROTOTYPE .*)) 
         {
            uniqueFieldNames(s, p);
         };
