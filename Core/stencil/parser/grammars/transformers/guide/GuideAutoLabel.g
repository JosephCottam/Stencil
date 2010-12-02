tree grammar GuideAutoLabel;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
/** Determines the names of fields being used to create a guide
 *  and transfers them to the label attribute (if one was not already supplied).
 *  
 *  Guide declarations are identified; all those without a `gLabel' parameter have one added that includes
 *  the names of variables passed to the Monitor operator.
 * 
 **/

  package stencil.parser.string;
  
  import stencil.parser.tree.*;
  import stencil.interpreter.guide.SeedOperator;
  
  import static stencil.parser.ParserConstants.GUIDE_LABEL;  
}

@members{
   public static Program apply (Tree t) {
     return (Program) TreeRewriteSequence.apply(t);
  }
  
  private static String getSources(Selector sel) {
    Program p = (Program) sel.getAncestor(PROGRAM);
    List<Id> path = sel.getPath();

    Layer l = p.getLayer(path.get(0).getID());
    Consumes c = l.getGroups().get(0);
               
    Rule r= null;
    for (Rule r2: c.getResultRules()) {
       if (((TuplePrototype) r2.getGenericTarget().getChild(0)).contains(path.get(1).getID())) {r=r2; break;}
    }
    assert r != null : "Guide path did not match any rule.";
           
    CallTarget t = r.getAction().getStart();
    while (t instanceof Function) {
      Function f = (Function) t;
      AstInvokeable target = f.getTarget();
      if (target != null && target.getOperator() instanceof SeedOperator) {
        return f.getArguments().get(0).getChild(1).getText(); //get child 1 because this is a tuple ref and it has been framed
      }
      t = f.getCall();
   }
   throw new Error("Guide path did not lead to location with seed operator");
  }
  
  private Specializer autoLabel(Specializer spec, Selector sel) {
    Specializer newSpec = (Specializer) adaptor.dupTree(spec);
    
    String fields = getSources(sel); 
    
    Tree entry = (Tree) adaptor.create(MAP_ENTRY, GUIDE_LABEL);
    Tree value = (Tree) adaptor.create(STRING, fields);
    
    adaptor.addChild(entry, value);
    adaptor.addChild(newSpec.getChild(0), entry);
    return newSpec;
  }
}

topdown: 
    ^(GUIDE_DIRECT ^(GUIDE type=. spec=. selector=. rules=. gen=. query=.))
      -> {!((Specializer) spec).containsKey(GUIDE_LABEL)}? ^(GUIDE_DIRECT ^(GUIDE $type {autoLabel((Specializer) spec, (Selector) selector)} $selector $rules $gen $query))
            -> ^(GUIDE_DIRECT ^(GUIDE $type $spec $selector $rules $gen $query));
    