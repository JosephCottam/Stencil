tree grammar MakeViewPoint;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
}

@header{
  /** Given a tree, returns a viewpoint of the tree.
   *  A viewpoint is hte same as the root tree EXCEPT all
   *  operators are populated with viewpoints instead of 
   *  fully fledged operator instances.
   **/

  package stencil.parser.string;

  import stencil.parser.tree.*;
  import stencil.module.operator.StencilOperator;
}

@members {
  public Program transform(Program p, TreeAdaptor adaptor) {
     Program copy = (Program) adaptor.dupTree(p);
     this.downup(copy);     
     return copy;
  }
}

topdown: i=AST_INVOKEABLE 
	{AstInvokeable inv = (AstInvokeable) i;
	 StencilOperator op = inv.getOperator();
	 if (op == null) {return;}
	 StencilOperator viewPoint = op.getViewPoint();
	 inv.setOperator(viewPoint);
	 inv.changeFacet("query"); //HACK:  Usually right where it matters...but not always!
	};




