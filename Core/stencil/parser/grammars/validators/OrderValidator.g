tree grammar OrderValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Checks that all entries in the Order statement are part of the stream declarations.**/
   

  package stencil.parser.string.validators;
  
  import stencil.parser.string.TreeFilterSequence;
  import stencil.parser.string.ValidationException;
  import stencil.parser.tree.StencilTree;
}

@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}

  public void validStreamNames(StencilTree order) {
  	StencilTree program = order.getAncestor(PROGRAM);
  	StencilTree streams = program.find(LIST_STREAM_DECLS);
  	
  	for (StencilTree streamRef: order.findAllDescendants(ID)) {
  	    String name = streamRef.getText();
  	    if (!find(name, streams)) {throw new ValidationException("No stream definition for '" + name + "' found in order statement.");}
  	}
  }
  
  private static boolean find(String ref, StencilTree defs) {
      for (StencilTree def: defs) {
         if (def.getText().equals(ref)) {return true;}
      }
      return false;
  }
}

topdown: ^(o=ORDER .*) {validStreamNames(o);};