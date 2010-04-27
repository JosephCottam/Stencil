/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


/**Moves rules from consumes blocks up to
 * the layer default set.  This move is performe
 * only if:
 *   (1) The rule appears in all consumes blocks
 *   (2) The rule has no tuple refs
 *   (3) The rule targets the glyph
 *   (4) Any operator call is to a function
 **/
tree grammar LiftSharedConstantRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
	superClass = TreeRewriteSequence;
}

@header {
	package stencil.parser.string;

	import java.util.Set;
	import java.util.HashSet;
	import java.util.ArrayList;

	import stencil.util.MultiPartName;
	import stencil.tuple.prototype.TuplePrototypes;		
  import stencil.parser.tree.util.Environment;
	import stencil.parser.tree.*;
	import stencil.parser.ParserConstants;	
	import stencil.operator.module.*;
	import stencil.operator.module.util.*;
}

@members {
	protected ModuleCache modules;

	public LiftSharedConstantRules(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		assert modules != null : "ModuleCache must not be null.";
		this.modules = modules;
	}
	
	public Object transform(Object t) throws Exception {
		t = lift(t);
		redefineLayers(t);
		return t;
	}	

	/**Build a list of things that need guides.**/
	private Object lift(Object t) throws Exception {
		fptr down =	new fptr() {public Object rule() throws RecognitionException { return liftShared(); }};
   	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
   	    return downup(t, down, up);
    }

	/**Build a list of things that need guides.**/
	private void redefineLayers(Object t) {
		fptr down =	new fptr() {public Object rule() throws RecognitionException { return updateLayers(); }};
   	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
   	    downup(t, down, up);
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

    //Is the passed Function Tree object include a facet that is a mathematical function?
    private boolean isFunction(Function f) {
    	MultiPartName name= new MultiPartName(f.getName());
    
       	try{
    		Module m = modules.findModuleForOperator(name.prefixedName()).module;
    		OperatorData od = m.getOperatorData(name.getName(), f.getSpecializer());
    		FacetData fd=od.getFacet(name.getSuffix());
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
}

liftShared: ^(LAYER impl=. defaults=. consumes=.)
	-> ^(LAYER $impl {augmentDefaults(defaults, consumes)} {reduceRules(consumes)});
	
	
updateLayers: ^(l=LAYER .*) {updateLayer((Layer)l);};