tree grammar Predicate_Expand;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
	filter = true;
	output = AST;
  superClass = TreeRewriteSequence;
}

@header {
 /**Takes predicates and turns them into rules that return 
  * a True/False singleton.
  */

  package stencil.parser.string;
  import stencil.parser.tree.Program;
  import static stencil.parser.string.util.Utilities.genSym;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;  
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}

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
 : ^(PREDICATE lhs=. (patOp=RE | patOp=NRE) pattern=STRING) 
   -> ^(PREDICATE 
          ^(RULE 
             ^(RESULT ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID["RESULT"] TYPE["BOOLEAN"])))
             ^(CALL_CHAIN
                 ^(FUNCTION[opName($patOp.getText()) + ".match"] ^(SPECIALIZER ^(LIST ^(MAP_ENTRY["pattern"] STRING[$pattern.getText()]))) ^(LIST["args"] $lhs) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX] ^(PACK DEFAULT)))))

 | ^(PREDICATE lhs=. op=. rhs=.) 
   -> ^(PREDICATE 
          ^(RULE 
             ^(RESULT ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID["RESULT"] TYPE["BOOLEAN"])))
             ^(CALL_CHAIN
                 ^(FUNCTION[opName($op.getText()) + ".query"] ^(SPECIALIZER DEFAULT) ^(LIST["args"] $lhs $rhs) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX] ^(PACK DEFAULT)))))
                 
  | ^(PREDICATE ALL)
   -> ^(PREDICATE 
          ^(RULE 
             ^(RESULT ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID["RESULT"] TYPE["BOOLEAN"])))
             ^(CALL_CHAIN
                 ^(FUNCTION["TrivialTrue.query"] ^(SPECIALIZER DEFAULT) ^(LIST["args"]) DIRECT_YIELD[genSym(FRAME_SYM_PREFIX] ^(PACK DEFAULT)))))
  ;
  
  
  		