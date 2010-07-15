tree grammar ReplaceConstants;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
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
  private GlobalsTuple globals;
  
  public ReplaceConstants(TreeNodeStream input, Program p) {
     super(input, new RecognizerSharedState());
     assert p != null : "Must supply a valid program object.";
     globals = new GlobalsTuple(p.getGlobals());
  }
}

topdown: 
	^(TUPLE_REF frame=ID ^(TUPLE_REF field=ID)) 
		{$frame.text.equals(GLOBALS_FRAME) &&
  		  globals.getPrototype().contains($field.text)}? ->  {adaptor.dupTree(globals.get($field.text))};
  		  
bottomup: ^(LIST ^(CONST .*)) -> ^(LIST);  		  