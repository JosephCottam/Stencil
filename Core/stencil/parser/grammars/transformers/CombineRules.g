tree grammar CombineRules;

options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
  /** Takes all rules in a combined-execution group (cosumes block, operator sub-group, etc) and turns them into a single rule.
   ** Must be run after operators are instantiated.
   **/
   package stencil.parser.string;
	
   import stencil.parser.tree.*;
   import stencil.parser.string.util.*;
}

@members {  
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
 
  //Merge the targets----------------------------------------------
  public Object mergeTargets(StencilTree rulesRoot) {
    Object tuple = adaptor.create(TARGET_TUPLE, "");
    for (StencilTree def: gatherTargets(rulesRoot)) {
       adaptor.addChild(tuple, adaptor.dupTree(def));
    }    
    return tuple;
  }
  
   
  public static Iterable<StencilTree> gatherTargets(Iterable<StencilTree> rules) {
        List<StencilTree> defs = new ArrayList();
        for (StencilTree r: rules) {
            StencilTree target = r.find(TARGET);    //Will only actually find one time if this is run after the types have been separated
            defs.addAll(target.findAllDescendants(TUPLE_FIELD));
        }
        return defs;
  }
  

  //Merge the chains and  packs
  public StencilTree mergeChains(Object rules) {
    StencilTree newPack = (StencilTree) adaptor.create(PACK, "");

    StencilTree newChain = (StencilTree) adaptor.create(CALL_CHAIN, "");
    adaptor.addChild(newChain, adaptor.create(PACK, ""));
    StencilTree head = newChain;    

    for (int i=0; i<((Tree) rules).getChildCount(); i++) {
       StencilTree rule = (StencilTree) ((Tree) rules).getChild(i);
       StencilTree chain = (StencilTree) RenameFrames.apply((Tree) adaptor.dupTree(rule.find(CALL_CHAIN)));
       
       //Splice in call chain
       Object splice = chain.find(FUNCTION);
       if (splice != null) {//Splice is null when values are copied straight out of the input (i.e., there are function nodes used in transforming the input value)
         StencilTree over = head.findDescendant(PACK); 
         adaptor.replaceChildren(over.getParent(), 
                                 over.getChildIndex(), 
                                 over.getChildIndex(), 
                                 adaptor.dupTree(splice));
       }
       
       StencilTree pack = (StencilTree) chain.findDescendant(PACK);
       //Construct composite pack
       for (int pc=0; pc< pack.getChildCount(); pc++) {
         Object packArg = pack.getChild(pc);
           newPack.addChild((Tree) adaptor.dupTree(packArg));
       }
    }
    
    //Use composite pack
    StencilTree pack = head.findDescendant(PACK);
    adaptor.replaceChildren(pack.getParent(), 
                            pack.getChildIndex(), 
                            pack.getChildIndex(), 
                            newPack);
    
    return newChain;
  }  
}

topdown
  : ^(l=RULES_PREFILTER rules[$l]) -> ^(RULES_PREFILTER rules)  
  | ^(l=RULES_LOCAL rules[$l])     -> ^(RULES_LOCAL rules)  
  | ^(l=RULES_RESULT rules[$l])    -> ^(RULES_RESULT rules)
  | ^(l=LIST_RULES rules[$l])      -> ^(LIST_RULES rules)
  | ^(l=RULES_DEFAULTS rules[$l])  -> ^(RULES_DEFAULTS rules)
  ; 
  
rules[StencilTree parent]: .+ -> ^(RULE ^(TARGET {mergeTargets($parent)}) {mergeChains($parent)} DEFINE);
  
  