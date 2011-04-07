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
  
  import stencil.parser.tree.*;
  import stencil.interpreter.guide.MonitorOperator;
  import stencil.interpreter.tree.Freezer;

  import static stencil.parser.ParserConstants.*;
}

@members{
   public static StencilTree apply (StencilTree t) {
     return (StencilTree) TreeRewriteSequence.apply(t);
  }
  
  private StencilTree adoptConsts(StencilTree guide) {
     StencilTree defaults = guide.getAncestor(LAYER).find(RULES_DEFAULTS);
     if (defaults.getChildCount() ==0) {return (StencilTree) adaptor.dupTree(guide);}
     
     guide = (StencilTree) adaptor.dupTree(guide);
     StencilTree rules = guide.find(LIST_RULES);
     
     for (StencilTree rule: defaults) {
        if (!useRule(rule)) {continue;} 
        adaptor.addChild(rules, modRule(rule));
     }
     return guide;
  }
  
  
  //TODO: Simplify this process by calculating guide defaults only once (like layer defaults)
  private boolean useRule(StencilTree rule) {
    StencilTree target = rule.findDescendant(TUPLE_FIELD_DEF).getChild(0);
    return !target.getText().equals("REGISTRATION")
            && ! target.getText().equals("X")
            && ! target.getText().equals("Y");
  }

  /**Make the rule target the example properties.*/
  private StencilTree modRule(StencilTree rule) {
     rule = (StencilTree) adaptor.dupTree(rule);
     StencilTree target = rule.findDescendant(TUPLE_FIELD_DEF).getChild(0);
     target.token.setText(GUIDE_ELEMENT_TAG + NAME_SEPARATOR + target.getText());
     return rule;
  }
}

topdown: ^(g=GUIDE type=. .*) {$type.getText().toLowerCase().contains("legend")}? -> {adoptConsts($g)};
