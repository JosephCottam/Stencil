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

  import stencil.parser.tree.util.*;
  import stencil.tuple.prototype.TuplePrototypes;		
  import stencil.parser.tree.util.Environment;
  import stencil.tuple.Tuple;
  import stencil.tuple.Tuples;
  import stencil.interpreter.Interpreter;
  import stencil.parser.tree.*;
  import stencil.parser.ParserConstants;	
  import stencil.module.*;
  import stencil.module.util.*;
}

@members {
	protected ModuleCache modules;

	public LiftSharedConstantRules(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		assert modules != null : "ModuleCache must not be null.";
		this.modules = modules;
	}
	
	public Object transform(Object t) throws Exception {
	  t = (Program) downup(t, this, "evaluateConstantRules");
		t = (Program) downup(t, this, "liftShared");
		downup(t, this, "updateLayers");
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
	
	  //Is the give rule a constant rule?
	  //Constant rules have no tuple refs and include only functions
    private boolean isConstant(Tree rule) {
        int children = rule.getChildCount();
        for (int i=0; i<children; i++) {
          Tree child = rule.getChild(i);
          if (child instanceof TupleRef) {
            return !runtimeRef((TupleRef) child);
          }
            if (child instanceof Function &&
              !isFunction((Function) child)) {return false;}
          if (!isConstant(child)) {return false;}
        }
        return true;
    }

    //A runtime ref is any reference that looks at runtime specific values
    private boolean runtimeRef(TupleRef ref) {
      Atom a = ref.getValue();
      int idx = ((Number) a.getValue()).intValue();
      return idx < Environment.DEFAULT_SIZE;
    }

    //Is the passed Function using a facet that is a mathematical function?
    private boolean isFunction(Function f) {
    	MultiPartName name= new MultiPartName(f.getName());
      try{
    		Module m = modules.findModuleForOperator(name.prefixedName());
    		OperatorData od = m.getOperatorData(name.getName(), f.getSpecializer());
    		FacetData fd=od.getFacet(name.getFacet());
    		return fd.isFunction();
   		} catch (Exception e) {
   			throw new RuntimeException("Error getting module information for operator " + name, e);
		}
	
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
    	layerDef.getDisplayLayer().updatePrototype(layerDef);
	}
	
	private CommonTree evaluate(Rule r) {
	   Tuple result;
	   try {result = Interpreter.process(Tuples.EMPTY_TUPLE, r);}
	   catch (Exception e) {
	      throw new RuntimeException("Error partially evaluating rule: " + r.toStringTree(), e);
	   }
	
	   Pack p = (Pack) adaptor.create(PACK, "PACK");
	   for (int i=0; i< result.size(); i++) {
	      adaptor.addChild(p, Atom.Literal.instance(result.get(i)));
	   }
	   return p;
	}
}

evaluateConstantRules
  : ^(r=RULE result=. cc=. bind=.) {isConstant($r)}? 
        -> ^(RULE $result ^(CALL_CHAIN {evaluate((Rule) $r)} ^(NUMBER["0"])) $bind); 

liftShared: ^(LAYER impl=. defaults=. consumes=.)
	-> ^(LAYER $impl {augmentDefaults(defaults, consumes)} {reduceRules(consumes)});
	
updateLayers: ^(l=LAYER .*) {updateLayer((Layer)l);};