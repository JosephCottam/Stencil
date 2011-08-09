tree grammar GuideDefaultRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Ensures that an appropriate output rule exists in the guide declaration.
   * Also ensures that the sample tuples are copied directly to a field named `sample'.
   */

   package stencil.parser.string;

   import java.util.Arrays;
	
   import stencil.parser.ParseStencil;
   import stencil.parser.tree.*;
   import stencil.tuple.prototype.*;
   import stencil.tuple.prototype.TuplePrototype;
   import stencil.parser.string.util.EnvironmentUtil;
   import stencil.parser.ParseStencil;


   import static stencil.parser.ParserConstants.BIND_OPERATOR;
   import static stencil.parser.ParserConstants.IDENTIFIER_FIELD;	
   import static stencil.parser.ParserConstants.X_FIELD;
   import static stencil.parser.ParserConstants.Y_FIELD;
   import static stencil.parser.ParserConstants.TEXT_FIELD;
   import static stencil.parser.ParserConstants.INPUT_FIELD;
   import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;
   import static stencil.parser.ParserConstants.NAME_SEPARATOR;
   import static stencil.parser.ParserConstants.STREAM_FRAME;
}

@members {
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
       
   public StencilTree reform(StencilTree g) {
     StencilTree rules = g.find(LIST_RULES);
     TuplePrototype p = EnvironmentUtil.calcPrototype(rules);
     List<String> names = Arrays.asList(TuplePrototypes.getNames(p));

     String guideType = g.find(ID).getText();
     if (guideType.equals("pointLabels")) {
        if (!names.contains(X_FIELD)) { 
           adaptor.addChild(rules, ParseStencil.ruleTree(X_FIELD, X_FIELD));
        }
        if (!names.contains(Y_FIELD)) {
           adaptor.addChild(rules, ParseStencil.ruleTree(Y_FIELD, Y_FIELD));
        }
        if (!names.contains(TEXT_FIELD)) {
           adaptor.addChild(rules, ParseStencil.ruleTree(TEXT_FIELD, IDENTIFIER_FIELD));
        }
     } else if (guideType.equals("trend")) {
        if (!names.contains(X_FIELD)) {
           adaptor.addChild(rules, ParseStencil.ruleTree(X_FIELD, X_FIELD));
        }
        if (!names.contains(Y_FIELD)) {
           adaptor.addChild(rules, ParseStencil.ruleTree(Y_FIELD, Y_FIELD));
        }        
     } else {          
       for (StencilTree s: g.find(LIST_SELECTORS)) {
          String boundField = s.getText();
          String displayField = GUIDE_ELEMENT_TAG + NAME_SEPARATOR + s.getText();
          if (!names.contains(boundField)) {
             adaptor.addChild(rules, ParseStencil.ruleTree(displayField, boundField));
          }
       }
       
       adaptor.addChild(rules, ParseStencil.ruleTree(INPUT_FIELD, INPUT_FIELD));  //Make the sample input tuple accessible
     }
     
     //TODO: Add sequence field so, for example, legend layouts can be computed in rules
     //if (!names.contains(SEQUENCE_FIELD)) {adaptor.addChild(rules, ParseStencil.ruleTree(SEQUENCE_FIELD, SEQUENCE_FIELD));}
          
     return (StencilTree) adaptor.dupTree(g);   
   }
}

topdown: ^(g=GUIDE .*) -> {reform(g)};