tree grammar LimitDynamicBind;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
/* Verifies that dynamic bind is only in layer defs.*/ 
  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.ValidationException;
  import stencil.parser.string.TreeFilterSequence;
}

@members {
  private static final class InvalidDynamicBindException extends ValidationException {
  	public InvalidDynamicBindException(String name) {
  		super("Invalid dynamic binding in \%1\$s.", name);
  	}
  }  

  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

topdown: d=DYNAMIC_BIND 
      {if (d.getAncestor(LAYER) == null) {
          StencilTree t = d.getAncestor(STREAM);
          String name;
          
          if (t==null) {t = d.getAncestor(VIEW);}
          if (t==null) {t = d.getAncestor(CANVAS);}
          
          if (t==null) {name = "unknown context.";}
          else {name = t.getText();}
      
          throw new InvalidDynamicBindException(name);}
      };  