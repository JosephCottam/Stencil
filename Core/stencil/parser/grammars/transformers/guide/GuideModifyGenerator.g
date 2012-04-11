tree grammar GuideModifyGenerator;
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
  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.module.operator.wrappers.LayerOperator;
  import stencil.parser.string.util.Utilities;
  import stencil.module.operator.StencilOperator;
  
  import static stencil.parser.ParserConstants.INPUT_FIELD;
  import static stencil.parser.ParserConstants.GLOBALS_FRAME;
  import static stencil.parser.ParserConstants.VIEW_FRAME;
  import static stencil.parser.ParserConstants.CANVAS_FRAME;
  import static stencil.parser.ParserConstants.STREAM_FRAME;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public StencilTree downup(Object t) {
    downup(t, this, "duplicateGens"); 
    downup(t, this, "topdown", "tagCompoundGuides");
    downup(t, this, "topdown", "identifyCrosses");
    downup(t, this, "simplifyGens");
    downup(t, this, "simplifyMons");
    downup(t, this, "combineCompoundGuides");
    downup(t, this, "sameTargets");
    downup(t, this, "setSummaryMon"); //Summary guide transformation
    downup(t, this, "repackGeneratorRule"); 
    downup(t, this, "rename");//Rename stream references to sample references
    return (StencilTree) t;  
  }
  
  /**Return true if a frame name should be replaced with a reference to the generated sample.
   *  In the simple world, this will only need to replace the references to the stream.
   *  However, with -#> sampler placement, this is more complicated.
   *
   *  TODO: When runtime constants are added, do not replace elements in the runtime global frame  
  */
  public static boolean replace(StencilTree ref, String frame) {
     if (frame.equals(GLOBALS_FRAME) 
         || frame.equals(VIEW_FRAME) 
         || frame.equals(CANVAS_FRAME)) {return false;}
     
     StencilTree f = ref.getAncestor(FUNCTION);
     if (f == null) {return true;}
     if (f.find(DIRECT_YIELD, GUIDE_YIELD).getText().equals(frame)) {return false;}
     return replace(f, frame);
  }
  
  
  /**Are all the monitors monitoring the same field? 
   ** TODO: only works for named de-referencing
   **/
  private static boolean sameName(StencilTree mons) {
      String field=null;
      for (StencilTree mon: mons) {
          String newField = mon.find(CALL_CHAIN).find(FUNCTION).find(LIST_ARGS).find(TUPLE_REF).getChild(1).getText();
          if (field == null) {field=newField; continue;}
          if (!newField.equals(field)) {return false;}
      }
      return true;
  }
  
  //TODO: Only works for named field dereferences...also consider numeric field dereferences 
  private static boolean isCompoundGuide(StencilTree directGuide) {
      StencilTree mons = directGuide.findDescendant(LIST_GUIDE_MONITORS);
      if (mons.getChildCount() <2) {return false;}
      return sameName(mons);
  }
  
  
   //TODO: Only works for named field dereferences...also consider numeric field dereferences 
   private static boolean isCrossGuide(StencilTree directGuide) {
       StencilTree mons = directGuide.findDescendant(LIST_GUIDE_MONITORS);
       if (mons.getChildCount() <2) {return false;}
       return !sameName(mons);
   }
  

  
  public StencilTree combine(StencilTree gens) {
      StencilTree rules = (StencilTree) adaptor.create(LIST_RULES, "Temp Rules");
      for (StencilTree t: gens) {adaptor.addChild(rules, adaptor.dupTree(t.find(RULE)));}
      rules = CombineRules.apply(rules);                          //Apply CombineRules
      StencilTree list = (StencilTree) adaptor.create(LIST_GUIDE_GENERATORS, "LIST_GUIDE_GENERATORS");
      StencilTree gen = (StencilTree)  adaptor.create(GUIDE_GENERATOR, "GUIDE_GENERATOR");
      adaptor.addChild(list, gen);
      adaptor.addChild(gen, rules.find(RULE));
      return list;
  }


  /**Add a layer monitor operator to the operators list. 
   **/
  public String addLayerMonitor(StencilTree layerNode) {
    String layerName = layerNode.getText();
    LayerOperator layerOp = (LayerOperator) Utilities.findOperator(layerNode, layerName);
    DisplayLayer dl = layerOp.layer();
    StencilOperator op = new LayerSampler.MonitorOperator(dl);
    
    String opName = Utilities.genSym(op.getName());
	StencilTree opList = Utilities.operatorsList(layerNode);		
	Utilities.addToOperators(ParserConstants.STAND_IN_GROUP, opName, op, opList, adaptor, layerNode.token);
	return opName;
  }
}


duplicateGens
  : ^(GUIDE type=. spec=. selector=. actions=. ^(LIST_GUIDE_GENERATORS gens+=.+) query=.)
  {type.getAncestor(GUIDE_DIRECT) != null}? 
     -> ^(GUIDE $type $spec $selector $actions ^(LIST_GUIDE_GENERATORS $gens+) $query ^(LIST_GUIDE_MONITORS $gens+));


//Multiple generators are used for compound- and cross- guides.
//These need to be tagged so the generators can be combined later
//These passes must be run bottom up or it will cause an infinite loop
tagCompoundGuides: ^(g=GUIDE_DIRECT rest+=.*) {isCompoundGuide($g)}? -> ^(COMPOUND_GUIDE ^(GUIDE_DIRECT $rest*));
identifyCrosses: ^(g=GUIDE_DIRECT ^(GUIDE type=. rest+=.*)) {isCrossGuide($g)}? ->  ^(GUIDE_DIRECT ^(GUIDE ID["crossLegend"] $rest*));


//Strip away all of the extra stuff from the generators
simplifyGens
  : ^(LIST_GUIDE_GENERATORS simplifyGen+);

simplifyGen
  : ^(RULE target=. ^(CALL_CHAIN ^(FUNCTION name=. spec=. args=. y=. c=.)))
    {target.getAncestor(GUIDE_DIRECT) != null}?
      -> ^(GUIDE_GENERATOR ^(RULE $target ^(CALL_CHAIN $c)));



//Strip away extra stuff from the monitors
simplifyMons
  : ^(LIST_GUIDE_MONITORS simplifyMon+);
  
simplifyMon
  : ^(r=RULE target=. ^(CALL_CHAIN ^(FUNCTION ^(OP_NAME pre=. name=. facet=.) .*))) -> ^(MONITOR_OPERATOR[$r.getToken(), $name.getText()]);

setSummaryMon
  : ^(g=GUIDE_SUMMARIZATION ^(GUIDE type=. spec=. selector=. actions=. smonitor=. query=.))
     -> ^(GUIDE_SUMMARIZATION 
          ^(GUIDE $type $spec $selector $actions 
            ^(LIST_GUIDE_GENERATORS ^(GUIDE_GENERATOR $smonitor))
            $query 
            ^(LIST_GUIDE_MONITORS MONITOR_OPERATOR[addLayerMonitor($g.getAncestor(LAYER))])));


     
//Creates one generator and one monitor for compound (not cross) guides
//TODO: remove the monitor from layer rule as well (not required, but would remove analysis runtime work that will be ignored anyway)
combineCompoundGuides:
  ^(COMPOUND_GUIDE ^(GUIDE_DIRECT ^(GUIDE type=. spec=. selectors=. actions=. gens=. query=. mons=.)))
   -> ^(GUIDE_DIRECT ^(GUIDE $type $spec $selectors $actions {combine($gens)} $query ^(LIST_GUIDE_MONITORS {adaptor.dupTree($mons.getChild(0))})));     
     
     
//Combine changes the the target type to TARGET, so it matches
sameTargets
  : ^(r=TARGET rs+=.*) {$r.getAncestor(GUIDE_DIRECT) != null}? -> ^(TARGET $rs*);



//Make sure the input appears in the result of the generator -------------
repackGeneratorRule
  : ^(GUIDE_GENERATOR ^(RULE retarget ^(CALL_CHAIN repack)));

retarget
  : ^(t=TARGET ^(TARGET_TUPLE p+=.+))
      -> ^(TARGET ^(TARGET_TUPLE $p* ^(TUPLE_FIELD ID[INPUT_FIELD])));

//Add sample output to the result prototpye      
repack
  : ^(FUNCTION n=. s=. a=. y=. repack)
  | ^(PACK f+=.*) -> ^(PACK $f* ^(TUPLE_REF ID[STREAM_FRAME]));

//Have the generator use the sample frame where it used to use the stream frame--------------------
rename 
   : ^(tr=TUPLE_REF fr=ID fi=. r+=.*)
      {($fr.getAncestor(GUIDE_GENERATOR) != null) && replace($fr, $fr.getText())}? 
        -> ^(TUPLE_REF ID[STREAM_FRAME] NUMBER[Integer.toString($tr.getChildIndex())] $r*)
   | ^(tr=TUPLE_REF fr=ID)
      {($fr.getAncestor(GUIDE_GENERATOR) != null) && replace($fr, $fr.getText()) && !$fr.getText().equals(STREAM_FRAME)}? 
        -> ^(TUPLE_REF ID[STREAM_FRAME] NUMBER[Integer.toString($tr.getChildIndex())]);