tree grammar GuideInsertMonitorOp;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
	superClass = TreeRewriteSequence;
}

@header{
  /** Ensures that a sample operator exists in the call chain.
   *  Sample operators are inserted where a #-> appears,
   *  after the last project operator in a chain or 
   *  at the start of the chain (in that order of precedence).
   *
   *  TODO: Extend so to can handle more than the first field in a mapping definition
   **/
	 
   package stencil.parser.string;
	    
   import stencil.parser.tree.util.*;
   import stencil.module.*;
   import stencil.parser.tree.*;
   import stencil.parser.ParseStencil;
   import stencil.interpreter.tree.Freezer;
   import stencil.interpreter.tree.Specializer;
   import stencil.interpreter.guide.Samplers;
   
   import static stencil.parser.ParserConstants.BIND_OPERATOR;
   import static stencil.parser.ParserConstants.MAP_FACET;
   import static stencil.interpreter.guide.Samplers.Monitor;
   import static stencil.interpreter.guide.Samplers.SAMPLE_KEY;
   
   import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
   import static stencil.parser.string.util.Utilities.genSym;
}

@members {
  public static StencilTree apply (Tree t, ModuleCache modules) {
     return (StencilTree) TreeRewriteSequence.apply(t, modules);
  }
    
  protected void setup(Object... args) {this.modules = (ModuleCache) args[0];}
  
  public Object downup(Object t) {
    downup(t, this, "listRequirements");
    downup(t, this, "replaceCompactForm");    /**Replace the auto-categorize operator.**/
    downup(t, this, "ensure");  /**Make sure that things which need guides have minimum necessary operators.*/    
    return t;
  }

  private static final String MONITOR_PREFIX = "monitor.";
  private static final String SEED_PREFIX = "seed.";

	protected ModuleCache modules;

  //Mapping from guide descriptors to the guide request
  //The keys are layer/attribute pairs
  protected HashMap<String, StencilTree> requestedGuides = new HashMap();
    
    /**Given a tree, how should it be looked up in the guides map?*/
    private String key(StencilTree tree) {
      if (tree.getType() == SELECTOR) {
          return key(tree.getAncestor(LAYER).getText(), tree.getText());
      } else if (tree.getType() == PACK || tree.getType() == FUNCTION) {
          return key(tree.getAncestor(RULE));
      } else if (tree.getType() == RULE) {
		      Tree layer = tree.getAncestor(LAYER);
		      Tree attRef = tree.find(TARGET, RESULT, LOCAL, PREFILTER, VIEW, CANVAS).find(TUPLE_PROTOTYPE).getChild(0);
		      if (attRef == null) {return null;} //rule has no target, happens when the rule is for side effects only
		      Tree att = attRef.getChild(0);
		      if (layer == null || att==null) {return null;}
		      return key(layer.getText(), att.getText());
      }      
      //Should be unreachable (given call sites)
      assert false : "Could not construct key for tree of type " + StencilTree.typeName(tree.getType());
      return null;
    }
    
    
	  private String key(String layer, String attribute) {
     	MultiPartName att = new MultiPartName(attribute);
    	String key= layer + BIND_OPERATOR + att.getName();	//Trim to just the attribute name
    	return key;
    }
    
    /**Does the given call group already have the appropriate sampling operator?**/ 
    private boolean requiresChanges(StencilTree chain) {
      StencilTree r = chain.getAncestor(RULE);
      if (r == null || !requestedGuides.containsKey(key(r))) {return false;}
      String type = requestedGuides.get(key(r)).find(SAMPLE_TYPE).getText();
      String operatorName = operatorName(type);
      StencilTree call = chain.find(FUNCTION, PACK);
      while(call.getType() == FUNCTION) {
        if (operatorName.equals(call.getText())) {return false;}
        call = call.find(FUNCTION, PACK);
      }
      return true;
    }
    
    private static final String operatorName(String type) {
      Monitor mon = Samplers.monitor(type);
      String operatorName;
      switch (mon) {
        case FLEX : operatorName = "MonitorFlex"; break; 
        case CATEGORICAL : operatorName = "MonitorCategorical"; break;   
        case CONTINUOUS : operatorName = "MonitorContinuous"; break; 
        case NONE : throw new RuntimeException("Attempt to acquire null monitor in guide system.");
        default : throw new Error(String.format("Monitor type \%2\$s not handled (requested for sample type \%1\$s)", mon, type));
      }
      return String.format("\%1\$s.\%2\$s", operatorName, MAP_FACET);
    }
 
    /**Construct the arguments section of an echo call block.
     * 
     * @param t Call target that will follow the new echo operator.
     */
    private StencilTree echoArgs(StencilTree target) {
       StencilTree newArgs = (StencilTree) adaptor.create(LIST_ARGS, StencilTree.typeName(LIST_ARGS));
          
       StencilTree args = (target.getType() == PACK) ? target : target.find(LIST_ARGS);
       for (StencilTree v: args) {
          if (v.getType() == TUPLE_REF) {
            adaptor.addChild(newArgs, adaptor.dupTree(v));
          }
       }
       return newArgs;
    }
        
    private String selectOperator(StencilTree t) {
      StencilTree layer = t.getAncestor(LAYER);
      StencilTree r = t.getAncestor(RULE);
      String field = r.findDescendant(TUPLE_PROTOTYPE).getChild(0).getChild(0).getText();
      
      String type = requestedGuides.get(key(layer.getText(), field)).find(SAMPLE_TYPE).getText();
      return operatorName(type);
    }
    
    private StencilTree spec(StencilTree t) {
       StencilTree guide = requestedGuides.get(key(t.getAncestor(RULE))).getAncestor(GUIDE);
       Specializer spec = Freezer.specializer(guide.find(SPECIALIZER));
       StencilTree newSpec = (StencilTree) adaptor.create(SPECIALIZER, "");
       
       for (String k: spec.keySet()) {
          if (k.startsWith(SEED_PREFIX)) {
             String key = k.substring(SEED_PREFIX.length());       
             Object entry = adaptor.create(MAP_ENTRY, key);
             Const value = (Const) adaptor.create(CONST, "");
             value.setValue(spec.get(k));
             adaptor.addChild(entry, value);
             adaptor.addChild(newSpec, entry);
          }       
       }
       return newSpec;
    }
}

//Identify requested guides from canvas def
listRequirements: ^(sel=SELECTOR .*)
   {if (sel.getAncestor(GUIDE_DIRECT) != null) {
     requestedGuides.put(key(sel), sel);
   }};

//Replace the #-> with a monitor operator...
replaceCompactForm:
 ^(f=FUNCTION s=. a=. gy=GUIDE_YIELD t=.) ->
		^(FUNCTION $s $a DIRECT_YIELD[$gy.text] ^(FUNCTION[selectOperator($f)] {spec($t)} {echoArgs($t)} DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)] $t));  
		
ensure:
  ^(c=CALL_CHAIN t=.)
		  {$c.getParent().getChild(0).getType() == RESULT &&  requiresChanges($c)}? ->
		    ^(CALL_CHAIN    
		       ^(FUNCTION[selectOperator($t)] 
                  {spec($t)} 
                  {echoArgs($t)} 
                  DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)]
                  $t));
