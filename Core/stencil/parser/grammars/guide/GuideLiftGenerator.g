tree grammar GuideLiftGenerator;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Removes the seed operator from the generator chain but adds
   * its invokeable to the guide node as the seed operator.
   */

  package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import stencil.interpreter.guide.SeedOperator;
  import stencil.module.operator.util.ReflectiveInvokeable;
  import stencil.interpreter.guide.samplers.LayerSampler;
}

topdown 
  @after{
    Guide g = (Guide) retval.tree.getChild(0);
    if (seed != null) {
       g.setSeedOperator(((Function) seed).getTarget().getInvokeable());
    } else {
       String layerName = g.getSelector().getPath().get(0).getName();
       Layer layer = ((Program) g.getAncestor(PROGRAM)).getLayer(layerName);
       SeedOperator op = new LayerSampler.SeedOperator(layer);
       g.setSeedOperator(new ReflectiveInvokeable("getSeed", op));
    }
  }

  : ^(GUIDE_DIRECT ^(GUIDE type=. spec=. selector=. actions=.
        ^(RULE target=.
           ^(CALL_CHAIN ^(seed=FUNCTION i=. s=. a=. y=. c=. ))) query=.))        
     -> ^(GUIDE_DIRECT ^(GUIDE $type $spec $selector $actions ^(GUIDE_GENERATOR  ^(RULE $target ^(CALL_CHAIN $c))) $query))
  | ^(GUIDE_SUMMARIZATION ^(GUIDE type=. spec=. selector=. actions=. seeder=. query=.))
     -> ^(GUIDE_SUMMARIZATION ^(GUIDE $type $spec $selector $actions ^(GUIDE_GENERATOR $seeder) $query));
     
bottomup
  : ^(GUIDE_GENERATOR ^(RULE retarget ^(CALL_CHAIN repack)));

retarget
  : ^(RESULT ^(TUPLE_PROTOTYPE p=.+)) 
      -> ^(RESULT ^(TUPLE_PROTOTYPE $p ^(TUPLE_FIELD_DEF STRING["Input"] STRING["DEFAULT"])));
      
repack
  : ^(FUNCTION (options {greedy=false;} :.)* repack)
  | ^(PACK f=.*) -> ^(PACK $f ^(TUPLE_REF ID["stream"] ^(TUPLE_REF NUMBER["0"])));
  //HACK: Only works if there is only one input
      