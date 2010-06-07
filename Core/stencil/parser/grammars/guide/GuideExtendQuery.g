tree grammar GuideExtendQuery;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;	
  filter = true;
  output = AST;	
}

@header {
  /**Ensure the query guide includes the seed, sample and any stateful action operations.*/
  
  package stencil.parser.string; 
  
  import stencil.module.operator.StencilOperator;
  import stencil.module.operator.util.Invokeable;
  import stencil.module.operator.util.ReflectiveInvokeable;
  import stencil.parser.tree.*;
  import static stencil.module.operator.StencilOperator.STATE_ID_FACET;
}

topdown:
  ^(g=GUIDE type=. spec=. selector=. actions=. gen=. query[(Guide) g])
     -> ^(GUIDE $type $spec $selector $actions $gen query);
     
     
query[Guide g]
   @after{
      Invokeable seedInv = new ReflectiveInvokeable(STATE_ID_FACET, g.getSeedOperator());
      AstInvokeable seedAInv = (AstInvokeable) adaptor.create(AST_INVOKEABLE, "seed");
      seedAInv.setInvokeable(seedInv);
      adaptor.addChild(gq, seedAInv);
   }
   : ^(gq=STATE_QUERY .*); 