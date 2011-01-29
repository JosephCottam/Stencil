tree grammar GuideDefaultSelector;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Determines the default selector attribute.*/

	package stencil.parser.string;

	import stencil.parser.tree.*;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

topdown: 
  ^(s=SELECTOR p+=ID+) 
    {s.getText().equals("DEFAULT")}? -> ^(SELECTOR[((StencilTree) $p.get($p.size()-1)).getToken()] $p+);
