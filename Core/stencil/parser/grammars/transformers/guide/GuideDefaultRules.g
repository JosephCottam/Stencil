tree grammar GuideDefaultRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
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

   import static stencil.parser.ParserConstants.BIND_OPERATOR;	
}

@members {
   public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
   }
    
   private static final Rule OUTPUT_RULE;
   private static final Rule INPUT_RULE;
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
   
   private static final Rule parseRule(String input) {
      ANTLRStringStream input1 = new ANTLRStringStream(input);
      StencilLexer lexer = new StencilLexer(input1);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      StencilParser parser = new StencilParser(tokens);
      parser.setTreeAdaptor(ParseStencil.TREE_ADAPTOR);
      
      try {
         return (Rule) parser.rule("result").getTree();
      } catch (Exception e) {
         throw new Error("Error constructing default rule for guides.",e);
      }
   }
    
   
   public StencilTree reform(Guide g) {
     TuplePrototype p = g.getPrototype();
     List<String> names = Arrays.asList(TuplePrototypes.getNames(p));
     List<Rule> rules = g.getRules();
     String layer = g.getSelector().getPath().get(0).getID();


     if (g.getGuideType().equals("pointLabels")) {
        if (!names.contains(X_FIELD)) {
           Rule r = parseRule(X_FIELD + BIND_OPERATOR + X_FIELD); 
           adaptor.addChild(rules, r);
        }
        if (!names.contains(Y_FIELD)) {
           Rule r = parseRule(Y_FIELD + BIND_OPERATOR + Y_FIELD); 
           adaptor.addChild(rules, r);
        }
        if (!names.contains(TEXT_FIELD)) {
           Rule r= parseRule(TEXT_FIELD + BIND_OPERATOR + ID_FIELD);
           adaptor.addChild(rules, r);
        }
     } else if (g.getGuideType().equals("trend")) {
        if (!names.contains(X_FIELD)) {
           Rule r = parseRule(X_FIELD + BIND_OPERATOR + X_FIELD); 
           adaptor.addChild(rules, r);
        }
        if (!names.contains(Y_FIELD)) {
           Rule r = parseRule(Y_FIELD + BIND_OPERATOR + Y_FIELD); 
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

topdown: ^(g=GUIDE .*) -> {reform((Guide) g)};
