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

  import stencil.parser.tree.*;
  import stencil.parser.tree.util.MultiPartName;
  import stencil.tuple.prototype.TuplePrototypes;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
  import static stencil.parser.string.util.Utilities.genSym;
  import stencil.parser.ParserConstants;
  
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
      Function core = (Function) facet.getRules().get(0).getRules().get(0).findDescendants(FUNCTION).get(0);    //All of those zeros BECAUSE there is only one rule in the facet
      String frameName = simpleOpRef.getPass().getText();
                    
      StencilTree splice = makeSplice((List) simpleOpRef.findChild(LIST), facet, core); //Start of the thing being inserted
      Pack spliceTail = (Pack) splice.findDescendants(PACK).get(0);                     //End of the thing being inserted
      StencilTree lastCall = (StencilTree) spliceTail.getParent();    //That which calls the tail
      
      CallTarget target = (CallTarget) adaptor.dupTree(simpleOpRef.getCall()); //Target of the thing being replaced (parent pointing to splice is handled by the ANTLR transformer)
      TuplePrototype resultsPrototype = (TuplePrototype) facet.findChild(YIELDS).getChild(1);
      Map<TupleRef, Value> subst = buildSubst(frameName, resultsPrototype, spliceTail.getArguments());          
      target = (CallTarget) ReplaceTupleRefs.apply(target, subst);


      if (requiresTuple(simpleOpRef, frameName)) {//Add a ToTuple operation ONLY if the whole tuple is referenced
            Function extension = createExtension(splice);
            
            //Attach the new end of the splice
            adaptor.deleteChild(lastCall,  spliceTail.getChildIndex());    //Remove the old pack
            adaptor.addChild(lastCall, extension);                         //Add the new call
            
            subst = buildSubst(frameName, extension.getPass().getText());  //Pass holds the frame name
            target = (CallTarget) ReplaceTupleRefs.apply(target, subst);   //Apply the tuple-reference name substitution
            
            lastCall = extension;                                          //Point splicy parts at the new parts
            spliceTail = (Pack) extension.getCall();                       
            
      }                         

      adaptor.setChild(lastCall, spliceTail.getChildIndex(), target);
          
      return splice;
  }
  
   private boolean requiresTuple(Function function, String frameName) {
       List<TupleRef> refs = (List<TupleRef>) function.findDescendants(TUPLE_REF);
       for (TupleRef ref: refs) {
         if (ref.getChildCount() == 1 && ref.getChild(0).getText().equals(frameName)) {return true;}
       }
       return false;
   }
   
   /**Creates the appropriate call for constructing a tuple.
    *  TODO: ToTuple is currently used, but without a prototype-creating-specializer; should a prototype be created for it? 
    */
   private Function createExtension(StencilTree splice) {
       Pack pack = (Pack) splice.findDescendants(PACK).get(0);
       String frameName = genSym(FRAME_SYM_PREFIX);
       Function newCall = (Function) adaptor.create(FUNCTION, "ToTuple." + ParserConstants.MAP_FACET);
       Object newArgs = adaptor.create(LIST, "arguments");
       for (Object arg: pack.getChildren()) {adaptor.addChild(newArgs, adaptor.dupTree(arg));}           
       adaptor.addChild(newCall, ParserConstants.EMPTY_SPECIALIZER);
       adaptor.addChild(newCall, newArgs);
       adaptor.addChild(newCall, adaptor.create(DIRECT_YIELD, frameName));

       //Add a dummy pack
       adaptor.addChild(newCall, adaptor.create(PACK, "DUMMY PACK--Probably an error if you ever see this"));
       
       return newCall;       
   }
       
       
   /**Creates a rename before and after the core.  
    *  The rename before is built as: take the inArgs and pass them as formals to the rename.  The specializer is the input prototype of the operator.
    *  The rename after  is built as: take the pack from the core and use its args as the formals to the rename.  The specializer is the output prototype of the operator.  
    */   
   private StencilTree makeSplice(List inArgs, StencilTree facet, StencilTree core) {
      try {             
         TuplePrototype inputPrototype  = (TuplePrototype) facet.findChild(YIELDS).getChild(0);
         Map<TupleRef, Value> subst = buildSubst(ParserConstants.STREAM_FRAME, inputPrototype, inArgs);
         
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
     private Map<TupleRef, Value> buildSubst(String frame, final TuplePrototype names, final List<Value> values) {
       assert values.size() == names.size() : "Must have same number of values as names";
  
       Map<TupleRef, Value> subst = new HashMap();
              
       for (int i=0; i<values.size(); i++) {
          Value value = (Value) adaptor.dupTree(values.get(i));
          TupleFieldDef def = names.get(i);
          String name = def.getFieldName();
          subst.put(makeRef(frame, name), value);
          subst.put(makeRef(frame, i), value);
       }
       return subst;
    }
    
    private Map<TupleRef, Value> buildSubst(String oldFrame, String newFrame) {
       assert oldFrame != null : "Must supply an old frame name";
       assert newFrame != null : "Must supply a new frame name";
       Map<TupleRef, Value> subst = new HashMap();
       
       subst.put(makeRef(oldFrame), makeRef(newFrame));
       return subst;  
    }

    private TupleRef makeRef(Object... path) {
       TupleRef rootRef = (TupleRef) adaptor.create(TUPLE_REF, "TUPLE_REF");
       for (Object part: path) {
           Atom newRef = Atom.instance(part, true); 
           adaptor.addChild(rootRef, newRef);
       }
       return rootRef;
   }
         
   private String getName(Object t) {
      MultiPartName name = new MultiPartName(((Tree) t).getText());
      return name.getName();
   }
   
   private String getFacet(Object t) {
      MultiPartName name = new MultiPartName(((Tree) t).getText());
      return name.getFacet();
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