tree grammar DefaultPack;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
	/** Convert default PACKs to fully formed PACKs.
	 **/
	package stencil.parser.string;
	
	import stencil.parser.tree.StencilTree;	
	import stencil.parser.tree.*;
}

@members{
  public static final class DefaultPackExpansionException extends RuntimeException {
    public DefaultPackExpansionException(String msg) {super(msg);}
  }
  
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

  public Object fromDefault(StencilTree pack) {
      StencilTree rule = pack.getAncestor(RULE);
      StencilTree targetPrototype = rule.findDescendant(TUPLE_PROTOTYPE); //Only one prototpye, right under the target
      
      Object newPack = adaptor.dupNode(pack);
        
      for (int i=0; i< targetPrototype.getChildCount(); i++) {
         Object ref = adaptor.create(TUPLE_REF, StencilTree.typeName(TUPLE_REF));
         adaptor.addChild(ref, adaptor.create(NUMBER, Integer.toString(i)));
         adaptor.addChild(newPack, ref);
      }
    
      return newPack;
  }
}

topdown: ^(r=PACK DEFAULT) -> {fromDefault($r)};		