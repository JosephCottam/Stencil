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
 *  Guide declarations are identified; all those without a `guideLabel' parameter have one added that includes
 *  the names of variables passed to the Monitor operator.
 * 
 **/

  package stencil.parser.string;
  
  import stencil.parser.ProgramCompileException;
  import stencil.parser.tree.*;
  import stencil.interpreter.guide.MonitorOperator;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.string.util.EnvironmentUtil;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.module.operator.StencilOperator;

  import static stencil.parser.string.util.Utilities.findOperator;
  import static stencil.parser.ParserConstants.GUIDE_LABEL;
}

@members{
   private static final class SelectorException extends RuntimeException {
      public SelectorException(String message) {super(message);}
   }

   public static StencilTree apply (StencilTree t) {
     return (StencilTree) TreeRewriteSequence.apply(t);
  }
  
  private static String buildLabel(StencilTree selectors) {
    StencilTree program = selectors.getAncestor(PROGRAM);
    String layerName = selectors.getAncestor(LAYER).getText();
    
    List<String> labels = new ArrayList();
    for (StencilTree sel: selectors) {
       String att;
       try {att= getSource(program, layerName, sel.getText());}
       catch (SelectorException e) {att = sel.getText();} 	//If there is no more info, just use the selected field
       
       if (!labels.contains(att)) {labels.add(att);}
    }
    
    StringBuilder label = new StringBuilder(labels.get(0));
    for (int i=1; i<labels.size(); i++) {label.append(" & "); label.append(labels.get(i));}
    return label.toString();
  }
  
  private static String getSource(StencilTree program, String layerName, String att) {
    StencilTree layer = program.find(LIST_LAYERS).find(LAYER, layerName);
    StencilTree consumes = layer.find(LIST_CONSUMES).getChild(0); //HACK: The zero-reference is a hack...Should I restrict layers to only one consumes block?
               
    StencilTree rule= null;
    for (StencilTree r2: consumes.find(RULES_RESULT)) {
       if (Freezer.targetTuple(r2.find(TARGET).find(TARGET_TUPLE)).contains(att)) {rule=r2; break;}
    }
    if (rule==null) {throw new SelectorException("Guide request did not match any (non-constant) rule.");}
           
    StencilTree tail = rule.find(CALL_CHAIN).find(FUNCTION, PACK);
    StencilOperator operator=null; 
    
    while (tail.getType() == FUNCTION) {
      operator = findOperator(tail.find(OP_NAME));
      if (operator instanceof MonitorOperator) {break;}
      tail = tail.find(FUNCTION, PACK);
    }


    if (operator == null || !(operator instanceof MonitorOperator)) {
       throw new SelectorException("Guide path did not lead to location with monitor operator");
    }
   
   
    StencilTree tupleRef = tail.find(LIST_ARGS).find(TUPLE_REF);
    StencilTree targetRef = rule.findDescendant(TARGET_TUPLE).find(TUPLE_FIELD);
    if (tupleRef!=null) {
        StencilTree fieldRef = tupleRef.getChild(0);
        
        if (tupleRef.getChildCount() >1) {
            fieldRef = tupleRef.getChild(1);
        }
        
        if (fieldRef.getType() ==ID) {
            return fieldRef.getText();
        } else {
           int fieldIdx = Integer.parseInt(fieldRef.getText());                              //If it is a number, resolve it
           TuplePrototype proto = EnvironmentUtil.framePrototypeFor(tupleRef);
           return proto.get(fieldIdx).name();
        }
     } else {   //Constants guide
        if (targetRef == null) {throw new SelectorException("Guide path did not lead to a valid data or constant location.");}
        return targetRef.getText();
     } 
  }
  
  private boolean needsLabel(StencilTree spec) {
     return !Freezer.specializer(spec).containsKey(GUIDE_LABEL);
  }
}

topdown:
     ^(s=SPECIALIZER entries+=.*)
      -> {(s.getParent().getType() == GUIDE && needsLabel($s))}?   
            ^(SPECIALIZER ^(MAP_ENTRY[GUIDE_LABEL] STRING[buildLabel($s.getParent().find(LIST_SELECTORS))]) $entries*)
      -> ^(SPECIALIZER $entries*);   