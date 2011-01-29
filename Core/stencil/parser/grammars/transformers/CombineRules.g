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
  
  public Object mergeTargets(StencilTree rules) {
    stencil.tuple.prototype.TuplePrototype<stencil.tuple.prototype.TupleFieldDef> p = EnvironmentProxy.calcPrototype(rules);    

    Object target = adaptor.create(TARGET, "Result");
    Object proto = adaptor.create(TUPLE_PROTOTYPE, "");
    for (stencil.tuple.prototype.TupleFieldDef def: p.fields()) {
       Object fd = adaptor.create(TUPLE_FIELD_DEF, "");
       adaptor.addChild(fd, adaptor.create(ID, def.getFieldName()));
       adaptor.addChild(fd, adaptor.create(DEFAULT, ""));//Update when types are properly handled
       adaptor.addChild(proto, fd);
    }
    
    adaptor.addChild(target, proto);
    return target;
  }
  
  public StencilTree mergeChains(Object rules) {
    StencilTree newPack = (StencilTree) adaptor.create(PACK, "");

    StencilTree newChain = (StencilTree) adaptor.create(CALL_CHAIN, "");
    adaptor.addChild(newChain, adaptor.create(PACK, ""));
    StencilTree head = newChain;    

    for (int i=0; i<((Tree) rules).getChildCount(); i++) {
       StencilTree rule = (StencilTree) ((Tree) rules).getChild(i);
       StencilTree chain = rule.find(CALL_CHAIN);
       
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
  | ^(l=RULES_CANVAS rules[$l])    -> ^(RULES_CANVAS rules)
  | ^(l=RULES_VIEW rules[$l])      -> ^(RULES_VIEW rules)
  ; 
  
rules[StencilTree parent]: .+ -> ^(RULE {mergeTargets($parent)} {mergeChains($parent)} DEFINE);
  
  