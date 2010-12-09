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
  import stencil.parser.tree.util.MultiPartName;
  import stencil.tuple.prototype.TuplePrototypes;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
  import static stencil.parser.string.util.Utilities.genSym;
  
  import java.util.HashMap;
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
    
  public Object downup(Object t) {
     simple.clear();
     downup(t, this, "search");
     downup(t, this, "replace");
     return t;
  }  
  
   protected final Map<String, StencilTree> simple = new HashMap();

   private Tree replaceRef(Function simpleOpRef) {
      String refName = getName(simpleOpRef);
      String facetName = getFacet(simpleOpRef);
      
      Operator op = (Operator) simple.get(refName);
      OperatorFacet facet =  op.getFacet(facetName);
      Function core = (Function) adaptor.dupTree(getCoreCall(facet));
      
      StencilTree target = simpleOpRef.findChild(FUNCTION);
      if (target == null) {target =simpleOpRef.findChild(PACK);}
      Tree pass = simpleOpRef.getChild(2);
      
      StencilTree splice = makeSplice(simpleOpRef.findChild(LIST), pass, facet, core);
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
         String inputFrameName = genSym(FRAME_SYM_PREFIX);
         Map<TupleRef, TupleRef> subst = buildSubst(inputFrameName, pass, (List) inArgs);

         StencilTree preRename = (StencilTree) adaptor.create(FUNCTION, "Rename.map");
         Tree preSpec = ParseStencil.parseSpecializer("[names: \"" + input + "\"]");
         adaptor.addChild(preRename, preSpec);
         adaptor.addChild(preRename, adaptor.dupTree(inArgs));
         adaptor.addChild(preRename, adaptor.create(DIRECT_YIELD,inputFrameName));
         adaptor.addChild(preRename, ReplaceTupleRefs.apply(workingCore, subst));
         
         Tree postRename = (Tree) adaptor.create(FUNCTION, "Rename.map");
         Tree postSpec = ParseStencil.parseSpecializer("[names: \"" + output + "\"]");
         adaptor.addChild(postRename, postSpec);
         adaptor.addChild(postRename, adaptor.dupTree(toList(pack)));
         adaptor.addChild(postRename, adaptor.dupNode(pass));
         adaptor.addChild(postRename, adaptor.create(PACK, "PACK"));
	  
         adaptor.setChild(pack.getParent(), pack.getChildIndex(), postRename);
	  
         return preRename;
      } catch (Exception e) {
         throw new RuntimeException("Exception in-lining simple operator.", e);
      }    
   }
   
   private Map<TupleRef, TupleRef> buildSubst(String newFrame, Tree pass, List<TupleRef> inArgs) {
      Map<TupleRef, TupleRef> result = new HashMap();
      
      for (TupleRef ref: inArgs) {
         TupleRef newRef = (TupleRef) adaptor.create(TUPLE_REF, newFrame);
//         adaptor.addChild(newRef, adaptor.dupTree(ref.findChild(TUPLE_REF)));  //TODO: Pop off the frame name level when framerefs are added earlier
         adaptor.addChild(newRef, adaptor.dupTree(ref));  

         result.put(ref, newRef);
      }
      return result;
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
      MultiPartName name = new MultiPartName(((Tree) t).getText());
      return name.getName();
   }
   
   private String getFacet(Object t) {
      MultiPartName name = new MultiPartName(((Tree) t).getText());
      return name.getFacet();
   }
   
   /**What is actually going to be inlined?
    * Having established that the call is inlineable, only the call chain not including the predicate needs to be inlined.
    * This is the "core" of the defined operator.
    *
    * @param operator A tree node representing an operator
    * @param facet The facet from that operator that is desired
    **/   
   private Function getCoreCall(OperatorFacet facet) {
      assert facet.getRules().size() == 1 : "Can only inline facets with exactly one rule";
      return (Function) ((CommonTree) facet.getRules().get(0).getRules().get(0).getFirstChildWithType(CALL_CHAIN)).getFirstChildWithType(FUNCTION);
   }
}

//Identify operators that only have one branch
search: ^(OPERATOR_FACET . LIST opRules);				    //This rule  ensures there are no pre-filters (part of being in-line-able)
opRules: ^(LIST ^(OPERATOR_RULE ^(LIST pred=predicate) ^(LIST .)))   //This step ensures there is only one rule (part of being in-line-able)  TODO: Inline multiple rules after explicit framing is universal
  {if ($pred.pred.getText().startsWith("#TrivialTrue")) {
     Tree op = pred.tree.getAncestor(OPERATOR);
     simple.put(op.getText(), (StencilTree) op);
  }};

predicate returns [Tree pred]: ^(PREDICATE ^(RULE . ^(CALL_CHAIN f=. n=.))) {$pred = $f;};


replace: ^(cc=CALL_CHAIN chain[cc]);
chain[Tree prior]
  : ^(f=FUNCTION spec=. args=. pass=. target=chain[f])
      -> {simple.containsKey(getName($f))}?  {replaceRef((Function) $f)}
      -> 									^(FUNCTION $spec $args $pass $target)
  | ^(PACK .*);