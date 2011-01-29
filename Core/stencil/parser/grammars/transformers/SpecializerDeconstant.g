tree grammar SpecializerDeconstant;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
  /**Identifies constants in specializers and replaces them with values.*/
	package stencil.parser.string;
	
  import stencil.tuple.prototype.TuplePrototype;
	import stencil.parser.tree.StencilTree;
	import stencil.parser.string.util.*;
}

@members {  
  public static StencilTree apply (StencilTree t) {
     GlobalsTuple globals = new GlobalsTuple(t.find(LIST_GLOBALS));
     return (StencilTree) TreeRewriteSequence.apply(t, globals);
  }
  
  protected void setup(Object... args) {
    globals = (GlobalsTuple) args[0];
    prototype = globals.getPrototype();
  }
  
  private GlobalsTuple globals;
  private TuplePrototype prototype;
  
  protected Tree replaceGlobal(Tree id) {
    String name = id.getText();
    if (prototype.contains(name)) {
       return (Tree) adaptor.dupTree(globals.get(name));
    }
    
    String context =  id.getAncestor(SPECIALIZER).toStringTree();
    throw new RuntimeException("Reference to unknown global " + name + " in specializer " + context);
  }
}

topdown:
  ^(me=MAP_ENTRY c=ID) 
		-> ^(MAP_ENTRY {replaceGlobal($c)});
  