tree grammar LiftLayerConstants;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree;	
    superClass = TreeRewriteSequence;
    output = AST;
    filter = true;
}

@header {
	/**Moves values from the rule set to the default blocks if the value:
	 *   (1) Appears in all consumes blocks
	 *   (2) Is an atom
	 *   (3) Is not a layer identity field (e.g. ID, IDX)
	 *   
	 *   Must be run after constant propogation and constant operator evaluation
	 **/

	package stencil.parser.string;

	import java.util.Set;
	import java.util.HashSet;
	import java.util.ArrayList;
  import java.util.Collection;

  import stencil.tuple.Tuple;
  import stencil.tuple.Tuples;
  import stencil.interpreter.Interpreter;
  import stencil.interpreter.tree.Freezer;
  import stencil.interpreter.tree.Rule;
  import stencil.parser.tree.StencilTree;
  import stencil.parser.tree.Const;
  import stencil.parser.ParserConstants;  
  import stencil.display.DisplayLayer;
}

@members {	
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public Object downup(Object t) {
    downup(t, this, "liftShared");
    downup(t, this, "updateLayers");  
    return t;
  }


  private static class Pair {
     final String att;
     final Tree value;
     public Pair(String att, Tree value) {this.att = att; this.value=value;}
     public int hashCode() {
      return att.hashCode() + value.hashCode();
     }
     public boolean equals(Object other) {
        return other instanceof Pair
                && att.equals(((Pair) other).att)
                && value.equals(((Pair) other).value);
     }
     
     //Create a constant rule from this pair.  
     public Object asRule(TreeAdaptor adaptor) {
       Object r = adaptor.create(RULE, "RULE");
       Object tar = adaptor.create(TARGET, "TARGET");
       Object cc = adaptor.create(CALL_CHAIN, "CALL_CHAIN");
       adaptor.addChild(r, tar);
       adaptor.addChild(r, cc);
       adaptor.addChild(r, adaptor.create(DEFINE, ""));
           
       Object proto = adaptor.create(TUPLE_PROTOTYPE, "TUPLE_PROTOTYPE");
       Object fd = adaptor.create(TUPLE_FIELD_DEF, "TUPLE_FIELD_DEF");
       adaptor.addChild(tar, proto);
       adaptor.addChild(proto, fd);
       adaptor.addChild(fd, adaptor.create(ID, att));
       adaptor.addChild(fd, adaptor.create(DEFAULT, "DEFAULT"));

       Object pack = adaptor.create(PACK, "PACK");
       adaptor.addChild(pack, adaptor.dupTree(value));
       adaptor.addChild(cc, pack);
       adaptor.addChild(cc, adaptor.create(NUMBER, "0"));
           
       return r;
     }
     
  }
	
	//Constants shared by all blocks
	private Collection<Pair> sharedConstants(Tree blocks) {
	  Set<Pair> constants = new HashSet();
	  boolean first = true;
	  for (int blockID=0; blockID< blocks.getChildCount(); blockID++) {
	     StencilTree block = (StencilTree) blocks.getChild(blockID);
	     Collection newConsts = constants(block);
	     if (first) {constants.addAll(newConsts);first=false;}   //Prime
	     else {constants.retainAll(newConsts);}                  //Intersection
	  }
	  return constants;
	}

  //Constants found in any block;  A constant is a att/value pair where the value is not a tuple ref
	private Collection<Pair> constants(StencilTree block) {
	   Collection<Pair> consts = new ArrayList();
	   Tree results = (StencilTree) block.find(RULES_RESULT);
	   for (int ruleID=0; ruleID<results.getChildCount(); ruleID++) {
	      StencilTree rule = (StencilTree) results.getChild(ruleID);
        Tree target = ((StencilTree) rule.findDescendant(TARGET)).find(TUPLE_PROTOTYPE);
        Tree pack = rule.findDescendant(PACK);
	      
	      for (int resultID=0; resultID<pack.getChildCount(); resultID++) {
	         Tree value = pack.getChild(resultID);
	         if (value.getType() == TUPLE_REF) {continue;}
	         
	         String name = ((StencilTree) target.getChild(resultID)).findDescendant(ID).getText();
           if (selectorRule(name)) {continue;}
           consts.add(new Pair(name, value));
	      }
	   } 
     return consts;
	}
	
	private Object reduceConstants(StencilTree blocks) {
	   blocks = (StencilTree) adaptor.dupTree(blocks);
	   Collection<Pair> sharedConstants = sharedConstants(blocks);
	   List<Pair> consts = new ArrayList();
     
     for (int b=0; b< blocks.getChildCount(); b++) {
       Tree block = blocks.getChild(b);
       Tree results = ((StencilTree) block).find(RULES_RESULT);
       for (int i=0; i<results.getChildCount(); i++) {
          StencilTree rule = (StencilTree) results.getChild(i);
          Tree target = ((StencilTree) rule.findDescendant(TARGET)).find(TUPLE_PROTOTYPE);
          Tree pack = rule.findDescendant(PACK);
          for (int j=0; j<pack.getChildCount(); j++) {
             Tree value = pack.getChild(j);
             String name = ((StencilTree) target.getChild(j)).findDescendant(ID).getText();
             Pair pair = new Pair(name, value);
             if (sharedConstants.contains(pair)) {
                adaptor.deleteChild(pack, j);
                adaptor.deleteChild(target, j);
                j--;//backup one was just deleted
             }
          }
       }
     }
     return blocks;
	}

	/**Is this a selector field (e.g. it sets ID)?  Selectors cannot be lifted.*/	
	private boolean selectorRule(String name) {return name.startsWith(ParserConstants.SELECTOR_FIELD);}
	
	private Object augmentDefaults(StencilTree defaults, StencilTree consumes) {
		Collection<Pair> sharedConstants = sharedConstants(consumes);
		for (Pair constant: sharedConstants) {
      adaptor.addChild(defaults,constant.asRule(adaptor));
		}
		return defaults;
	}
	
	private void updateLayer(StencilTree layerDef) {
     try {
        StencilTree rules = layerDef.find(RULES_DEFAULTS);
        for (StencilTree ruleSource: rules) {
           Rule rule = Freezer.rule(ruleSource);
           Tuple defaults = Interpreter.processTuple(Tuples.EMPTY_TUPLE, rule);
	       DisplayLayer dl = (DisplayLayer) ((Const) layerDef.find(CONST)).getValue();	
    	   dl.updatePrototype(defaults);
    	}
     } catch (Exception e) {
        throw new RuntimeException("Error updating layer defaults.", e);
     }
	}

}

liftShared: ^(LAYER spec=. defaultList=. consumes=. direct=.)
  -> ^(LAYER $spec {augmentDefaults((StencilTree) defaultList, (StencilTree) consumes)} {reduceConstants((StencilTree) consumes)} $direct);
	
updateLayers: ^(l=LAYER .*) {updateLayer(l);};