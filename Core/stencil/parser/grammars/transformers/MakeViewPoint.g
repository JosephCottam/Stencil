tree grammar MakeViewPoint;
options {
   tokenVocab = Stencil;
   ASTLabelType = CommonTree;	
   filter = true;
   superClass = TreeFilterSequence;
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
  import stencil.module.operator.wrappers.SyntheticOperator;
}

@members {
 private static final TreeAdaptor DUPLICATOR = new StencilTreeAdapter();
 private static final MakeViewPoint INSTANCE = new MakeViewPoint(new CommonTreeNodeStream(null));

 private static final Map<String, SyntheticOperator> synthetics = new HashMap();

 public synchronized static Program viewPoint(Program p) {
    //this.viewPointer = new MakeViewPoint(new CommonTreeNodeStream(program));
     synthetics.clear();
     Program copy = (Program) DUPLICATOR.dupTree(p);
     INSTANCE.downup(copy, INSTANCE, "instantiate");
     INSTANCE.downup(copy, INSTANCE, "change");
     return copy;
 }
 
}

//instantiate new synethetic operators that pointer here (they don't need to be updated yet since they run off the actual AST).
instantiate: ^(opDef=OPERATOR .*) 
{
    SyntheticOperator op = new SyntheticOperator("", (Operator) opDef);
    synthetics.put(op.getName(), op);   
};


//Replace AST instances with pointers to new viewpoints
//Uses the new synthetic instances when necessary
change: i=AST_INVOKEABLE 
{
   AstInvokeable inv = (AstInvokeable) i;
   StencilOperator op = inv.getOperator();
   if (op == null) {return;}
   StencilOperator viewPoint;
   if (synthetics.containsKey(op.getName())) {
      viewPoint = synthetics.get(op.getName());
   } else {
      viewPoint = op.viewPoint();
   }
   
   inv.setOperator(viewPoint);
   inv.changeFacet("query"); //Probably correct where it matters...but maybe not always
};
	