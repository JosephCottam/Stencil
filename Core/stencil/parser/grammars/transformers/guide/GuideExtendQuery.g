tree grammar GuideExtendQuery;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;	
  filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Ensure the guide state query includes the monitor, sample and any stateful action operations.
   * This is required for summaraization guides because the monitor operator is never in the call chain.
   * Direct guides already have their monitor included in the state query because
   * the monitor operator is integrated into the analysis chain.
   */
  
  package stencil.parser.string; 
  
  import stencil.interpreter.tree.MultiPartName;
  import stencil.module.operator.util.Invokeable;
  import stencil.module.operator.util.ReflectiveInvokeable;
  import stencil.parser.tree.*;
  import stencil.parser.string.util.TreeRewriteSequence;
  
  import static stencil.module.operator.StencilOperator.STATE_ID_FACET;
  import static stencil.parser.string.util.Utilities.unfreezeName;
}

@members {
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}
     
     
topdown: ^(LIST_GUIDE_MONITORS monitor+);
   
monitor
   @after{
     StencilTree summary = monitorName.getAncestor(GUIDE_SUMMARIZATION);
     if (summary != null) {
       StencilTree queries = summary.findDescendant(STATE_QUERY);
       MultiPartName name = new MultiPartName(null, monitorName.getText(), STATE_ID_FACET); 
       adaptor.addChild(queries, unfreezeName(name, adaptor));
     }
   }
   : monitorName=MONITOR_OPERATOR;
     