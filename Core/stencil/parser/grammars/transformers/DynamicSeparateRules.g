tree grammar DynamicSeparateRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Splits dynamic rules into two parts: UpdateQuery and Base. **/
  package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import static stencil.parser.string.SeparateRules.siftRules;

}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}

  protected StencilTree dynamicResults(CommonTree source) {return siftRules(adaptor, (List<Rule>) source, RESULT, DYNAMIC, "Dynamic");}
}

topdown: ^(CONSUMES filter=. prefilter=. local=. results=. view=. canvas=.)
   -> ^(CONSUMES $filter $prefilter $local $results $view $canvas {dynamicResults($results)});