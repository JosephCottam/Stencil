tree grammar Predicate_Expand;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
	filter = true;
	output = AST;
  superClass = TreeRewriteSequence;
}

@header {
 /**Takes predicates and turns them into rules that return 
  * a True/False singleton.
  */

  package stencil.parser.string;
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.TreeRewriteSequence;
  import static stencil.parser.string.util.Utilities.genSym;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;  
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

   /**Converts the operator from a symbol to a regular name.*/
   public static String opName(String opSymbol) {
      if (opSymbol.equals("=")) {return "EQ";}
      if (opSymbol.equals("!=")) {return "NEQ";}
      if (opSymbol.equals(">")) {return "GT";}
      if (opSymbol.equals(">=")) {return "GTE";}
      if (opSymbol.equals("<")) {return "LT";}
      if (opSymbol.equals("<=")) {return "LTE";}
      if (opSymbol.equals("=~")) {return "RE";}
      if (opSymbol.equals("!~")) {return "NRE";}
      throw new RuntimeException("Could not find comparison operator name for " + opSymbol);
   }
}
 
topdown              
  : ^(PREDICATE ALL)
   -> ^(PREDICATE 
          ^(RULE 
             ^(TARGET ^(TARGET_TUPLE ^(TUPLE_FIELD ID["RESULT"])))
             ^(CALL_CHAIN
                 ^(FUNCTION
                      ^(OP_NAME DEFAULT ID["TrivialTrue"] ID["query"])  
                      ^(SPECIALIZER DEFAULT) 
                      LIST_ARGS 
                      DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)] 
                      ^(PACK DEFAULT))))) 
                      
 | ^(PREDICATE lhs=. (patOp=RE | patOp=NRE) pattern=STRING) 
   -> ^(PREDICATE 
          ^(RULE 
            ^(RESULT ^(TARGET_TUPLE ^(TUPLE_FIELD ID["RESULT"])))
             ^(CALL_CHAIN
                 ^(FUNCTION
                    ^(OP_NAME DEFAULT ID[opName($patOp.getText())] ID["match"]) 
                    ^(SPECIALIZER ^(MAP_ENTRY["pattern"] STRING[$pattern.getText()]))
                    ^(LIST_ARGS $lhs) 
                    DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)] 
                    ^(PACK DEFAULT)))))

 | ^(PREDICATE lhs=. op=. rhs=.) 
   -> ^(PREDICATE 
          ^(RULE 
             ^(RESULT ^(TARGET_TUPLE ^(TUPLE_FIELD ID["RESULT"])))
             ^(CALL_CHAIN
                 ^(FUNCTION
                      ^(OP_NAME DEFAULT ID[opName($op.getText())] ID["query"])
                      ^(SPECIALIZER DEFAULT) 
                      ^(LIST_ARGS $lhs $rhs) 
                      DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)]
                      ^(PACK DEFAULT)))))

  ;
  
  
  		