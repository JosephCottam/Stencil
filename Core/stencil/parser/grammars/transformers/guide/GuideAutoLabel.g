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
  
  import stencil.parser.tree.Program;
  import stencil.parser.tree.Specializer;
  import stencil.parser.tree.Selector; 
  import static stencil.parser.ParserConstants.GUIDE_LABEL;  
}

@members{
   public static Program apply (Tree t) {
     return (Program) TreeRewriteSequence.apply(t);
  }
  
  private Specializer autoLabel(Specializer spec, Selector sel) {
    Specializer newSpec = (Specializer) adaptor.dupTree(spec);
    
    String fields = "HA HA HA HA!!!";   //Change to the argument names 
    
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
    