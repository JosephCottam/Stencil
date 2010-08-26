tree grammar DynamicToSimple;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Ensures that each dynamic binding also has a simple
   * binding in the result list.  If a simple binding is 
   * explicitly provided, the dynamic binding is simply removed
   * from the simple-binding results.  If a simple binding is
   * not provided, the dynamic marker is removed.
   **/

  package stencil.parser.string;
	
  import org.antlr.runtime.tree.*;
  import stencil.parser.tree.Rule;
  import stencil.tuple.prototype.TuplePrototypes;
  import java.util.Arrays;
  import stencil.parser.tree.Program;
}

@members {
  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }
}

topdown: ^(CONSUMES . . . ^(LIST rule*) .*);
      
rule: ^(RULE t=. cc=. b=.) -> ^(RULE $t $cc DEFINE[":"]);




