tree grammar DynamicReducer;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Reworks the bindings to stream and local values and creates the reducer object for the consumes block.
   *  Must be done after frame references have been added, but before numeralization (or the frame check must switched to be numerical).
   **/

  package stencil.parser.string;
  
  import stencil.parser.tree.*;
  import static stencil.parser.string.StencilParser.*;
  import static stencil.parser.ParserConstants.*;
  import stencil.parser.tree.util.Environment;
}

@members {
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}
  
  public StencilTree changeRefs(List dynamicRules) {
     Pack p = reducer(dynamicRules);
     Map<TupleRef, Value> subst = buildSubst(p);
     StencilTree rules = (StencilTree) adaptor.dupTree(dynamicRules);

     StencilTree t = (StencilTree) ReplaceTupleRefs.apply(rules, subst);
     return t;
  }

  //Build a subst to replace prefixes of stream/local refs with new prefixes to the revised stream ref
  private Map<TupleRef, Value> buildSubst(Pack p) {
     Map<TupleRef, Value> subst = new HashMap();
     for(int i=0; i< p.getChildCount(); i++) {
        TupleRef newRef = (TupleRef) adaptor.create(TUPLE_REF, "TupleRef");
//        adaptor.addChild(newRef, adaptor.create(NUMBER, Integer.toString(Environment.STREAM_FRAME)));
        adaptor.addChild(newRef, adaptor.create(ID, STREAM_FRAME));
        adaptor.addChild(newRef, adaptor.create(NUMBER, Integer.toString(i)));
        StencilTree oldRef = (StencilTree) adaptor.dupTree((StencilTree) p.getChild(i));
        for (int extraRef=2; extraRef< oldRef.getChildCount(); extraRef++) {
            adaptor.deleteChild(oldRef, extraRef);
        }
        subst.put((TupleRef) oldRef, newRef);
     }
     return subst;
  }

  public Pack reducer(List dynamicRules) {
     List<TupleRef> refs = (List<TupleRef>) ((StencilTree) dynamicRules).findDescendants(TUPLE_REF);
     Pack reducer = (Pack) adaptor.create(PACK, "Dynamic Reducer");
     List reducerLst = new stencil.parser.tree.List.WrapperList(reducer);
   
     for (TupleRef ref: refs) {
        if (STREAM_FRAME.equals(ref.getChild(0).getText()) || LOCAL_FRAME.equals(ref.getChild(0).getText())) {
          if (!reducerLst.contains(ref)) {
              adaptor.addChild(reducer, adaptor.dupTree(ref));
          }
        }
     }
     return reducer;
  }
  
}

topdown: ^(CONSUMES f=. p=. l=. r=. v=. c=. d=.) -> ^(CONSUMES $f $p $l $r $v $c {changeRefs((List) $d)} {reducer((List) $d)});



