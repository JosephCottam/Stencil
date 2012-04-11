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
   import stencil.module.operator.StencilOperator;
   import stencil.parser.string.util.*;
   import stencil.parser.string.util.TreeRewriteSequence;
   import static stencil.parser.ParserConstants.GLOBALS_FRAME;
   import static stencil.parser.string.util.Utilities.findOperator;
}

@members {  
  public static StencilTree apply (StencilTree t) {
     GlobalsTuple globals = new GlobalsTuple(t.find(LIST_GLOBALS));
     return (StencilTree) TreeRewriteSequence.apply(t, globals);
  }
  
  
  public Object downup(Object p) {
    p=downup(p, this, "globalsDown", "globalsUp");     
    p=downup(p, this, "opAsArg");  //Move the needed ones to the guide definitions
    return p;
  }
  
  protected void setup(Object... args) {globals = (GlobalsTuple) args[0];}
  
  private GlobalsTuple globals;
}

globalsDown
   : ^(TUPLE_REF frame=ID field=ID) 
		{$frame.text.equals(GLOBALS_FRAME) &&
  		  globals.prototype().contains($field.text)}? ->  {adaptor.dupTree(globals.get($field.text))};

//Delete the global list of constants
globalsUp: ^(p=PROGRAM .*) {adaptor.deleteChild(p, p.find(LIST_GLOBALS).getChildIndex());};


//TODO: Should this resolve to the invokeable level instead?
opAsArg
@after {
   Const c = (Const) retval.tree;
   StencilOperator op = findOperator($on);
   c.setValue(op);
   //c.setValue(op.getFacet($facet.getText()));
}
  : ^(OP_AS_ARG ^(on=OP_NAME pre=. name=. facet=.)) -> CONST[on.getToken(), name.getText()];     
