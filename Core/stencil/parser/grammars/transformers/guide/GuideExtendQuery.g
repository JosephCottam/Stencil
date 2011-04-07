tree grammar GuideExtendQuery;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;	
  filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Ensure the query guide includes the monitor, sample and any stateful action operations.
   * This is required for summaraization guides because the monitor operator is never in the call chain.
   * Direct guides already have their monitor included in the state query because
   * the monitor operator is integrated into the analysis chain.
   */
  
  package stencil.parser.string; 
  
  import stencil.module.operator.util.Invokeable;
  import stencil.module.operator.util.ReflectiveInvokeable;
  import stencil.parser.tree.*;
  import static stencil.module.operator.StencilOperator.STATE_ID_FACET;
}

@members {
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}
     
     
topdown: ^(LIST_GUIDE_MONITORS monitor+);
   
monitor
   @after{
     StencilTree summary = op.getAncestor(GUIDE_SUMMARIZATION);
     if (summary != null) {
       StencilTree queries = summary.findDescendant(STATE_QUERY);
        Invokeable monitorInv = new ReflectiveInvokeable(STATE_ID_FACET, ((Const) op).getValue());
        AstInvokeable monitorAInv = (AstInvokeable) adaptor.create(AST_INVOKEABLE, "monitor");
        monitorAInv.setInvokeable(monitorInv);
        adaptor.addChild(queries, monitorAInv);
     }
   }
   : ^(MONITOR_OPERATOR op=.);
     