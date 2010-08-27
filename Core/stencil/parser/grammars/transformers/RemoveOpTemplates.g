tree grammar RemoveOpTemplates;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
/**Removes all operator templates from the program.
 * 
 * Operator templates are not needed at runtime, and
 * interfere with certain optimizations, so they are 
 * deleted before the last phases of the compiler.
 */

  package stencil.parser.string;
  
  import stencil.parser.tree.Program;
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
}
                
bottomup:
    ^(PROGRAM i=. g=. s=. o=. cl=. sd=. l=. ops=. p=. t=.) 
        -> ^(PROGRAM $i $g $s $o $cl $sd $l $ops $p);
                 
