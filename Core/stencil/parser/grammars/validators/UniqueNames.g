tree grammar UniqueNames;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /**Verifies that layers, operators and streams are uniquely named (even against each other).*/

  package stencil.parser.string.validators;
  
  import java.util.HashSet;
  import java.util.Set;
  
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.ValidationException;
  import stencil.parser.string.util.TreeFilterSequence;
}


@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}

  private Set<String> names;
  protected void setup(Object... args) {names = new HashSet();}
  
  private void checkName(String name) {
    if (names.contains(name)) {throw new ValidationException("Duplicate name: " + name);}
    names.add(name);
  }
}


topdown
  : (^(n=LAYER .*) 
     | ^(n=OPERATOR_REFERENCE .*)
     | ^(n=OPERATOR .*)
     | ^(n=STREAM_DEF .*)
     | ^(n=STREAM .*)) {checkName($n.text);};
