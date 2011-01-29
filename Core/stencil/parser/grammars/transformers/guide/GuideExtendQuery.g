tree grammar GuideExtendQuery;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;	
  filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Ensure the query guide includes the seed, sample and any stateful action operations.*/
  
  package stencil.parser.string; 
  
  import stencil.module.operator.util.Invokeable;
  import stencil.module.operator.util.ReflectiveInvokeable;
  import stencil.parser.tree.*;
  import static stencil.module.operator.StencilOperator.STATE_ID_FACET;
}

@members {
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
}
     
     
topdown
   @after{
      StencilTree guide = q.getAncestor(GUIDE);
      StencilTree summary = q.getAncestor(GUIDE_SUMMARIZATION);
      if (summary != null) {
	      Invokeable seedInv = new ReflectiveInvokeable(STATE_ID_FACET, ((Const) guide.find(SEED_OPERATOR).getChild(0)).getValue());
	      AstInvokeable seedAInv = (AstInvokeable) adaptor.create(AST_INVOKEABLE, "seed");
	      seedAInv.setInvokeable(seedInv);
	      adaptor.addChild(q, seedAInv);
	    }
   }
   : ^(q=STATE_QUERY .*); 