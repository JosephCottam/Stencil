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
	
  import stencil.parser.ParserConstants;
  import stencil.parser.tree.*;
  import stencil.interpreter.guide.SeedOperator;
  import stencil.module.operator.util.ReflectiveInvokeable;
  import stencil.interpreter.guide.samplers.LayerSampler;
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
  
  public Program downup(Object t) {
    downup(t, this, "lift");
    downup(t, this, "repackRoot");  //Lift up and package as a guide generator
    downup(t, this, "rename");//Rename stream references to sample references
    return (Program) t;  
  }
  
  /**Return true if a frame name should be replaced with a reference to the generated sample.
   *  In the simple world, this will only need to replace the references to the stream.
   *  However, with -#> sampler placement, this is more copmlicated.
   *
   *  TODO: Have the replacement strategy throw an exception if it is replacing from more than one frame.
   *  TODO: When runtime constants are added, do not replace elements in the runtime global frame  
  */
  public boolean retain(CommonTree ref, String frame) {
     if (frame.equals(ParserConstants.VIEW_FRAME) || frame.equals(ParserConstants.CANVAS_FRAME)) {return true;}
     
     Function f = (Function) ref.getAncestor(FUNCTION);
     if (f == null) {return false;}
     if (f.getPass().getText().equals(frame)) {return true;}
     return retain(f, frame);
  }
}

lift
  @after{
    Guide g = (Guide) retval.tree.getChild(0);
    if (seed != null) {
       g.setSeedOperator(((Function) seed).getTarget().getInvokeable());
    } else {
       String layerName = g.getSelector().getPath().get(0).getID();
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
     
repackRoot
  : ^(GUIDE_GENERATOR ^(RULE retarget ^(CALL_CHAIN repack)));

//Add input to the result prototype
retarget
  : ^(RESULT ^(TUPLE_PROTOTYPE p=.+)) 
      -> ^(RESULT ^(TUPLE_PROTOTYPE $p ^(TUPLE_FIELD_DEF STRING["Input"] STRING["DEFAULT"])));

//Add sample output to the result prototpye      
repack
  : ^(FUNCTION (options {greedy=false;} :.)* repack)
  | ^(PACK f=.*) -> ^(PACK $f ^(TUPLE_REF ID["stream"] ID["#Sample"]));

rename: ^(TUPLE_REF fr=ID fi=ID r=.*)
  {($fr.getAncestor(GUIDE_GENERATOR) != null) && !retain($fr, $fr.getText())}? 
      -> ^(TUPLE_REF ID["stream"] ID["#Sample"] $r*);
        