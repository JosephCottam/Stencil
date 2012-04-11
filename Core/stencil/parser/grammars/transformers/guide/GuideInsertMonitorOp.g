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
	    
   import stencil.module.*;
   import stencil.parser.tree.*;
   import stencil.parser.ParserConstants;
   import stencil.interpreter.tree.Freezer;
   import stencil.interpreter.tree.Specializer;
   import stencil.interpreter.tree.MultiPartName;
   import stencil.interpreter.guide.Samplers;
   import stencil.parser.ProgramCompileException;
   import stencil.parser.string.util.TreeRewriteSequence;
      
   import static stencil.parser.ParserConstants.BIND_OPERATOR;
   import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
   import static stencil.parser.string.util.Utilities.genSym;
   import static stencil.parser.string.util.Utilities.addOperator;
}

@members {
  public static StencilTree apply (StencilTree t, ModuleCache modules) {
     return TreeRewriteSequence.apply(t, modules);
  }
    
  protected void setup(Object... args) {this.modules = (ModuleCache) args[0];}
  
  public StencilTree downup(Object p) {
    StencilTree r;
    r = downup(p, this, "listRequirements");
    r = downup(r, this, "replaceCompactForm");    /**Replace the auto-categorize operator.**/
    r = downup(r, this, "ensure");  /**Make sure that things which need guides have minimum necessary operators.*/    
    return r;
  }

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
		      Tree attRef = tree.find(TARGET).find(TARGET_TUPLE).getChild(0);
		      if (attRef == null) {return null;} //rule has no target, happens when the rule is for side effects only
		      Tree att = attRef.getChild(0);
		      if (layer == null || att==null) {return null;}
		      return key(layer.getText(), att.getText());
      }      
      //Should be unreachable (given call sites)
      assert false : "Could not construct key for tree of type " + StencilTree.typeName(tree.getType());
      return null;
    }
    
    
	private String key(String layer, String att) {return layer + BIND_OPERATOR + att;}
    
    /**If there is a guide associated, 
    	does the monitor need the constants or the the tuple refs as its inputs?**/ 
    private boolean constArgs(StencilTree chain) {
      StencilTree r = chain.getAncestor(RULE);
      if (r == null || !requestedGuides.containsKey(key(r))) {return false;}
      String type = requestedGuides.get(key(r)).find(SAMPLE_TYPE).getText();
      return type.toUpperCase().equals("CONST");
    }
    
    /**Does the given call group already have the appropriate sampling operator?**/ 
    private boolean requiresChanges(StencilTree chain) {
      StencilTree r = chain.getAncestor(RULE);
      if (r == null || !requestedGuides.containsKey(key(r))) {return false;}      
      String type = requestedGuides.get(key(r)).find(SAMPLE_TYPE).getText();
      String operatorName = Samplers.monitor(type);
      if (operatorName == null) {throw new ProgramCompileException("Unknown sample type requested: " + type, chain);}
      
      
      StencilTree call = chain.find(FUNCTION, PACK);
      while(call.getType() == FUNCTION) {
        MultiPartName name = Freezer.multiName(call.find(OP_NAME));
        String root = name.name();
        if (root.contains(operatorName)) {return false;}
        call = call.find(FUNCTION, PACK);
      }
      return true;
    }
 
    /**Construct the arguments section of an echo call block.
     * 
     * @param t Call target that will follow the new echo operator.
     */
    private StencilTree echoArgs(StencilTree target, boolean consts) {
       StencilTree newArgs = (StencilTree) adaptor.create(LIST_ARGS, StencilTree.typeName(LIST_ARGS));
       StencilTree args = (target.getType() == PACK) ? target : target.find(LIST_ARGS);
       for (StencilTree v: args) {
          if (v.getType() == TUPLE_REF && !consts) {
            adaptor.addChild(newArgs, adaptor.dupTree(v));
          } else if (v.getType() != TUPLE_REF && consts) {
            adaptor.addChild(newArgs, adaptor.dupTree(v));
          }
       }
       return newArgs;
    }
        
    /**Determine which monitor operator to use.
      * Create and instance of it, add it to the operators list and return the name.  
      *If this is to replace a compact form, the flag should be set to true (different error conditions apply)
      **/
    private String makeOperator(StencilTree t, boolean compactForm) {
      StencilTree layer = t.getAncestor(LAYER);
      StencilTree r = t.getAncestor(RULE);
      String field = Freezer.tupleField(r.findDescendant(TARGET_TUPLE).getChild(0)).toString();

      StencilTree request = requestedGuides.get(key(layer.getText(), field));
      String opName;
      if (request == null && !compactForm) {throw new RuntimeException("Error construction guide: Request for guide does not correspond to a property with rules.");}
      else if (request == null) {opName= Samplers.monitor("NOP");}
      else {
          String type = request.find(SAMPLE_TYPE).getText();
          opName = Samplers.monitor(type);
      }

      Specializer specializer = Freezer.specializer(spec(t));
      String refName = addOperator(opName, specializer, modules, t, adaptor);
      
      return refName;
    }
    
    private StencilTree spec(StencilTree t) {
       StencilTree request = requestedGuides.get(key(t.getAncestor(RULE)));
       if (request == null) {return ParserConstants.EMPTY_SPECIALIZER_TREE;}
       
       StencilTree guide = request.getAncestor(GUIDE);
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
   ^(f=FUNCTION n=. s=. a=. gy=GUIDE_YIELD t=.) ->
		^(FUNCTION  $n $s $a DIRECT_YIELD[$gy.text] 
		      ^(FUNCTION 
		          ^(OP_NAME DEFAULT ID[makeOperator($t, true)] DEFAULT_FACET) 
		          {spec($t)} 
		          {echoArgs($t, constArgs($t.getAncestor(CALL_CHAIN)))} 
		          DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)] 
		          $t));  
		
ensure:
  ^(c=CALL_CHAIN t=.)
		  {$c.getAncestor(RULES_RESULT) != null &&  requiresChanges($c)}? ->
		    ^(CALL_CHAIN    
		       ^(FUNCTION
		          ^(OP_NAME DEFAULT ID[makeOperator($t, false)] DEFAULT_FACET) 
                  {spec($t)} 
                  {echoArgs($t, constArgs($t.getAncestor(CALL_CHAIN)))} 
                  DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)]
                  $t));
	                  
