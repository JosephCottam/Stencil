tree grammar DynamicStoreSource;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
  /**Dynamic bindings require some source data to be stored with the glyph.  
   * This transformer converts the earlier created "reducer" into ToTuple call 
   and stores it with a standard. **/
  package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import static stencil.parser.ParserConstants.DYNAMIC_STORE_FIELD;
  import static stencil.parser.string.util.Utilities.genSym;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
  import static stencil.parser.ParserConstants.MAP_FACET;
  
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  private StencilTree args(StencilTree pack) {
     StencilTree args = (StencilTree) adaptor.create(LIST_ARGS, (String) null);
     for (StencilTree arg: pack) {
        adaptor.addChild(args, adaptor.dupTree(arg));
     }
     return args;
  }
}

topdown: ^(CONSUMES f=. p=. l=. results d=. dr=.) -> ^(CONSUMES $f $p $l results $d) ; //Remove dynamic reducer
results
  @init{StencilTree reducer = null;
       String frameName = genSym(FRAME_SYM_PREFIX);
  }
  : ^(rr=RULES_RESULT rules+=.*) 
    {reducer = $rr.getParent().find(DYNAMIC_REDUCER).find(PACK);}
      -> {reducer.getChildCount() >0}? 
        ^(RULES_RESULT $rules*
            ^(RULE ^(TARGET ^(TARGET_TUPLE ^(TUPLE_FIELD ID[DYNAMIC_STORE_FIELD])))
                   ^(CALL_CHAIN 
                       ^(FUNCTION ^(OP_NAME DEFAULT ID["ToTuple"] ID[MAP_FACET]) SPECIALIZER {args(reducer)} DIRECT_YIELD[frameName] ^(PACK ^(TUPLE_REF ID[frameName]))))))
      ->^(RULES_RESULT $rules*); 
