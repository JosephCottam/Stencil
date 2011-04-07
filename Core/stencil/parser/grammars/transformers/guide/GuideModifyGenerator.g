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
  import static stencil.parser.ParserConstants.INVOKEABLE;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public StencilTree downup(Object t) {
    downup(t, this, "duplicateGens"); //Direct guide transformation
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
   *  However, with -#> sampler placement, this is more copmlicated.
   *
   *  TODO: When runtime constants are added, do not replace elements in the runtime global frame  
  */
  public static boolean replace(StencilTree ref, String frame) {
     if (frame.equals(ParserConstants.GLOBALS_FRAME) 
         || frame.equals(ParserConstants.VIEW_FRAME) 
         || frame.equals(ParserConstants.CANVAS_FRAME)) {return false;}
     
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
  
  private static String index(StencilTree t) {
     int idx = t.getAncestor(GUIDE_GENERATOR).getChildIndex();
     String index =(idx==0 ? "" : Integer.toString(idx));  
     return index;
  }
}


duplicateGens
  : ^(GUIDE type=. spec=. selector=. actions=. ^(LIST_GUIDE_GENERATORS gens+=.+) query=.)
  {type.getAncestor(GUIDE_DIRECT) != null}? 
     -> ^(GUIDE $type $spec $selector $actions ^(LIST_GUIDE_GENERATORS $gens+) $query ^(LIST_GUIDE_MONITORS $gens+));


//If it multiple generators based in the same source, tag it for combining later in the modification process
//Must be run bottom up or it will cause an infinite loop
tagCompoundGuides: ^(g=GUIDE_DIRECT rest+=.*) {isCompoundGuide($g)}? -> ^(COMPOUND_GUIDE ^(GUIDE_DIRECT $rest*));


//If it has multiple generators based on different sources, change it to a cross legend
//Must be run bottom up or this rule causes an infinite loop
identifyCrosses: ^(g=GUIDE_DIRECT ^(GUIDE type=. rest+=.*)) {isCrossGuide($g)}? ->  ^(GUIDE_DIRECT ^(GUIDE ID["crossLegend"] $rest*));


//Strip away all of the extra stuff from the generators
simplifyGens
  : ^(LIST_GUIDE_GENERATORS simplifyGen+);

simplifyGen
  : ^(RULE target=. ^(CALL_CHAIN ^(FUNCTION i=. s=. a=. y=. c=. )))
    {target.getAncestor(GUIDE_DIRECT) != null}?
      -> ^(GUIDE_GENERATOR ^(RULE $target ^(CALL_CHAIN $c)));


//Strip away extra stuff from the monitors
simplifyMons
  : ^(LIST_GUIDE_MONITORS simplifyMon+);
  
simplifyMon
  @after {
     AstInvokeable inv = (AstInvokeable) r.findAllDescendants(AST_INVOKEABLE).get(0);
     Const c = ((Const) retval.tree.find(CONST));
     c.setValue(inv.getOperator());
  }
  : ^(r=RULE .*) -> ^(MONITOR_OPERATOR CONST);

setSummaryMon
  @after{
    StencilTree g = retval.tree.getChild(0);
    String layerName = g.getAncestor(LAYER).getText();
    StencilTree layer = g.getAncestor(PROGRAM).find(LIST_LAYERS).find(LAYER, layerName);
    DisplayLayer dl = (DisplayLayer) ((Const) layer.find(CONST)).getValue();
    
    MonitorOperator op = new LayerSampler.MonitorOperator(dl);
    ((Const) g.findDescendant(MONITOR_OPERATOR).find(CONST)).setValue(op);
  }
  : ^(GUIDE_SUMMARIZATION ^(GUIDE type=. spec=. selector=. actions=. smonitor=. query=.))
     -> ^(GUIDE_SUMMARIZATION 
          ^(GUIDE $type $spec $selector $actions 
            ^(LIST_GUIDE_GENERATORS ^(GUIDE_GENERATOR $smonitor))
            $query 
            ^(LIST_GUIDE_MONITORS ^(MONITOR_OPERATOR CONST))));


     
//Creates one generator and one monitor for compound (not cross) guides
//TODO: remove the monitor from layer rule as well (not required, but would remove analysis runtime work that will be ignored anyway)
combineCompoundGuides:
  ^(COMPOUND_GUIDE ^(GUIDE_DIRECT ^(GUIDE type=. spec=. selectors=. actions=. gens=. query=. mons=.)))
   -> ^(GUIDE_DIRECT ^(GUIDE $type $spec $selectors $actions {combine($gens)} $query ^(LIST_GUIDE_MONITORS {adaptor.dupTree($mons.getChild(0))})));     
     
     
//Combine changes the the target type to TARGET, so it matches
sameTargets
  : ^(r=RESULT rs+=.*) {$r.getAncestor(GUIDE_DIRECT) != null}? -> ^(TARGET $rs*);



//Make sure the input appears in the result of the generator -------------
repackGeneratorRule
  : ^(GUIDE_GENERATOR ^(RULE retarget ^(CALL_CHAIN repack) .?)) -> ^(GUIDE_GENERATOR ^(RULE retarget ^(CALL_CHAIN repack)));

retarget
  : ^(t=TARGET ^(TUPLE_PROTOTYPE p+=.+))
      -> ^(TARGET ^(TUPLE_PROTOTYPE $p* ^(TUPLE_FIELD_DEF STRING["Input" + index($t)] STRING["DEFAULT"])));

//Add sample output to the result prototpye      
repack
  : ^(FUNCTION (options {greedy=false;} :.)* repack)
  | ^(PACK f+=.*) -> ^(PACK $f* ^(TUPLE_REF ID[ParserConstants.STREAM_FRAME] ID["#Sample"]));

//Have the generator use the sample frame where it used to use the stream frame--------------------
rename: ^(TUPLE_REF fr=ID fi=. r+=.*)
  {($fr.getAncestor(GUIDE_GENERATOR) != null) && replace($fr, $fr.getText())}? 
      -> ^(TUPLE_REF ID[ParserConstants.STREAM_FRAME] ID["#Sample"] $r*);
        