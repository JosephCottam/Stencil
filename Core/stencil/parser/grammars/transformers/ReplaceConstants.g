tree grammar ReplaceConstants;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
  /**Identifies constants in operator arguments and specializers, replacing them with the literal value.
   *
   * TODO: Make more delicate when runtime constants are added.  Only replace/remove constants if they are compile-time
   **/
   package stencil.parser.string;
	
   import stencil.parser.tree.*;
   import stencil.parser.string.util.*;
   import static stencil.parser.ParserConstants.GLOBALS_FRAME;
}

@members {  
  public static StencilTree apply (StencilTree t) {
     GlobalsTuple globals = new GlobalsTuple(t.find(LIST_GLOBALS));
     return (StencilTree) TreeRewriteSequence.apply(t, globals);
  }
  
  protected void setup(Object... args) {globals = (GlobalsTuple) args[0];}
  
  private GlobalsTuple globals;
}

topdown: 
	^(TUPLE_REF frame=ID field=ID) 
		{$frame.text.equals(GLOBALS_FRAME) &&
  		  globals.getPrototype().contains($field.text)}? ->  {adaptor.dupTree(globals.get($field.text))};

//Delete the global list of constants
bottomup: ^(p=PROGRAM .*) {adaptor.deleteChild(p, p.find(LIST_GLOBALS).getChildIndex());};
