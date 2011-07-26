tree grammar GuideDefaultRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Ensures that an Input and Output rule exist in the guide declaration.
   * If either is missing, one is inserted which copies the source values
   * to the results.
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
       
       for (StencilTree s: g.find(LIST_GUIDE_MONITORS)) {
           String inputField = INPUT_FIELD + (s.getChildIndex() > 0 ? s.getChildIndex() : "");
            if (!names.contains(inputField)) {
               adaptor.addChild(rules, ParseStencil.ruleTree(inputField, inputField));  //Make the sample inputs accessible
            }
       }
     }
     
     //TODO: Add sequence field so, for example, legend layouts can be computed in rules
     //if (!names.contains(SEQUENCE_FIELD)) {adaptor.addChild(rules, ParseStencil.ruleTree(SEQUENCE_FIELD, SEQUENCE_FIELD));}
          
     return (StencilTree) adaptor.dupTree(g);   
   }
}

topdown: ^(g=GUIDE .*) -> {reform(g)};