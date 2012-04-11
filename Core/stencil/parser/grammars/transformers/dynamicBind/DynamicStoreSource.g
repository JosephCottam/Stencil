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
	
  import stencil.module.ModuleCache;
  import stencil.parser.tree.*;
  import stencil.parser.string.util.TreeRewriteSequence;
  import static stencil.parser.ParserConstants.DYNAMIC_STORE_FIELD;
  import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
  import static stencil.parser.string.util.Utilities.genSym;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
  import stencil.parser.string.util.Utilities;
}

@members {
  protected ModuleCache modules;

  public static StencilTree apply (StencilTree t, ModuleCache modules) {
    return (StencilTree) TreeRewriteSequence.apply(t, modules);
   }
   
  protected void setup(Object... args) {this.modules = (ModuleCache) args[0];}
  
  private StencilTree args(StencilTree pack) {
     StencilTree args = (StencilTree) adaptor.create(LIST_ARGS, (String) null);
     for (StencilTree arg: pack) {
        adaptor.addChild(args, adaptor.dupTree(arg));
     }
     return args;
  }
  
  public String addOperator(StencilTree in) {
     return Utilities.addOperator("ToTuple", EMPTY_SPECIALIZER, modules, in, adaptor);
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
                       ^(FUNCTION ^(OP_NAME DEFAULT ID[addOperator($rr)] DEFAULT_FACET) SPECIALIZER {args(reducer)} DIRECT_YIELD[frameName] ^(PACK ^(TUPLE_REF ID[frameName]))))))
      ->^(RULES_RESULT $rules*); 
