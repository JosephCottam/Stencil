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
  import stencil.parser.tree.util.MultiPartName;
  import stencil.module.util.FacetData;
  import stencil.module.util.OperatorData;
  import static stencil.parser.ParserConstants.QUERY_FACET;
  import static stencil.parser.ParserConstants.STATE_ID_FACET;
}

@members {
 private static final TreeAdaptor DUPLICATOR = new StencilTreeAdapter();
 private static final MakeViewPoint INSTANCE = new MakeViewPoint(new CommonTreeNodeStream(null));

 private static final Map<String, StencilOperator> instances = new HashMap();

 public synchronized static Program viewPoint(Program p) {
    //this.viewPointer = new MakeViewPoint(new CommonTreeNodeStream(program));
     instances.clear();
     Program copy = (Program) DUPLICATOR.dupTree(p);
     INSTANCE.downup(copy, INSTANCE, "instantiate");
     INSTANCE.downup(copy, INSTANCE, "change");
     return copy;
 }
 
}

//instantiate new synthetic operators that point to the new tree (Instatiated operators are later used to thread values through).
instantiate 
    : proxy=OPERATOR_PROXY 
      {
         StencilOperator op =  ((OperatorProxy) proxy).getOperator();
         instances.put(op.getName(), op.viewPoint());
      }
    | ^(opDef=OPERATOR .*) 
      {
          SyntheticOperator op = new SyntheticOperator("", (Operator) opDef);
          instances.put(op.getName(), op);   
      };


//Replace AST instances with pointers to new viewpoints
//Uses the new synthetic instances when necessary
//TODO: Why does it actually make three copies of the operator???
change:  i=AST_INVOKEABLE 
{
   AstInvokeable inv = (AstInvokeable) i;
   StencilOperator op = inv.getOperator();
   if (op == null) {return;}
   
   MultiPartName name = new MultiPartName(inv.getParent().getText());
   if (!name.getFacet().equals("")) {//If this is a true facet operator AND it is a function, nothing more needs to be done.
     OperatorData od = op.getOperatorData();   
     FacetData fd = od.getFacet(name.getFacet());
     if (fd.isFunction()) {return;}
   }
   
   StencilOperator viewPoint = instances.get(op.getName());
   if (viewPoint == null) {throw new RuntimeException("Could not find viewpoint for operator " + op.getName());}
   
   inv.setOperator(viewPoint);
   
   //Probably correct where it matters...but maybe not always
   if (inv.getAncestor(STATE_QUERY) != null) {
       inv.changeFacet(STATE_ID_FACET);
   } else {
       inv.changeFacet(QUERY_FACET); 
   }
};
