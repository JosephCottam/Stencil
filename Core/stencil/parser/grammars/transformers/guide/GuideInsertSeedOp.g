tree grammar GuideInsertSeedOp;
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
   *  TODO: Needs to be fixed so that constants don't mess this up...ARG!!!!
   *  TODO: Extend so to can handle more than the first field in a mapping definition
   **/
	 
   package stencil.parser.string;
	    
   import stencil.parser.tree.util.*;
   import stencil.module.*;
   import stencil.parser.tree.*;
   import stencil.parser.ParseStencil;
   import stencil.interpreter.tree.Freezer;
   import stencil.interpreter.tree.Specializer;
   
   import static stencil.parser.ParserConstants.BIND_OPERATOR;
   import static stencil.parser.ParserConstants.MAP_FACET;
   import static stencil.interpreter.guide.Samplers.CATEGORICAL;
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

  private static final String SEED_PREFIX = "seed.";

  private static final boolean isCategorical(Specializer spec) {
    return !spec.containsKey(SAMPLE_KEY) ||
           CATEGORICAL.equals(spec.get(SAMPLE_KEY));
  }

	protected ModuleCache modules;

  //Mapping from requested guides to descriptor construction strategy.  This is populated by the 'build' pass
  protected HashMap<String, Specializer> requestedGuides = new HashMap();
    
    /**Given a tree, how should it be looked up in the guides map?*/
    private String key(StencilTree tree) {
      if (tree.getType() == SELECTOR) {
          return key(tree.getChild(0).getText(), tree.getChild(1).getText()); //TODO: Extend the range of keys beyond layer/att pairs to full paths
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
      Specializer strat = requestedGuides.get(key(r));
      String operatorName = operatorName(strat);
      StencilTree call = chain.find(FUNCTION, PACK);
      while(call.getType() == FUNCTION) {
        if (operatorName.equals(call.getText())) {return false;}
        call = call.find(FUNCTION, PACK);
      }
      return true;
    }
    
    private static final String operatorName(Specializer spec) {
           String operatorName;
      
      if (spec == null || isCategorical(spec)) {
        operatorName = "SeedCategorize";
      } else {
        operatorName = "SeedContinuous";
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
    
    //HACK: Do this more directly, just build the tree (specializer is simpler than it used to be)
    private StencilTree spec(StencilTree target) {
      StringBuilder b = new StringBuilder("[range" + BIND_OPERATOR + " ALL,");      
      
      //Get additional map arguments from the guide declaration
      Specializer spec = requestedGuides.get(key(target));
      for (String k: spec.keySet()) {
          if (k.startsWith(SEED_PREFIX)) {
             String value = spec.get(k).toString();
             String key = k.substring(SEED_PREFIX.length());
             
             b.append(key);
             b.append(BIND_OPERATOR);
             b.append(value);
             b.append(",");
          }
      }
      b.replace(b.length()-1, b.length(), "]");
      try {return ParseStencil.specializerTree(b.toString());}
      catch (Exception e) {throw new Error("Error parsing synthesized specializer: " + b.toString());}
    }
        
    private String selectOperator(StencilTree t) {
      StencilTree layer = t.getAncestor(StencilParser.LAYER);
      StencilTree r = t.getAncestor(StencilParser.RULE);
      String field = r.findDescendant(TUPLE_PROTOTYPE).getChild(0).getChild(0).getText();
      
      Specializer strat = requestedGuides.get(key(layer.getText(), field));
      return operatorName(strat);
    }
}

//Identify requested guides from canvas def
listRequirements: ^(GUIDE_DIRECT ^(guide=GUIDE .*)) 
   {requestedGuides.put(key(guide.find(SELECTOR)), Freezer.specializer(guide.find(SPECIALIZER)));};

//Replace the #-> with an echo operator...
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
