tree grammar Imports;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
  superClass = TreeFilterSequence;
}

@header{
	/*Perform import operations specified in the stencil program.
	 *This builds the fully populated module cache, ready to be used to instantiate
	 *methods for use in the interpreter.
	 */ 
	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import stencil.module.*;
	import org.antlr.runtime.tree.*;
	
}

@members{ 
  public static ModuleCache apply (Tree t) {
     modules = new ModuleCache();
     TreeFilterSequence.apply(t);
     return modules;
  }
  
  protected static ModuleCache modules;
}

/**Build up imports (used to decide the operation type)*/
topdown	: ^(IMPORT name=ID prefix=ID) {modules.importModule(name.getText(), prefix.getText());};
