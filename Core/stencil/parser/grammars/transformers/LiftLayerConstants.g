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
   *   Also tags the constant fields in the schema as constants (reducing the amount of copying done and storage used).
   *   Must be run after constant propagation and constant operator evaluation
   *
   *  TODO: Change to re-instantiate the layer (instead of mutating it)...allows the .layer() accessor to be removed from LayerOperator 
  **/

  package stencil.parser.string;

  import java.util.Set;
  import java.util.HashSet;
  import java.util.Collection;

 
  import stencil.tuple.PrototypedTuple;
  import stencil.tuple.Tuples;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.tuple.instances.MultiResultTuple;
  import stencil.interpreter.Interpreter;
  import stencil.interpreter.tree.*;
  import stencil.parser.tree.StencilTree;
  import stencil.parser.tree.Const;
  import stencil.parser.ParserConstants;  
  import stencil.display.DisplayLayer;
  import stencil.display.SchemaFieldDef;
  import stencil.types.Converter;
  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.module.operator.wrappers.LayerOperator;
  import stencil.parser.string.util.Utilities;
  
  
}

@members {	
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public StencilTree downup(Object p) {
    StencilTree r;
    r = downup(p, this, "liftShared");
    r = downup(r, this, "updateLayers");  
    r = downup(r, this, "flagConstantFields");  
    return r;
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
           
       Object result = adaptor.create(TARGET_TUPLE, "TARGET_TUPLE");
       Object fd = adaptor.create(TUPLE_FIELD, "TUPLE_FIELD");
       adaptor.addChild(tar, result);
       adaptor.addChild(result, fd);
       adaptor.addChild(fd, adaptor.create(ID, att));

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
        Tree target = ((StencilTree) rule.findDescendant(TARGET)).find(TARGET_TUPLE);
        Tree pack = rule.findDescendant(PACK);
	      
	      for (int resultID=0; resultID<pack.getChildCount(); resultID++) {
	         Tree value = pack.getChild(resultID);
	         if (value.getType() == TUPLE_REF) {continue;}
	         
	         String name = ((StencilTree) target.getChild(resultID)).getChild(0).getText();
             if (identifierRule(name)) {continue;}
             if (value.getType() == CONST && ((Const) value).getValue() instanceof MultiResultTuple) {continue;}
             
             consts.add(new Pair(name, value));
	      }
	   } 
     return consts;
	}
	
	private Object reduceConstants(StencilTree blocks) {
	   blocks = (StencilTree) adaptor.dupTree(blocks);
	   Collection<Pair> sharedConstants = sharedConstants(blocks);
     
     for (int b=0; b< blocks.getChildCount(); b++) {
       Tree block = blocks.getChild(b);
       Tree results = ((StencilTree) block).find(RULES_RESULT);
       for (int i=0; i<results.getChildCount(); i++) {
          StencilTree rule = (StencilTree) results.getChild(i);
          Tree target = ((StencilTree) rule.findDescendant(TARGET)).find(TARGET_TUPLE);
          Tree pack = rule.findDescendant(PACK);
          for (int j=0; j<pack.getChildCount(); j++) {
             Tree value = pack.getChild(j);
             String name = ((StencilTree) target.getChild(j)).getChild(0).getText();
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

	/**Is this a identifier field (e.g. it sets ID)?  Identifiers cannot be lifted.*/	
	private boolean identifierRule(String name) {return name.equals(ParserConstants.IDENTIFIER_FIELD);}
	
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
           LayerOperator layerOp = (LayerOperator) Utilities.findOperator(layerDef, layerDef.getText());
           DisplayLayer dl = layerOp.layer();
           Rule rule = Freezer.rule(ruleSource);
           
           //Find the defaults...
           PrototypedTuple updates = (PrototypedTuple) Interpreter.processTuple(Tuples.EMPTY_TUPLE, rule);
           
           //Merge with existing layer schema
           //TODO: Push the default values into the layer around the time specializers are set, then this lookup/merge can be skipped
            
           TuplePrototype<SchemaFieldDef> current = dl.prototype();
           SchemaFieldDef[] updated = new SchemaFieldDef[current.size()];
           for (int i=0; i<current.size(); i++) {
              SchemaFieldDef def = current.get(i);
              if (!updates.prototype().contains(def.name())) {
                  updated[i] = def;
              } else {
                  Object val = Converter.convert(updates.get(def.name()), def.type());
                  updated[i] = new SchemaFieldDef(def.name(), val, def.type(), false);
              }
           }
           
           //Update the layer
    	   dl.updatePrototype(new TuplePrototype(updated));
    	}
     } catch (Exception e) {
        throw new RuntimeException("Error updating layer defaults.", e);
     }
	}
	
	/**Mark constant fields as constants in the layer.
	 * Must be done AFTER constant lifting and the layer def has been updated.
	 **/ 
	private void flagConstFields(StencilTree layerDef) {
     try {
       LayerOperator layerOp = (LayerOperator) Utilities.findOperator(layerDef, layerDef.getText());
       DisplayLayer dl = layerOp.layer();
 
       //Calculate which columns are actually updated
       List<StencilTree> resultRules = layerDef.findAllDescendants(RULES_RESULT);
       List<String> setFields = new ArrayList();
       for (StencilTree resultRule: resultRules) {
           if (resultRule.getChildCount() ==0) {continue;}
           TargetTuple fields = Freezer.targetTuple(resultRule.findDescendant(TARGET_TUPLE));
           for (TupleField field: fields) {setFields.add(field.toString());}
       }
       
       
       //Mark schema fields that are not modified as constant
       TuplePrototype<SchemaFieldDef> proto = dl.prototype();
       SchemaFieldDef[] maybeConstants = stencil.util.collections.ArrayUtil.fromIterator(proto, new SchemaFieldDef[proto.size()]);
       for (int i=0; i< maybeConstants.length; i++) {
           SchemaFieldDef def = maybeConstants[i];
           if (!setFields.contains(def.name())) {
               maybeConstants[i] = new SchemaFieldDef(def.name(), def.defaultValue(), def.type(), true);
           }
       }
       dl.updatePrototype(new TuplePrototype(maybeConstants));
     } catch (Exception e) {
        throw new RuntimeException("Error flaging constant fields.", e);
     }
   }

}

liftShared: ^(LAYER spec=. guides=. defaultList=. consumes=.)
  -> ^(LAYER $spec $guides {augmentDefaults((StencilTree) defaultList, (StencilTree) consumes)} {reduceConstants((StencilTree) consumes)});
	
updateLayers: ^(l=LAYER .*) {updateLayer(l);};
flagConstantFields: ^(l=LAYER .*) {flagConstFields(l);};