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
  private static Program copy;

  public static Program apply (Tree t) {
     TreeFilterSequence.apply(t);
     return copy;
  }

 private static final Map<String, StencilOperator> instances = new HashMap();

  public void downup(Object p) {
     instances.clear();
     copy = (Program) DUPLICATOR.dupTree(p);
     downup(copy, this, "instantiate");
     downup(copy, this, "change");
  }
}

//instantiate new synthetic operators that point to the new tree (Instantiated operators are later used to thread values through).
//Also creates viewpoints of all user-declared  operators
instantiate 
    : proxy=OPERATOR_PROXY 
      {
         StencilOperator op =  ((OperatorProxy) proxy).getOperator();
         instances.put(proxy.getText(), op.viewPoint());
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
       if (!fd.mutative()) {return;}
     } else if (name.prefixedName().equals("STATE_QUERY")) {return;}    //State queries are all handled on the original tree
       
     StencilOperator viewPoint = instances.get(name.prefixedName());
     if (viewPoint == null) {
        //TODO: Make stencil inserted operators (like Echo is for guides) appear as OPERATOR_PROXY nodes
        //This case is required because stencil inserted operators don't appear as PROXY entities anywhere
        //    This is wasteful because it makes a viewpoint for each apperance of the operator (which may be many, esp. in the guide system)
        //    This is very dangerous if the viewPoint operation takes a lot of time (doesn't for any operator automatically inserted YET).
        viewPoint = op.viewPoint();
     }
     
     inv.setOperator(viewPoint);
     
     //Probably correct where it matters...but maybe not always
     inv.changeFacet(QUERY_FACET); 
};
