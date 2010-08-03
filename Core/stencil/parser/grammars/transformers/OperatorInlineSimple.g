tree grammar OperatorInlineSimple;
options {
    tokenVocab = Stencil;
    ASTLabelType = CommonTree;	
    superClass = TreeRewriteSequence;
    output = AST;
    filter = true;
}

@header {
	/**Moves synthetic operators that only have one branch (the ALL branch)
	 * up to their call site.  Requires that the OperatorExplicit has already
	 * been run to ensure that state sharing is still correct.
	 **/

	package stencil.parser.string;

	import stencil.parser.ParseStencil;
	import stencil.parser.tree.*;
    import stencil.tuple.prototype.TuplePrototypes;
}


@members {	
   private final Map<String, StencilTree> simple = new HashMap();

  public Object transform(Object t) {
    t = downup(t, this, "search");
    t = downup(t, this, "replace");
    return t;
  }   
  
  
   private Tree replaceRef(String refName, StencilTree simpleOpRef) {
      StencilTree op = simple.get(refName);
      StencilTree core = (StencilTree) adaptor.dupTree(getCoreCall(op));
      StencilTree target = simpleOpRef.findChild(FUNCTION);
      if (target == null) {target =simpleOpRef.findChild(PACK);}
      Tree pass = simpleOpRef.getChild(2);
      
      StencilTree splice = makeSplice(simpleOpRef.findChild(LIST), pass, op, core);
      StencilTree tail = findTail(splice);
      
      adaptor.setChild(tail.getParent(), tail.getChildIndex(), adaptor.dupTree(target));
      
      return splice;
   }
   
   
   /**Creates a rename before and after the core.  
    *  The rename before is built as: take the inArgs and pass them as formals to the rename.  The specializer is the input prototype of the operator.
    *  The rename after  is built as: take the pack from the core and use its args as the formals to the rename.  The specializer is the output prototype of the operator.  
    */   
   private StencilTree makeSplice(StencilTree inArgs, Tree pass, StencilTree op, StencilTree core) {
      try {
         StencilTree workingCore = (StencilTree) adaptor.dupTree(core);
         StencilTree pack = findTail(workingCore);
         String input = prototypeNames(op.findChild(YIELDS).getChild(0));
         String output =prototypeNames(op.findChild(YIELDS).getChild(1));

         StencilTree preRename = (StencilTree) adaptor.create(FUNCTION, "Rename.map");
         Tree preSpec = ParseStencil.parseSpecializer("[names: \"" + input + "\"]");
         adaptor.addChild(preRename, preSpec);
         adaptor.addChild(preRename, adaptor.dupTree(inArgs));
         adaptor.addChild(preRename, adaptor.create(DIRECT_YIELD,""));
         adaptor.addChild(preRename, workingCore);
	  
         Tree postRename = (Tree) adaptor.create(FUNCTION, "Rename.map");
         Tree postSpec = ParseStencil.parseSpecializer("[names: \"" + output + "\"]");
         adaptor.addChild(postRename, postSpec);
         adaptor.addChild(postRename, adaptor.dupTree(toList(pack)));
         adaptor.addChild(postRename, adaptor.dupNode(pass));
         adaptor.addChild(postRename, adaptor.create(PACK, ""));
	  
         adaptor.setChild(pack.getParent(), pack.getChildIndex(), postRename);
	  
         return preRename;
      } catch (Exception e) {
         throw new RuntimeException("Exception in-lining simple operator.", e);
      }    
   }
   
   
   private List toList(Tree source) {
       List list = (List) adaptor.create(LIST, "args");
       for (int i=0; i<source.getChildCount(); i++) {
          adaptor.addChild(list, adaptor.dupTree(source.getChild(i)));
       }
       return list;
   }
   
   private String prototypeNames(Tree prototype) {
      String[] names = TuplePrototypes.getNames((TuplePrototype) prototype);
      StringBuilder b = new StringBuilder();
      for (String name: names) {
         b.append(name);
         b.append(",");
      }
      b.replace(b.length()-1, b.length(), "");
      return b.toString();
   }
   
   private StencilTree findTail(StencilTree head) {
     while (head != null) {
        StencilTree tail = head.findChild(PACK);
        if (tail == null) {head = head.findChild(FUNCTION);}
        else {return (StencilTree) tail;}
     }
     throw new Error("Could not find splice tail when required...");
   }
      
   private String getName(Object t) {
      return ((Tree) t).getText().substring(0, ((Tree) t).getText().indexOf("."));
   }
   
   private StencilTree getCoreCall(StencilTree operator) {
      return (StencilTree) operator.getChild(2).getChild(0).getChild(1).getChild(0).getChild(1).getChild(0);
   }
}

//Identify operators that only have one branch
search: ^(OPERATOR . LIST opRules);				    //This rule  ensures there are no pre-filters (part of being in-line-able)
opRules: ^(LIST ^(OPERATOR_RULE ^(LIST pred=predicate) ^(LIST .)))   //This step ensures there is only one rule (part of being in-line-able)  TODO: Inline multiple rules
  {if ($pred.pred.getText().startsWith("#TrivialTrue")) {
     Tree op = pred.tree.getAncestor(OPERATOR);
     simple.put(op.getText(), (StencilTree) op);
  }};

predicate returns [Tree pred]: ^(PREDICATE ^(RULE . ^(CALL_CHAIN f=.))) {$pred = $f;};


replace: ^(cc=CALL_CHAIN chain[cc]);
chain[Tree prior]
  : ^(f=FUNCTION spec=. args=. pass=. target=chain[f])
      -> {simple.containsKey(getName($f))}?  {replaceRef(getName($f), (StencilTree) $f)}
      -> 									^(FUNCTION $spec $args $pass $target)
  | ^(PACK .*);