tree grammar FullNumeralize;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /**Verifies that all tuple-refs inside of operator calls are numeric (not names).**/
   

  package stencil.parser.string.validators;

  import stencil.parser.string.StencilParser;
  import stencil.parser.string.ValidationException;
  import stencil.parser.ParseStencil;
  import stencil.parser.string.TreeFilterSequence;
}

@members{
  private static final class NumeralizationException extends ValidationException {
    public NumeralizationException(Tree t) {
      super(message(t));
    }
    
    private static String message(Tree t) {
        Tree ancestor = t.getAncestor(FUNCTION);
        if (ancestor != null) {
          return "Non-numeralized tuple ref outside of target: " + t.toStringTree() + " in " + ancestor.toStringTree();
        } else {
          return "Non-numeralized tuple ref outside of target: " + t.toStringTree();
        }
    }
  }

  public static void apply (Tree t) {
     apply(t, new Object(){}.getClass().getEnclosingClass());
  }
}

//TODO: Remove if statement when numeralize is done in return values as well
topdown
 : ^(t=TUPLE_REF ID .*) 
    {if (t.getAncestor(StencilParser.FUNCTION) != null
          || t.getAncestor(StencilParser.PACK) != null
          || t.getAncestor(StencilParser.PREDICATE) != null) {
        throw new NumeralizationException(t);
      }};
  
  