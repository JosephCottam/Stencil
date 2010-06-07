tree grammar DynamicSeparateRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
  /** Splits dynamic rules into two parts: UpdateQuery and Base. **/
  package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import static stencil.parser.string.SeparateRules.siftRules;

}

@members {
   protected StencilTree dynamicResults(CommonTree source) {return siftRules(adaptor, (List<Rule>) source, RESULT, DYNAMIC, "Dynamic");}
}

topdown: ^(CONSUMES filter=. prefilter=. local=. results=. view=. canvas=.)
   -> ^(CONSUMES $filter $prefilter $local $results $view $canvas {dynamicResults($results)});