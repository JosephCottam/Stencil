tree grammar FullNumeralize;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /**Verifies that all tuple-refs inside of operator calls are numeric (not names).**/
   

  package stencil.parser.string.validators;

  import stencil.parser.string.ValidationException;
  import stencil.parser.string.TreeFilterSequence;
  import stencil.parser.tree.util.Path;
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.StencilParser;
  import stencil.interpreter.tree.Freezer;
}

@members{
  private static final class NumeralizationException extends ValidationException {
    public NumeralizationException(String message) {
      super(message);
    }
    public static ValidationException incomplete(Tree t) {
      return new NumeralizationException("Non-numeralized tuple ref outside of target: " + Path.toString((StencilTree) t));
    }
    public static ValidationException bad(Tree t) {
      return new NumeralizationException("Malformed tuple ref: " + Path.toString((StencilTree) t));
    }
 }
    
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
}

//TODO: Remove if statement when numeralize is done in return values as well
topdown
 : ^(t=TUPLE_REF .*) 
    {if (t.getAncestor(StencilParser.TARGET) == null) {
      if (t.getFirstChildWithType(ID) != null
          || t.getFirstChildWithType(ALL) != null
          || t.getFirstChildWithType(LAST) != null) {throw NumeralizationException.incomplete(t);}

       for (StencilTree child: t) {
          Object c = Freezer.freeze(child);
          if (!(c instanceof Number) ||
               (((Number) c).intValue() <0)) {
              throw NumeralizationException.bad(t);
          }
       }
      }};