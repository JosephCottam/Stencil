tree grammar ReplaceConstants;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
  /**Identifies constants in operator arguments and specializers, replacing them with the literal value.
   **/
	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import stencil.parser.string.util.*;
	import stencil.tuple.Tuple;
  import static stencil.parser.ParserConstants.GLOBALS_FRAME;
}

@members {  
  public static Program apply (Tree t) {
     GlobalsTuple globals = new GlobalsTuple(((Program) t).getGlobals());
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass(), globals);
  }
  
  protected void setup(Object... args) {globals = (GlobalsTuple) args[0];}
  
  private GlobalsTuple globals;
}

topdown: 
	^(TUPLE_REF frame=ID ^(TUPLE_REF field=ID)) 
		{$frame.text.equals(GLOBALS_FRAME) &&
  		  globals.getPrototype().contains($field.text)}? ->  {adaptor.dupTree(globals.get($field.text))};
  		  
bottomup: ^(LIST ^(CONST .*)) -> ^(LIST);  		  