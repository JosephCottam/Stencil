tree grammar CompletePrototypeTypes;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
   /**Convert Stencil type name shorthand to fully-qualified (java) type names. **/

   package stencil.parser.string;
	
   import stencil.parser.tree.*;
   import stencil.parser.string.util.TreeRewriteSequence;
   import static stencil.parser.ParserConstants.SYSTEM_STREAM_TYPE;
}

@members {
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
   //TODO: Move this to the converter and bring in info from the registered converters/type-registry.  Add short-hand names to type registry to help.
   public static Class getType(String rootName) throws ClassNotFoundException {
     Class type = null;
     String typeName = rootName; 
     //Standard associations
     if (rootName.equalsIgnoreCase("default")) {type = Object.class;}
     else if (rootName.equalsIgnoreCase("int")) {type = Integer.class;} //Primitives don't work with Class.getClass(...), so the boxed type is used instead.
     else if (rootName.equalsIgnoreCase("bool")) {type = Boolean.class;}
     else if (rootName.equalsIgnoreCase("boolean")) {type = Boolean.class;}
     else if (rootName.equalsIgnoreCase("double")) {type = Double.class;}
     else if (rootName.equalsIgnoreCase("long")) {type = Long.class;}
     else if (rootName.equalsIgnoreCase("float")) {type = Float.class;}
     else if (rootName.equalsIgnoreCase("color")) {type = java.awt.Color.class;}
     else if (rootName.equalsIgnoreCase("font")) {type = java.awt.Font.class;}
     else if (rootName.equalsIgnoreCase("PatternTuple")) {type = stencil.types.pattern.PatternTuple.class;}
     else if (rootName.equalsIgnoreCase("DateTuple")) {type = stencil.types.date.DateTuple.class;}
     else if (rootName.equalsIgnoreCase("Tuple")) {type = stencil.tuple.Tuple.class;}
     
     if (type == null) {
       try {type = Class.forName(typeName);}
       catch (ClassNotFoundException e) {
         typeName = "java.lang." + rootName;
         try {type = Class.forName(typeName);}
         catch (ClassNotFoundException ex) {
            typeName = "java.util." + rootName;
            type = Class.forName(typeName);
         }
       }
     }
     return type;
   }
  
   public static String completeType(String typeString) {
      try {return getType(typeString).getCanonicalName();}
      catch (Exception e) {throw new RuntimeException(e);}
   }

}

topdown: ^(TUPLE_FIELD_DEF name=. type=.) -> ^(TUPLE_FIELD_DEF $name TYPE[completeType($type.getText())]);
