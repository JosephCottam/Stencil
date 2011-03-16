tree grammar GuideLiftGenerator;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Removes the monitor operator from the generator chain but adds
   * its invokeable to the guide node as the monitor operator.
   */

  package stencil.parser.string;
	
  import stencil.parser.ParserConstants;
  import stencil.parser.tree.*;
  import stencil.interpreter.guide.MonitorOperator;
  import stencil.module.operator.util.ReflectiveInvokeable;
  import stencil.interpreter.guide.samplers.LayerSampler;
  import stencil.display.DisplayLayer;
  import static stencil.parser.ParserConstants.INVOKEABLE;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public StencilTree downup(Object t) {
    downup(t, this, "lift");
    downup(t, this, "repackRoot");  //Lift up and package as a guide generator
    downup(t, this, "rename");//Rename stream references to sample references
    return (StencilTree) t;  
  }
  
  /**Return true if a frame name should be replaced with a reference to the generated sample.
   *  In the simple world, this will only need to replace the references to the stream.
   *  However, with -#> sampler placement, this is more copmlicated.
   *
   *  TODO: When runtime constants are added, do not replace elements in the runtime global frame  
  */
  public boolean replace(StencilTree ref, String frame) {
     if (frame.equals(ParserConstants.GLOBALS_FRAME) 
         || frame.equals(ParserConstants.VIEW_FRAME) 
         || frame.equals(ParserConstants.CANVAS_FRAME)) {return false;}
     
     StencilTree f = ref.getAncestor(FUNCTION);
     if (f == null) {return true;}
     if (f.find(DIRECT_YIELD, GUIDE_YIELD).getText().equals(frame)) {return false;}
     return replace(f, frame);
  }
}

lift
  @after{
    StencilTree g = retval.tree.getChild(0);
    if (dmonitor != null) {
       Object op = dmonitor.find(INVOKEABLE).getOperator();
       ((Const) g.find(MONITOR_OPERATOR).find(CONST)).setValue(op);
    } else {
       String layerName = g.find(SELECTOR).getChild(0).getText();
       StencilTree layer = g.getAncestor(PROGRAM).find(LIST_LAYERS).find(LAYER, layerName);
       DisplayLayer dl = (DisplayLayer) ((Const) layer.find(CONST)).getValue();
       
       MonitorOperator op = new LayerSampler.MonitorOperator(dl);
       ((Const) g.find(MONITOR_OPERATOR).find(CONST)).setValue(op);
    }
  }

  : ^(GUIDE_DIRECT ^(GUIDE type=. spec=. selector=. actions=.
        ^(RULE target=.
           ^(CALL_CHAIN ^(dmonitor=FUNCTION i=. s=. a=. y=. c=. ))) query=.))        
     -> ^(GUIDE_DIRECT ^(GUIDE $type $spec $selector $actions ^(GUIDE_GENERATOR  ^(RULE $target ^(CALL_CHAIN $c))) $query ^(MONITOR_OPERATOR CONST)))
  | ^(GUIDE_SUMMARIZATION ^(GUIDE type=. spec=. selector=. actions=. smonitor=. query=.))
     -> ^(GUIDE_SUMMARIZATION ^(GUIDE $type $spec $selector $actions ^(GUIDE_GENERATOR $smonitor) $query ^(MONITOR_OPERATOR CONST)));
     
repackRoot
  : ^(GUIDE_GENERATOR ^(RULE retarget ^(CALL_CHAIN repack)));

//Add input to the result prototype
retarget
  : ^(RESULT ^(TUPLE_PROTOTYPE p=.+)) 
      -> ^(RESULT ^(TUPLE_PROTOTYPE $p ^(TUPLE_FIELD_DEF STRING["Input"] STRING["DEFAULT"])));

//Add sample output to the result prototpye      
repack
  : ^(FUNCTION (options {greedy=false;} :.)* repack)
  | ^(PACK f=.*) -> ^(PACK $f ^(TUPLE_REF ID[ParserConstants.STREAM_FRAME] ID["#Sample"]));

rename: ^(TUPLE_REF fr=ID fi=. r+=.*)
  {($fr.getAncestor(GUIDE_GENERATOR) != null) && replace($fr, $fr.getText())}? 
      -> ^(TUPLE_REF ID[ParserConstants.STREAM_FRAME] ID["#Sample"] $r*);
        