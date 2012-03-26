tree grammar GuideDefaultSelector;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Determines the default selector attribute.
   * Relies on summarization/direct types being distinguished.
   **/

	package stencil.parser.string;

	import stencil.parser.tree.*;
	import static stencil.parser.ParserConstants.IDENTIFIER_FIELD;
	import static stencil.parser.string.GuideDistinguish.DIRECT_TYPES;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public static Object typeNotCovered(String type) {throw new ValidationException("Must supply an attribute selector for guides of type: " + type);}

  //TODO: HACK, this is tied to the available guide types...which is determined by the adaptor...
  public static boolean isValidCombo(String type, String att) {
    if (type.equals("axis") || type.equals("gridlines") || type.equals("density")) {return att.startsWith("X") || att.startsWith("Y");}
    if (GuideDistinguish.DIRECT_TYPES.contains(type) && att.equals(IDENTIFIER_FIELD)) {return false;}
    return !att.equals("X") && !att.equals("Y");
  }
}

topdown
  : ^(SELECTOR s=DEFAULT) 
    -> {$s.getAncestor(GUIDE_SUMMARIZATION) != null}? ^(SELECTOR SAMPLE_TYPE["Layer"])
    -> ^(SELECTOR SAMPLE_TYPE["Flex"]);

bottomup
  : ^(s=SELECTOR SAMPLE_TYPE) 
    {if (!isValidCombo($s.getAncestor(GUIDE).find(ID).getText(), $s.getText())) {
       throw new ValidationException(String.format("Invalid guide requested: type \%1$s for attribute \%2$s", $s.getAncestor(GUIDE).find(ID).getText(), $s.getText()));
    }};