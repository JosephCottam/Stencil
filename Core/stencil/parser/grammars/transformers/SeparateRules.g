tree grammar SeparateRules;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree;	
    output = AST;
    filter = true;
    superClass = TreeRewriteSequence;
}

@header {
/** Takes layer rules and separates them by target type.
 */

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
   private StencilTree siftRules(StencilTree rules, int type, int listType) {return siftRules(adaptor, rules, type, listType, -1);}
 
   //This binding check will be a problem when animated bindings come into play
   public static StencilTree siftRules(TreeAdaptor adaptor, StencilTree rules, int type, int listType, int binding) {
      StencilTree list = (StencilTree) adaptor.create(listType, StencilTree.typeName(listType));
      
      for(StencilTree rule: rules) {
         if(rule.find(TARGET, RESULT, VIEW, CANVAS, LOCAL, PREFILTER).getType() == type) {
            if (binding < 0 || rule.find(DEFINE, DYNAMIC, ANIMATED,ANIMATED_DYNAMIC).getType() == binding) {
              adaptor.addChild(list, adaptor.dupTree(rule));
            }
         }
      }
      return list;
   }

   protected StencilTree local(StencilTree source)         {return siftRules(source, LOCAL, RULES_LOCAL);}   
   protected StencilTree canvas(StencilTree source)        {return siftRules(source, CANVAS, RULES_CANVAS);}
   protected StencilTree view(StencilTree source)          {return siftRules(source, VIEW, RULES_VIEW);}
   protected StencilTree prefilter(StencilTree source)     {return siftRules(source, PREFILTER, RULES_PREFILTER);}
   protected StencilTree result(StencilTree source)        {return siftRules(source, RESULT, RULES_RESULT);}
}

//Put things in blocks based on their type
topdown: ^(CONSUMES filters=. rules=.) 
		-> ^(CONSUMES 
              $filters 
              {prefilter(rules)} 
              {local(rules)} 
              {result(rules)} 
              {view(rules)} 
              {canvas(rules)});