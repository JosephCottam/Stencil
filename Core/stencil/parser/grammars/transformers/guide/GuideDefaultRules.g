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
   import stencil.parser.string.util.EnvironmentProxy;

   import static stencil.parser.ParserConstants.BIND_OPERATOR;	
}

@members {
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
    
   private static final StencilTree OUTPUT_RULE;
   private static final StencilTree INPUT_RULE;
   private static final String OUTPUT_FIELD = "Output";
   private static final String INPUT_FIELD = "Input";
   private static final String X_FIELD = "X";
   private static final String Y_FIELD = "Y";
   private static final String ID_FIELD = "ID";
   private static final String TEXT_FIELD = "TEXT";
   
   static {
       OUTPUT_RULE = parseRule(OUTPUT_FIELD + BIND_OPERATOR + OUTPUT_FIELD);
       INPUT_RULE = parseRule(INPUT_FIELD + BIND_OPERATOR + INPUT_FIELD);
   }
   
   private static final StencilTree parseRule(String input) {
      ANTLRStringStream input1 = new ANTLRStringStream(input);
      StencilLexer lexer = new StencilLexer(input1);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      StencilParser parser = new StencilParser(tokens);
      parser.setTreeAdaptor(ParseStencil.TREE_ADAPTOR);
      
      try {
         return (StencilTree) parser.rule("result").getTree();
      } catch (Exception e) {
         throw new Error("Error constructing default rule for guides.",e);
      }
   }
    
   
   public StencilTree reform(StencilTree g) {
     StencilTree rules = g.find(LIST_RULES);
     TuplePrototype p = EnvironmentProxy.calcPrototype(rules);
     List<String> names = Arrays.asList(TuplePrototypes.getNames(p));

     String guideType = g.find(ID).getText();
     if (guideType.equals("pointLabels")) {
        if (!names.contains(X_FIELD)) {
           StencilTree r = parseRule(X_FIELD + BIND_OPERATOR + X_FIELD); 
           adaptor.addChild(rules, r);
        }
        if (!names.contains(Y_FIELD)) {
           StencilTree r = parseRule(Y_FIELD + BIND_OPERATOR + Y_FIELD); 
           adaptor.addChild(rules, r);
        }
        if (!names.contains(TEXT_FIELD)) {
           StencilTree r= parseRule(TEXT_FIELD + BIND_OPERATOR + ID_FIELD);
           adaptor.addChild(rules, r);
        }
     } else if (guideType.equals("trend")) {
        if (!names.contains(X_FIELD)) {
           StencilTree r = parseRule(X_FIELD + BIND_OPERATOR + X_FIELD); 
           adaptor.addChild(rules, r);
        }
        if (!names.contains(Y_FIELD)) {
           StencilTree r = parseRule(Y_FIELD + BIND_OPERATOR + Y_FIELD); 
           adaptor.addChild(rules, r);
        }        
     } else {          
	     if (!names.contains(OUTPUT_FIELD)) {
	        adaptor.addChild(rules, adaptor.dupTree(OUTPUT_RULE));   
	     }
	     
	     if (!names.contains(INPUT_FIELD)) {
	        adaptor.addChild(rules, adaptor.dupTree(INPUT_RULE));
	     }
     }
          
     return (StencilTree) adaptor.dupTree(g);   
   }
}

topdown: ^(g=GUIDE .*) -> {reform(g)};
