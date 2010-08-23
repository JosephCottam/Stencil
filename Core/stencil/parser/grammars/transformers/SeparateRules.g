tree grammar SeparateRules;
options {
    tokenVocab = Stencil;
    ASTLabelType = CommonTree;	
    output = AST;
    filter = true;
}

@header {
/** Takes layer rules and separates them by target type.
 */

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import static stencil.parser.string.StencilParser.*;
}

@members {

   private StencilTree siftRules(List<Rule> rules, int type) {return siftRules(adaptor, rules, type, -1, null);}
 
   //This binding check will be a problem when animated bindings come into play
   public static StencilTree siftRules(TreeAdaptor adaptor, List<Rule> rules, int type, int binding, String label) {
      label = label != null ? label : StencilParser.tokenNames[type] ;
      StencilTree list = (StencilTree) adaptor.create(LIST, label);
      
      for(Rule r: rules) {
         if(r.getGenericTarget().getType() == type) {
            if (binding < 0 || r.getBinding().getType() == binding) {
              adaptor.addChild(list, adaptor.dupTree(r));
            }
         }
      }
      return list;
   }

   protected StencilTree local(CommonTree source)         {return siftRules((List<Rule>) source, LOCAL);}   
   protected StencilTree canvas(CommonTree source)        {return siftRules((List<Rule>) source, CANVAS);}
   protected StencilTree view(CommonTree source)          {return siftRules((List<Rule>) source, VIEW);}
   protected StencilTree prefilter(CommonTree source)     {return siftRules((List<Rule>) source, PREFILTER);}
   protected StencilTree result(CommonTree source)        {return siftRules((List<Rule>) source, RESULT);}
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