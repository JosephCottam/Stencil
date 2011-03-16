tree grammar GuideAutoLabel;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
/** Determines the names of fields being used to create a guide
 *  and transfers them to the label attribute (if one was not already supplied).
 *  
 *  Guide declarations are identified; all those without a `gLabel' parameter have one added that includes
 *  the names of variables passed to the Monitor operator.
 * 
 **/

  package stencil.parser.string;
  
  import stencil.parser.tree.*;
  import stencil.interpreter.guide.MonitorOperator;
  import stencil.interpreter.tree.Freezer;
  
  import static stencil.parser.ParserConstants.GUIDE_LABEL;
  import static stencil.parser.ParserConstants.INVOKEABLE;  
}

@members{
   public static StencilTree apply (StencilTree t) {
     return (StencilTree) TreeRewriteSequence.apply(t);
  }
  
  private static String getSources(StencilTree sel) {
    StencilTree p = sel.getAncestor(PROGRAM);
    String layerName = sel.getChild(0).getText();
    String att = sel.getChild(1).getText();

    StencilTree l = p.find(LIST_LAYERS).find(LAYER, layerName);
    StencilTree c = l.find(LIST_CONSUMES).getChild(0); //HACK: The zero-reference is a hack...Should I restrict layers to only one consumes block?
               
    StencilTree r= null;
    for (StencilTree r2: c.find(RULES_RESULT)) {
       if (Freezer.prototype(r2.find(RESULT).find(TUPLE_PROTOTYPE)).contains(att)) {r=r2; break;}
    }
    assert r != null : "Guide path did not match any rule.";
           
    StencilTree t = r.find(CALL_CHAIN).find(FUNCTION, PACK);
    while (t.getType() == FUNCTION) {
      AstInvokeable target = t.find(INVOKEABLE);
      if (target != null && target.getOperator() instanceof MonitorOperator) {
        return t.find(LIST_ARGS).find(TUPLE_REF).getChild(1).getText(); //get child 1 because this is a tuple ref and it has been framed
      }
      t = t.find(FUNCTION, PACK);
   }
   throw new Error("Guide path did not lead to location with monitor operator");
  }
  
  private boolean needsLabel(StencilTree spec) {
     return !Freezer.specializer(spec).containsKey(GUIDE_LABEL);
  }
}

topdown:
     ^(s=SPECIALIZER entries+=.*)
      -> {(s.getParent().getType() == GUIDE) && (s.getAncestor(GUIDE_DIRECT) != null) && needsLabel($s)}?  //Is this the guide specializer for a direct guide? 
            ^(SPECIALIZER ^(MAP_ENTRY[GUIDE_LABEL] STRING[getSources($s.getParent().find(SELECTOR))]) $entries*)
      -> ^(SPECIALIZER $entries*);   