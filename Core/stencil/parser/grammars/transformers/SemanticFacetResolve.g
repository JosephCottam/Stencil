tree grammar SemanticFacetResolve;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
	output = AST;
	superClass = TreeRewriteSequence;
}

@header {
/** Determines the actual facet based on semantic facet annotations (e.g., "DEFAULT_FACET" or "COUNTERPART_FACET")**/
	package stencil.parser.string;
	
  import stencil.module.*;
  import stencil.module.operator.StencilOperator;
  import stencil.parser.tree.*;
  import stencil.interpreter.tree.Specializer;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.string.util.TreeRewriteSequence;
    
  import static stencil.parser.string.util.Utilities.counterpartFacet;
  import static stencil.parser.string.util.Utilities.defaultFacet;
  
}

@members { 
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}

topdown
  : ^(on=OP_NAME p=. o=. f=DEFAULT_FACET) -> ^(OP_NAME $p $o ID[defaultFacet($on)])
  | ^(on=OP_NAME p=. o=. ^(f=COUNTERPART_FACET .)) -> ^(OP_NAME $p $o ID[counterpartFacet($on)]);

