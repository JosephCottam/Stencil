tree grammar TupleRefChain;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
/* Converts the list format tuple-refs into a chain format tuple ref.
 * TODO: Eliminate this step by making chain for tuple refs from the start 
 */

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import stencil.parser.ParseStencil;
}

@members {
    public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
}

topdown : ^(TUPLE_REF r=. chain)  -> ^(TUPLE_REF $r chain);
chain: (^(TUPLE_REF r=.) chain) -> ^(TUPLE_REF $r chain)
	 | ^(TUPLE_REF r=.) -> ^(TUPLE_REF $r);
	 
	 
	 
	 