tree grammar Imports;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
  superClass = TreeFilterSequence;
}

@header{
	/*Perform import operations specified in the stencil program.
	 *This builds the fully populated module cache, ready to be used to instantiate
	 *methods for use in the interpreter.
	 */ 
	package stencil.parser.string;
	
	import java.util.Map;
	import java.util.HashMap;
	import stencil.parser.tree.*;
	import stencil.module.*;
	import stencil.module.util.*;
	import org.antlr.runtime.tree.*;
	
}

@members{ 
  public static ModuleCache apply (Tree t) {
     modules = new ModuleCache();
     apply(t, new Object(){}.getClass().getEnclosingClass());
     return modules;
  }
  
  protected static ModuleCache modules;
	
	public void doImport(String name, String prefix, CommonTree spec) {
		//TODO: handle arg list on import (currently just ignored)
		try {modules.importModule(name, prefix);}
		catch (Exception e) {throw new RuntimeException(String.format("Error importing \%1\$s (with prefix '\%2\$s').", name, prefix), e);} 
	}	
}

/**Build up imports (used to decide the operation type)*/
topdown	: ^(name=IMPORT prefix=ID spec=.) {doImport($name.getText(), $prefix.getText(), spec);};
