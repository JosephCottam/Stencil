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
  import stencil.parser.tree.util.Path;
  import stencil.parser.tree.StencilTree;
}

@members{
  private static final class NumeralizationException extends ValidationException {
    public NumeralizationException(Tree t) {
      super("Non-numeralized tuple ref outside of target: " + Path.toString((StencilTree) t));
    }
  }

  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

//TODO: Remove if statement when numeralize is done in return values as well
topdown
 : ^(t=TUPLE_REF .*) 
    {if (t.getAncestor(StencilParser.TARGET) == null
         &&
          (t.getFirstChildWithType(ID) != null
          || t.getFirstChildWithType(ALL) != null
          || t.getFirstChildWithType(LAST) != null)) {
        throw new NumeralizationException(t);
      }};
  
  