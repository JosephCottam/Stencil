tree grammar FillCoConsumes;

options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
  /** Given a layer, ensures all consumes blocks produce a tuple with the same prototype.
   ** This enables updates to run smoothly even when there are updates from multiple consumes block
   ** in the same update batch.
   **
   **  This is accomplished by adding rules from attributes to a sentinel "NO_UPDATE".
   **  These rules are added to each block for each attribute set in some other block.
   ** 
   **  Assumes a relatively-late, but pre-combineRules structure to the grammar.
   **/
   package stencil.parser.string;
	
   import stencil.parser.tree.*;
   import stencil.parser.string.util.*;
   import stencil.tuple.prototype.TuplePrototype;
   import stencil.tuple.prototype.TupleFieldDef;
   import stencil.parser.ProgramCompileException;
   import stencil.parser.ParseStencil;
   import stencil.parser.string.util.TreeRewriteSequence;
   
   import java.util.Set;
   import java.util.HashSet;
}

@members {  
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public StencilTree augment(StencilTree rules) {
     assert rules.is(RULES_RESULT);
          
     StencilTree layer = rules.getAncestor(LAYER);
     StencilTree allConsumes = rules.getAncestor(LIST_CONSUMES); 
     if (layer == null
          || allConsumes == null
          || allConsumes.getChildCount() ==1) {return (StencilTree) adaptor.dupTree(rules);} //No changes to be made

     rules = (StencilTree) adaptor.dupTree(rules);

	 Set<String> fields = gatherFields(allConsumes);      
	 TuplePrototype<TupleFieldDef> proto = EnvironmentUtil.calcPrototype(rules);
	 
	 for (TupleFieldDef field: proto) {fields.remove(field.name());}
	 for (String field: fields) {
	     StencilTree noChangeRule = FillCoConsumes.noChange(rules, field, adaptor);
	     rules.addChild(noChangeRule);
	 }
	 return rules;
  }
  
  private Set<String> gatherFields(StencilTree listConsumes) {
     Set<String> fields = new HashSet();
     for (StencilTree consumes: listConsumes) {
        TuplePrototype<TupleFieldDef> proto = EnvironmentUtil.calcPrototype(consumes.find(RULES_RESULT));
        for (TupleFieldDef field: proto) {fields.add(field.name());}
     }
     return fields;
  }
  
  private static StencilTree noChange(StencilTree tree, String name, TreeAdaptor adaptor) {
      StencilTree t = ParseStencil.ruleTree(name, "1");
      StencilTree p = t.findDescendant(PACK);
      adaptor.setChild(p, 0, adaptor.create(NO_UPDATE, "NO_UPDATE"));
      return t;
  }


}
  
topdown
  : ^(rules=RULES_RESULT .+) -> {augment($rules)};