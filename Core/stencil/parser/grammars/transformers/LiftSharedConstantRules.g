tree grammar LiftSharedConstantRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
  superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header {
	/**Moves rules from consumes blocks up to
	 * the layer default set.  This move is performe
	 * only if:
	 *   (1) The rule appears in all consumes blocks
	 *   (2) The rule has no tuple refs
	 *   (3) The rule targets the glyph
	 *   (4) Any operator call is to a function
	 **/

	package stencil.parser.string;

	import java.util.Set;
	import java.util.HashSet;
	import java.util.ArrayList;

  import stencil.tuple.prototype.TuplePrototypes;   
  import stencil.tuple.Tuple;
  import stencil.tuple.Tuples;
  import stencil.interpreter.Interpreter;
  import stencil.parser.tree.*;
  import stencil.parser.ParserConstants;  
}

@members {	
	public Object transform(Object t) throws Exception {
		t = (Program) downup(t, this, "liftShared");
		t = (Program) downup(t, this, "updateLayers");
		return t;
	}	

	private List<Rule> sharedConstants(stencil.parser.tree.List<Consumes> consumes) {
		List<List<Rule>> ruleSet = allConstants(consumes);
		Set<Rule> sharedRules = new HashSet();
		
		for (Rule candidate: ruleSet.get(0)) {
			boolean alwaysPresent = true;
			if (selectorRule(candidate)) {continue;}
			for (List<Rule> rules: ruleSet) {
				boolean present = false;
				for (Rule other: rules) {
					present = present || treeEquals(candidate, other);
					if (present) {break;}
				}
				alwaysPresent = present && alwaysPresent;
				if (!alwaysPresent) {break;}
			}
			if (alwaysPresent) {sharedRules.add(candidate);}					
		}
		return new ArrayList(sharedRules);
	}

	private List<List<Rule>> allConstants(stencil.parser.tree.List<Consumes> consumes) {
		List<List<Rule>> rules = new ArrayList();
		for (Consumes c: consumes) {
			rules.add(constants(c.getResultRules()));
		}
		return rules;
	}
	
	private List<Rule> constants(List<Rule> rules) {
		List<Rule> constants = new ArrayList();
		for (Rule rule: rules) {
			if (isConstant(rule)) {
   				Rule constantRule = (Rule) adaptor.dupTree(rule);
   				constants.add(constantRule);
			}
		}
		return constants;
	}

	/**Is this the selector rule (e.g. it sets ID)?  The selector rule cannot be lifted.*/	
	private boolean selectorRule(Rule candidate) {
   		for (String name: TuplePrototypes.getNames(candidate.getTarget().getPrototype())) {
   			if (name.equals(ParserConstants.GLYPH_ID_FIELD)) {return true;}
   		}
   		return false;
   	}
	
	  /**Is the give rule a constant rule?
	    Constant rules have pack as the only component in their call target and
	    do not contain any tuple refs.
	  **/
    private boolean isConstant(Rule rule) {
      StencilTree target = rule.getAction().getStart();
      return target instanceof Pack
             && target.getFirstChildWithType(TUPLE_REF) == null;
    }
    
	private StencilTree augmentDefaults(Tree d, Tree c) {
		stencil.parser.tree.List<Rule> defaults = (stencil.parser.tree.List<Rule>) d;
		stencil.parser.tree.List<Consumes> consumes = (stencil.parser.tree.List<Consumes>) c;
	
		List<Rule> sharedConstants = sharedConstants(consumes);
		
		for (Rule rule: sharedConstants) {
      Rule r =  (Rule) adaptor.dupTree(rule);
      defaults.addChild(r);
      adaptor.setParent(r, defaults);
		}
		
		return defaults;
	}
	
	
	private StencilTree reduceRules(Tree c) {
		stencil.parser.tree.List<Consumes> consumes = (stencil.parser.tree.List<Consumes>) c;
		
		List<Rule> sharedConstants = sharedConstants(consumes);
		
		    		
    	//To keep from messing up the iterator, we create a list of rules to delete
    	//and delete them in batch later.
    	//
    	//This may be longer than the sharedConstants list, since this will be a list of ALL instances
    	//of rules that match those found in the sharedConstants list.  (exact length is sharedConstants.size()*consumes.size())
    	List<Rule> toDelete = new ArrayList();
    		
    	for (Rule constRule: sharedConstants) {
    		for (Consumes consume: consumes) {
    			for (Rule rule: consume.getResultRules()) {
    				if (treeEquals(constRule, rule)) {
    					toDelete.add(rule);
    				}
    			}
    		}
    	}

    	for (Rule rule: toDelete) {
    		rule.getParent().deleteChild(rule.getChildIndex());
    	}
    		
    	return consumes;
	}
	
	private boolean treeEquals(Tree t1, Tree t2) {
		return t1 == t2  || t1.equals(t2) || t1.toStringTree().equals(t2.toStringTree());
	}
	
	private void updateLayer(Layer layerDef) {
     try {
	      Tuple defaults = Interpreter.processSequential(Tuples.EMPTY_TUPLE, layerDef.getDefaults());	
    	  layerDef.getDisplayLayer().updatePrototype(defaults);
     } catch (Exception e) {
        throw new RuntimeException("Error updating layer defaults.", e);
     }
	}

}

liftShared: ^(LAYER impl=. defaults=. consumes=.)
	-> ^(LAYER $impl {augmentDefaults(defaults, consumes)} {reduceRules(consumes)});
	
updateLayers: ^(l=LAYER .*) {updateLayer((Layer)l);};