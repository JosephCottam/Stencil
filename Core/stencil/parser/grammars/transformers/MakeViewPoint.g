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
 private static final TreeAdaptor DUPLICATOR = new StencilTreeAdapter();
 private static final MakeViewPoint INSTANCE = new MakeViewPoint(new CommonTreeNodeStream());

 //TODO: Take care of re-instantiating synthetic operators....somehow 
 public static Program viewPoint(Program p) {
    //this.viewPointer = new MakeViewPoint(new CommonTreeNodeStream(program));
     Program copy = (Program) DUPLICATOR.dupTree(p);
     INSTANCE.downup(copy);     
     return copy;
 }

}

topdown: i=AST_INVOKEABLE 
	{AstInvokeable inv = (AstInvokeable) i;
	 StencilOperator op = inv.getOperator();
	 if (op == null) {return;}
	 StencilOperator viewPoint = op.viewPoint();
	 inv.setOperator(viewPoint);
	 inv.changeFacet("query"); //HACK: Probably correct where it matters...but not always!
	};
	





