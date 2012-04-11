tree grammar DynamicToSimple;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
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
  import stencil.parser.tree.*;
  import stencil.parser.string.util.TreeRewriteSequence;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

topdown: ^(CONSUMES f=. p=. l=. ^(RESULTS_RULES rule*) .*);
      
rule: ^(RULE t=. cc=. b=.) -> ^(RULE $t $cc DEFINE[":"]);




