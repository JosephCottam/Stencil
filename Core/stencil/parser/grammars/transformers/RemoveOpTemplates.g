tree grammar RemoveOpTemplates;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
/**Removes all operator templates from the program.
 * 
 * Operator templates are not needed at runtime, and
 * interfere with certain optimizations, so they are 
 * deleted before the last phases of the compiler.
 */

  package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import static stencil.parser.string.Utilities.genSym;
}
                
bottomup:
    ^(PROGRAM i=. g=. s=. o=. cl=. sd=. l=. ops=. p=. t=.) 
        -> ^(PROGRAM $i $g $s $o $cl $sd $l $ops $p);
                 
