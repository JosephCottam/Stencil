tree grammar GuideAdoptLayerDefaults;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
/** Take the layer defaults and apply them to guide formatting (if appropriate).
 **  TODO: Can this work for anything other than legend/crossLegend?
 **/

  package stencil.parser.string;
  
  import stencil.parser.tree.StencilTree;
  import stencil.interpreter.tree.TupleField;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.TreeRewriteSequence;
  import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;
}

@members{
   public static StencilTree apply (StencilTree t) {
     return (StencilTree) TreeRewriteSequence.apply(t);
  }
  
  private StencilTree adoptConsts(StencilTree guide) {
     StencilTree defaults = guide.getAncestor(LAYER).find(RULES_DEFAULTS);
     if (defaults.getChildCount() ==0) {return (StencilTree) adaptor.dupTree(guide);}
     
     guide = (StencilTree) adaptor.dupTree(guide);
     StencilTree guideRules = guide.find(LIST_RULES);
     
     for (StencilTree defRule: defaults) {
        StencilTree candidate = modRule(defRule);
        if (!useRule(candidate, guideRules)) {continue;} 
        adaptor.addChild(guideRules, candidate);
     }
     return guide;
  }
  
  
  /**Should the candidate guide-default be used?  
     Reasons not to include an existing rule in the guide or setting properties that would otherwise cause problems later.
  */
  private boolean useRule(StencilTree candidate, StencilTree guideRules) {
    TupleField target = Freezer.tupleField(candidate.findDescendant(TARGET_TUPLE).getChild(0));
    
    for (StencilTree tt : guideRules.findAllDescendants(TARGET_TUPLE)) {
        for (StencilTree field: tt) {
            TupleField guideField = Freezer.tupleField(field);
            if (guideField.equals(target)) {return false;}
        }
    }

    
    //TODO: Can these restrictions be removed?      (uses parts(1) because parts(0) should be GUIDE_ELEMENT_TAG for all of them)    
    return !target.parts(1).equals("REGISTRATION")
            && ! target.parts(1).equals("X")
            && ! target.parts(1).equals("Y");
  }


  /**Make the rule target the example properties.*/
  private StencilTree modRule(StencilTree rule) {
     rule = (StencilTree) adaptor.dupTree(rule);
     StencilTree target = rule.findDescendant(TARGET_TUPLE).getChild(0);
     StencilTree backup = (StencilTree) adaptor.dupTree(target);

     for (int i=0; i< target.getChildCount(); i++) {target.deleteChild(i);}
     adaptor.addChild(target, adaptor.create(ID, GUIDE_ELEMENT_TAG));
     for (int i=0; i< backup.getChildCount(); i++) {adaptor.addChild(target, adaptor.dupTree(backup.getChild(i)));}
     return rule;
  }
}

topdown: ^(g=GUIDE type=. .*) {$type.getText().toLowerCase().contains("legend")}? -> {adoptConsts($g)};
