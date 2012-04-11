tree grammar OperatorInlineSimple;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree;	
    superClass = TreeRewriteSequence;
    output = AST;
    filter = true;
}

@header {
 /**Moves synthetic operators that only have one branch (the default branch)
  * up to their call site.  Requires that the OperatorExplicit has already
  * been run to ensure that state sharing is still correct.
  **/

  package stencil.parser.string;

  import stencil.parser.tree.*;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.parser.ParserConstants;
  import stencil.tuple.prototype.TupleFieldDef;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.string.util.TreeRewriteSequence;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
  import static stencil.parser.string.util.Utilities.genSym;
  
  import java.util.HashMap;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
    
  public StencilTree downup(Object p) {
     simple.clear();
     StencilTree t;
     t = downup(p, this, "search");
     t = downup(t, this, "replace");
     return t;
  }  
  
   protected final Map<String, StencilTree> simple = new HashMap();

   private Tree replaceRef(StencilTree simpleOpRef) {
      String refName = getName(simpleOpRef);
      String facetName = getFacet(simpleOpRef);
          
      StencilTree op = simple.get(refName);
      StencilTree facet = getFacetTree(facetName, op);
      StencilTree core = facet.find(RULES_OPERATOR).getChild(0).findAll(RULE).get(0).findAllDescendants(FUNCTION).get(0);    //All of those zeros BECAUSE there is only one rule in the facet
      StencilTree pass = (StencilTree) adaptor.dupTree(simpleOpRef.find(DIRECT_YIELD, GUIDE_YIELD));
      String frameName = pass.getText();
                    
      StencilTree splice = makeSplice(simpleOpRef.find(LIST_ARGS), facet, core); //Start of the thing being inserted
      StencilTree spliceTail = splice.findDescendant(PACK);                        //End of the thing being inserted
      StencilTree lastCall = (StencilTree) spliceTail.getParent();                 //That which calls the tail
      
      StencilTree target = (StencilTree) adaptor.dupTree(simpleOpRef.find(FUNCTION, PACK));    //What will be called after the splice
      TuplePrototype resultsPrototype = Freezer.prototype(facet.find(YIELDS).getChild(1));
      Map<StencilTree, StencilTree> subst = buildSubst(frameName, resultsPrototype, spliceTail.find(LIST_ARGS));          
      target = (StencilTree) ReplaceTupleRefs.apply(target, subst);


      if (requiresTuple(simpleOpRef, frameName)) {//Add a ToTuple operation ONLY if the whole tuple is referenced
            StencilTree extension = createExtension(splice, pass);
            
            //Attach the new end of the splice
            adaptor.deleteChild(lastCall,  spliceTail.getChildIndex());    //Remove the old pack
            adaptor.addChild(lastCall, extension);                         //Add the new call
            
            subst = buildSubst(frameName, extension.find(DIRECT_YIELD, GUIDE_YIELD).getText());  //Pass holds the frame name
            target = (StencilTree) ReplaceTupleRefs.apply(target, subst);   //Apply the tuple-reference name substitution
            
            lastCall = extension;                                          //Point splicy parts at the new parts
            spliceTail = extension.find(PACK);                       
            
      }                         

      adaptor.setChild(lastCall, spliceTail.getChildIndex(), target);
      
      //Rebuild pass properly
      Tree splicePass = lastCall.find(DIRECT_YIELD, GUIDE_YIELD);
      frameName = splicePass.getText();
      adaptor.setChild(lastCall, splicePass.getChildIndex(), adaptor.create(pass.getType(), frameName));//Make sure the pass type is retained
                      
      return RenameFrames.apply((StencilTree) adaptor.dupTree(splice)); //Rename because the insertion can cause the same frame name to appear in multiple rules
  }
  
  private StencilTree getFacetTree(String name, StencilTree operator) {
    for (StencilTree facet: operator) {
      if (facet.getText().equals(name)) {return facet;}
    }
    throw new RuntimeException(String.format("Facet \%1\$s not known \%2\$s.",name, operator.getText()));
  }
  
  
   private boolean requiresTuple(StencilTree function, String frameName) {
       List<StencilTree> refs = function.findAllDescendants(TUPLE_REF);
       for (StencilTree ref: refs) {
         if (ref.getChildCount() == 1 && ref.getChild(0).getText().equals(frameName)) {return true;}
       }
       return false;
   }
   
   
   /**Creates the appropriate call for constructing a tuple.*/
   private StencilTree createExtension(StencilTree splice, Tree pass) {
       StencilTree pack = splice.findDescendant(PACK);
       String frameName = genSym(FRAME_SYM_PREFIX);
       StencilTree newCall = (StencilTree) adaptor.create(FUNCTION, "ToTuple.map");	//TODO: Remove hard-coded call to map; replace with call to default...
       Object newArgs = adaptor.create(LIST_ARGS, StencilTree.typeName(LIST_ARGS));
       for (StencilTree arg: pack) {adaptor.addChild(newArgs, adaptor.dupTree(arg));}           
       adaptor.addChild(newCall, adaptor.dupTree(ParserConstants.EMPTY_SPECIALIZER_TREE));
       adaptor.addChild(newCall, newArgs);
       adaptor.addChild(newCall, adaptor.create(pass.getType(), frameName));

       //Add a dummy pack
       adaptor.addChild(newCall, adaptor.create(PACK, "DUMMY PACK--Probably an error if you ever see this"));
       
       return newCall;       
   }
       
       
   /**Creates a rename before and after the core.  
    *  The rename before is built as: take the inArgs and pass them as formals to the rename.  The specializer is the input prototype of the operator.
    *  The rename after  is built as: take the pack from the core and use its args as the formals to the rename.  The specializer is the output prototype of the operator.  
    */   
   private StencilTree makeSplice(StencilTree inArgs, StencilTree facet, StencilTree core) {
      try {             
         TuplePrototype inputPrototype  = Freezer.prototype(facet.find(YIELDS).getChild(0));
         Map<StencilTree, StencilTree> subst = buildSubst(ParserConstants.STREAM_FRAME, inputPrototype, inArgs);
         
         StencilTree workingCore = (StencilTree) adaptor.dupTree(core);
         workingCore = (StencilTree) ReplaceTupleRefs.apply(workingCore, subst);             
         return workingCore;
      } catch (Exception e) {
         throw new RuntimeException("Exception in-lining simple operator.", e);
      }    
   }
   
     /**Construct a substitution that matches values to names pair-wise
      * and includes a numeric reference substitution as well.
      */ 
     private Map<StencilTree, StencilTree> buildSubst(String frame, final TuplePrototype names, final StencilTree values) {
       assert values.getChildCount() == names.size() : "Must have same number of values as names";
  
       Map<StencilTree, StencilTree> subst = new HashMap();
              
       for (int i=0; i<values.getChildCount(); i++) {
          StencilTree value = (StencilTree) adaptor.dupTree(values.getChild(i));
          TupleFieldDef def = names.get(i);
          String name = def.name();
          subst.put(makeRef(frame, name), value);
          subst.put(makeRef(frame, i), value);
       }
       return subst;
    }
    
    private Map<StencilTree, StencilTree> buildSubst(String oldFrame, String newFrame) {
       assert oldFrame != null : "Must supply an old frame name";
       assert newFrame != null : "Must supply a new frame name";
       Map<StencilTree, StencilTree> subst = new HashMap();
       
       subst.put(makeRef(oldFrame), makeRef(newFrame));
       return subst;  
    }

    private StencilTree makeRef(Object... path) {
       StencilTree rootRef = (StencilTree) adaptor.create(TUPLE_REF, "TUPLE_REF");
       for (Object part: path) {
           StencilTree newRef = Const.instance(part);
           if (newRef.getType() == CONST) {throw new RuntimeException("Error rebuilding ref with " + part);}

           adaptor.addChild(rootRef, newRef);
       }
       return rootRef;
   }
         
   private String getName(StencilTree t) {return Freezer.multiName(t).name();}   
   private String getFacet(StencilTree t) {return Freezer.multiName(t).facet();}
   private String predName(StencilTree pred) {return pred.findDescendant(OP_NAME).getChild(1).getText();} 
}

//Identify operators that only have one branch
search: ^(OPERATOR_FACET . LIST_PREFILTER opRules);				    //This rule  ensures there are no pre-filters (part of being in-line-able)
opRules: ^(RULES_OPERATOR ^(OPERATOR_RULE ^(LIST_PREDICATES pred=predicate) ^(LIST_RULES .)))   //This step ensures there is only one rule (part of being in-line-able)  TODO: Inline multiple rules after explicit framing is universal
  {if (predName(pred.tree).equals("TrivialTrue")) {
     Tree op = pred.tree.getAncestor(OPERATOR);
     simple.put(op.getText(), (StencilTree) op);
  }};

predicate: ^(PREDICATE ^(RULE . ^(CALL_CHAIN f=.)));


replace: ^(cc=CALL_CHAIN chain[cc]);
chain[Tree prior]
  : ^(f=FUNCTION n=. spec=. args=. pass=. target=chain[f])
      -> {simple.containsKey(getName($n))}?  {replaceRef($f)}
      -> 									^(FUNCTION $n $spec $args $pass $target)
  | ^(PACK .*);