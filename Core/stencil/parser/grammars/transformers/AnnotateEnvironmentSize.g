tree grammar AnnotateEnvironmentSize;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
}

@header{
  /** Determines the size of the environment required for a call
   *  chain and annotates the call chain to indicate that size.
   **/
   
  package stencil.parser.string;  
  import stencil.parser.tree.*;
}


topdown: ^(c=CALL_CHAIN ct=callTarget[0] .?) //Will accept a call chain with or without existing size annotation
			-> ^(CALL_CHAIN callTarget ^(NUMBER[Integer.toString($ct.depth)]));

callTarget[int d] returns [int depth]
	@init {$depth = d;}
	: ^(FUNCTION (options {greedy=false;} :.)* ct=callTarget[d+1] {$depth = $ct.depth;})
	| ^(PACK .*);
 