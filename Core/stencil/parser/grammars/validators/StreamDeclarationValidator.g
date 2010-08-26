tree grammar StreamDeclarationValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
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
  
  import stencil.parser.tree.Stream;
  import stencil.parser.string.StencilParser;
  import stencil.parser.tree.TuplePrototype;
  import stencil.parser.tree.TupleFieldDef;
  import stencil.parser.ParseStencil;
  import stencil.parser.string.TreeFilterSequence;

  import java.util.HashSet;
  import java.util.Set;

  import stencil.parser.string.ValidationException;
  import static java.lang.String.format;
  import static stencil.parser.ParserConstants.SOURCE_FIELD;
}

@members {
  public static void apply (Tree t) {
     apply(t, new Object(){}.getClass().getEnclosingClass());
  }

  public void uniqueFieldNames(Stream e, TuplePrototype prototype) {
    String field = null;
    Set<String> fields = new HashSet<String>();
    
    for (TupleFieldDef def: prototype) {
      field = def.getFieldName();
      if (!fields.add(field)) {break;}
      else {field = null;}
    }  

    String stream = e.getName();  
    if (field != null) {
      throw new ValidationException(format("Duplicate field name in stream declaration \%1\$s: \%2\$s.", stream, field));
    }
    
    if (fields.contains(stream)) {
      throw new ValidationException(format("Field with same name as containing stream: \%1\$s", stream));
    }
  }
  
}

topdown: ^(e=EXTERNAL_STREAM ^(p=TUPLE_PROTOTYPE .*)) 
         {
            uniqueFieldNames((Stream) e, (TuplePrototype) p);
         };
