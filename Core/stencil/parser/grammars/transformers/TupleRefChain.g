tree grammar TupleRefChain;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header {
/* Converts the list format tuple-refs into a chain format tuple ref.
 * TODO: Eliminate this step by making chain for tuple refs from the start 
 */

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
}

topdown : ^(TUPLE_REF r=. chain)  -> ^(TUPLE_REF $r chain);
chain: (^(TUPLE_REF r=.) chain) -> ^(TUPLE_REF $r chain)
	 | ^(TUPLE_REF r=.) -> ^(TUPLE_REF $r);
	 
	 
	 
	 