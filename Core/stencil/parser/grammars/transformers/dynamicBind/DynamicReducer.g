tree grammar DynamicReducer;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Reworks the bindings to stream and local values and creates the reducer object for the consumes block.
   *  Must be done after frame references have been added, but before numeralization (or the frame check must switched to be numerical).
   **/

  package stencil.parser.string;  
  
  import stencil.parser.tree.StencilTree; 
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.util.TreeRewriteSequence;
  import static stencil.parser.ParserConstants.*;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  public StencilTree changeRefs(StencilTree dynamicRules) {
     StencilTree reducer = reducer(dynamicRules);
     Map<StencilTree, StencilTree> subst = buildSubst(reducer.find(PACK));
     StencilTree rules = (StencilTree) adaptor.dupTree(dynamicRules);

     StencilTree t = (StencilTree) ReplaceTupleRefs.apply(rules, subst);
     return t;
  }

  //Build a subst to replace prefixes of stream/local refs with new prefixes to the revised stream ref
  private Map<StencilTree, StencilTree> buildSubst(StencilTree p) {
     Map<StencilTree, StencilTree> subst = new HashMap();
     for(int i=0; i< p.getChildCount(); i++) {
        StencilTree newRef = (StencilTree) adaptor.create(TUPLE_REF, StencilTree.typeName(TUPLE_REF));
        adaptor.addChild(newRef, adaptor.create(ID, STREAM_FRAME));
        adaptor.addChild(newRef, adaptor.create(NUMBER, Integer.toString(i)));
        
        StencilTree oldRef = (StencilTree) adaptor.dupTree((StencilTree) p.getChild(i));
        for (int extraRef=2; extraRef< oldRef.getChildCount(); extraRef++) {
            adaptor.deleteChild(oldRef, extraRef);
        }
        subst.put(oldRef, newRef);
     }
     return subst;
  }

  /**Generate a pack that takes an input and local and produces a tuple to be stored for 
   ** processing of dynamic rules.
   **/
  public StencilTree reducer(StencilTree dynamicRules) {
     List<StencilTree> refs = dynamicRules.findAllDescendants(TUPLE_REF);
     StencilTree reducer = (StencilTree) adaptor.create(DYNAMIC_REDUCER, "DYNAMIC_REDUCER");
     StencilTree pack = (StencilTree) adaptor.create(PACK, StencilTree.typeName(PACK));
     adaptor.addChild(reducer, pack);
   
     for (StencilTree ref: refs) {
        if (STREAM_FRAME.equals(ref.getChild(0).getText()) || LOCAL_FRAME.equals(ref.getChild(0).getText())) {
          if (!hasChild(pack, ref)) {
              adaptor.addChild(pack, adaptor.dupTree(ref));
          }
        }
     }
     return reducer;
  }
  
  /**Is the tree a child of the root?**/
  private boolean hasChild(final StencilTree root, final StencilTree tree) {
    for (StencilTree child:root) {
      if (child == tree|| tree.equals(child)) {return true;}
    }
    return false;
  }
}

topdown: ^(CONSUMES f=. p=. l=. r=. d=.) 
          -> ^(CONSUMES $f $p $l $r {changeRefs($d)} {reducer($d)});



