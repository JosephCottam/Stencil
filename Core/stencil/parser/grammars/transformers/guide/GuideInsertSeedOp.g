tree grammar GuideInsertSeedOp;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
	superClass = TreeRewriteSequence;
}

@header{
  /** Ensures that a sample operator exists in the call chain.
   *  Sample operators are inserted where a #-> appears,
   *  after the last project operator in a chain or 
   *  at the start of the chain.
   *
   *  TODO: Needs to be fixed so that constants don't mess this up...ARG!!!!
   **/
	 
   package stencil.parser.string;
	 
   import java.util.Set;
   import java.util.HashSet;
   
   import stencil.parser.tree.util.*;
   import stencil.util.collections.ArrayUtil;
   import stencil.module.*;
   import stencil.module.operator.StencilOperator;
   import stencil.parser.tree.*;
   import stencil.parser.ParseStencil;
   
   import static stencil.parser.ParserConstants.BIND_OPERATOR;
   import static stencil.parser.ParserConstants.MAP_FACET;
   import static stencil.interpreter.guide.Samplers.CATEGORICAL;
   import static stencil.interpreter.guide.Samplers.SAMPLE_KEY;
   import static stencil.parser.ParserConstants.BIND_OPERATOR;
   
   import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
   import static stencil.parser.string.util.Utilities.genSym;
	
   //TODO: Extend so we can handle more than the first field in a mapping definition
}

@members {
  public static Program apply (Tree t, ModuleCache modules) {
     return (Program) TreeRewriteSequence.apply(t, modules);
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
    return !spec.getMap().containsKey(SAMPLE_KEY) ||
           CATEGORICAL.equals(spec.getMap().get(SAMPLE_KEY).getValue());
  }

	protected ModuleCache modules;

  //Mapping from requested guides to descriptor construction strategy.  This is populated by the 'build' pass
  protected HashMap<String, Specializer> requestedGuides = new HashMap();
    
    private String key(Selector s) {
      List<Id> path = s.getPath();
      return key(path.get(0).getText(), path.get(1).getText()); //TODO: Extend the range of keys beyond layer/att pairs to full paths
    }
    
    private String key(CallTarget target) {return key((Rule) target.getAncestor(RULE));}

    /**Given a tree, how should it be looked up in the guides map?*/
    private String key(Rule rule) {
      Tree layer = rule.getAncestor(LAYER);
      Tree attRef = ((TuplePrototype) rule.getGenericTarget().findChild(TUPLE_PROTOTYPE)).get(0);
      if (attRef == null) {return null;} //rule has no target, happens when the rule is for side effects only
      Tree att = attRef.getChild(0);
      if (layer == null || att==null) {return null;}
      return key(layer.getText(), att.getText());
    }
    
    
	  private String key(String layer, String attribute) {
     	MultiPartName att = new MultiPartName(attribute);
    	String key= layer + BIND_OPERATOR + att.getName();	//Trim to just the attribute name
    	return key;
    }
    
    /**Does the given call group already have the appropriate sampling operator?**/ 
    private boolean requiresChanges(CallChain chain) {
      Rule r = (Rule) chain.getAncestor(RULE);
      if (r == null || !requestedGuides.containsKey(key(r))) {return false;}
      Specializer strat = requestedGuides.get(key(r));
      String operatorName = operatorName(strat);
      CallTarget call = chain.getStart();
      while(!(call instanceof Pack)) {
        Function f  = (Function) call;
        if (operatorName.equals(f.getName())) {return false;}
        call = f.getCall();
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
     * TODO: Remove by having guide happen AFTER numeralize occurs
     * 
     * @param t Call target that will follow the new echo operator.
     */
    private List<Value> echoArgs(Tree target) {return echoArgs((CallTarget) target);}
    private List<Value> echoArgs(CallTarget target) {
    	List<Value> args = (List<Value>) adaptor.create(LIST, "Arguments");
    	
 		  for (Value v: target.getArguments()) {
 			  if (v.isAtom()) {continue;}
 			  adaptor.addChild(args, adaptor.dupTree(v));
 		  }
 		  return args;
    }
    
    private Specializer spec(CommonTree t) {
      CallTarget target = (CallTarget) t;
      List<Value> args = echoArgs(target);
      StringBuilder b = new StringBuilder("[range" + BIND_OPERATOR + " ALL, fields: \"");
      for (Value v: args) {
        if (v instanceof TupleRef) {
	        b.append(((TupleRef) v).getValue());
	        b.append(",");
	      }
      }
      b.replace(b.length()-1, b.length(), "\",");
      
      
      //Get additional map arguments from the guide declaration
      Specializer spec = requestedGuides.get(key(target));
      for (String k: spec.getMap().keySet()) {
          if (k.startsWith(SEED_PREFIX)) {
             String value = spec.getMap().get(k).toString();
             String key = k.substring(SEED_PREFIX.length());
             
             b.append(key);
             b.append(BIND_OPERATOR);
             b.append(value);
             b.append(",");
          }
      }
      b.replace(b.length()-1, b.length(), "]");
      try {return ParseStencil.parseSpecializer(b.toString());}
      catch (Exception e) {throw new Error("Error parsing synthesized specializer: " + b.toString());}
    }
    
     public CallTarget getStart(Tree c) {
       return ((CallChain) c).getStart();
     }
    
    private String selectOperator(Tree t) {
      Layer layer = (Layer) t.getAncestor(StencilParser.LAYER);
      Rule r = (Rule) t.getAncestor(StencilParser.RULE);
      String field = ((TuplePrototype) r.getGenericTarget().findChild(TUPLE_PROTOTYPE)).get(0).getFieldName();
      
      Specializer strat = requestedGuides.get(key(layer.getName(), field));
      return operatorName(strat);
    }
}

//Identify requested guides from canvas def
listRequirements: ^(GUIDE_DIRECT ^(GUIDE type=. spec=. sel=. actions=.)) 
   {requestedGuides.put(key((Selector) sel), (Specializer) spec);};

//Replace the #-> with an echo operator...
replaceCompactForm:
 ^(f=FUNCTION s=. a=. gy=GUIDE_YIELD t=.) ->
		^(FUNCTION $s $a DIRECT_YIELD[$gy.text] ^(FUNCTION[selectOperator($f)] {spec($t)} {echoArgs($t)} DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)] $t));  
		
ensure:
  ^(c=CALL_CHAIN t=. d=.)
		  {$c.getParent().getChild(0).getType() == RESULT &&  requiresChanges((CallChain) $c)}? ->
		    ^(CALL_CHAIN    
		         ^(FUNCTION[selectOperator($t)] 
                {spec($t)} 
                {echoArgs($t)} 
                DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)]
                $t)
            $d);
