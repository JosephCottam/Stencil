tree grammar StoreValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Ensures that any target only contains references to fields valid in its context.
   **/
   

  package stencil.parser.string.validators;
  
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.string.TreeFilterSequence;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.tuple.prototype.TupleFieldDef;
  import stencil.parser.string.ValidationException;
  import static java.lang.String.format;
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.EnvironmentUtil;
  import stencil.tuple.prototype.TuplePrototypes;
  
}

@members {
  private static final class  StoreValidationException extends ValidationException {
     public StoreValidationException(String context, String name, TuplePrototype valid) {
        super("Attempt to store in to unknown field " + name + " (valid fields in '" + context + "' are " + TuplePrototypes.prettyNames(valid) + ")");
     }
  }

  public static void apply (Tree t) {
    TreeFilterSequence.apply(t);
  }

  private void layerStore(StencilTree target) {
     StencilTree layerDef = target.getAncestor(LAYER);
     
     TuplePrototype targetProto = EnvironmentUtil.calcPrototype(target);
     TuplePrototype contextProto = EnvironmentUtil.layerPrototype(layerDef);
     
     checkFields(layerDef.getText(), targetProto, contextProto);
  }
  
  private void streamStore(StencilTree target) {
     StencilTree streamDef = target.getAncestor(STREAM_DEF);
     TuplePrototype targetProto = EnvironmentUtil.calcPrototype(target);
     TuplePrototype contextProto = Freezer.prototype(streamDef.find(TUPLE_PROTOTYPE));               
     checkFields(streamDef.getText(), targetProto, contextProto);
  }
  
  private void operatorStore(StencilTree target) {
     StencilTree context = target.getAncestor(OPERATOR_FACET);
     TuplePrototype contextProto = Freezer.prototype(context.find(YIELDS).getChild(1));
     TuplePrototype targetProto = EnvironmentUtil.calcPrototype(target);
     checkFields(context.getParent().getText(), targetProto, contextProto);
  }

  private void checkFields(String contextID, TuplePrototype<TupleFieldDef> target, TuplePrototype context) {
      for (TupleFieldDef def: target) {
         if (!context.contains(def.name())) {throw new StoreValidationException(contextID, def.name(),context);}
      }
  }
  
}

topdown
    : ^(rr=RULES_RESULT .*)   {if (rr.getAncestor(LAYER) != null) {layerStore(rr);} else if (rr.getAncestor(STREAM_DEF) != null) {streamStore(rr);}}  //TODO: verify view and canvas as well
    | ^(rd=RULES_DEFAULT .*)  {layerStore(rd);}
    | ^(lr=LIST_RULES .*)     {if (lr.getAncestor(OPERATOR) != null) {operatorStore(lr);}};  //TODO: verify guide stores as well
    
