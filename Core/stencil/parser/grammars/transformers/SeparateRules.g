    tree grammar SeparateRules;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree;	
    output = AST;
    filter = true;
    superClass = TreeRewriteSequence;
}

@header {
/** Takes layer rules and separates them by target type,
 *  then removes the individual target types.
 */

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
   private StencilTree siftRules(StencilTree rules, int type, int listType) {return siftRules(adaptor, rules, type, listType, -1);}
 
   //TODO: This binding check will be a problem when animated bindings come into play
   /**Create a new collection of rules from an old collection of rules.
    * If a rule's binding is of the given type, 
    *    AND its binding is less than zero or of the given type 
    *    THEN a new rule is made.  
    * All newly create rules are made part of a list of type listType.
    * This new list is the return value.
    **/  
   public static StencilTree siftRules(TreeAdaptor adaptor, StencilTree rules, int type, int listType, int binding) {
      StencilTree list = (StencilTree) adaptor.create(listType, StencilTree.typeName(listType));
      
      for(StencilTree rule: rules) {
         if(rule.find(type) != null) {
            if (binding < 0 || rule.find(DEFINE, DYNAMIC, ANIMATED,ANIMATED_DYNAMIC).getType() == binding) {
              adaptor.addChild(list, adaptor.dupTree(rule));
            }
         }
      }
      return list;
   }

   protected StencilTree local(StencilTree source)         {return siftRules(source, LOCAL, RULES_LOCAL);}   
   protected StencilTree prefilter(StencilTree source)     {return siftRules(source, PREFILTER, RULES_PREFILTER);}
   protected StencilTree result(StencilTree source)        {return siftRules(source, RESULT, RULES_RESULT);}
}

//Put things in blocks based on their type
topdown: ^(CONSUMES filters=. rules=.) 
		-> ^(CONSUMES 
              $filters 
              {prefilter(rules)} 
              {local(rules)} 
              {result(rules)});
              
              
//Remove individual target types, target type is no identified by grouping type
bottomup
    : ^(LOCAL s+=.*)     -> ^(TARGET["Local"] $s*)
    | ^(PREFILTER s+=.*) -> ^(TARGET["Prefilter"] $s*)
    | ^(RESULT s+=.*)    -> ^(TARGET["Result"] $s*);