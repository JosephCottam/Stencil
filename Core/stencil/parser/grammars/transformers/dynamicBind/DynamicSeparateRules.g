tree grammar DynamicSeparateRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
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
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

topdown: ^(c=CONSUMES rest+=.*)
   -> ^(CONSUMES $rest* {siftRules(adaptor, $c.find(RULES_RESULT), TARGET, RULES_DYNAMIC, DYNAMIC)});