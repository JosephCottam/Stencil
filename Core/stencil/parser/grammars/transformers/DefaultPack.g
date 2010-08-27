tree grammar DefaultPack;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
	/**  Convert default PACKs to fully fledged PACKs.
	 *
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality	  
	 **/
	package stencil.parser.string;
	
  import stencil.parser.tree.util.*;
	import stencil.module.*;
	import stencil.module.util.*;
	import stencil.parser.tree.*;
	
}

@members{
  public static final class DefaultPackExpansionException extends RuntimeException {
    public DefaultPackExpansionException(String msg) {super(msg);}
  }
  
  public static Program apply (Tree t) {return (Program) TreeRewriteSequence.apply(t);}

  public Pack fromDefault(Pack pack) {
      Rule rule = pack.getRule();
      StencilTree target = rule.getGenericTarget();
      TuplePrototype targetPrototype = (TuplePrototype) target.findChild(TUPLE_PROTOTYPE);
      
      Pack newPack = (Pack) adaptor.dupNode(pack);
        
      for (int i=0; i< targetPrototype.size(); i++) {
         TupleRef ref = (TupleRef) adaptor.create(TUPLE_REF,"<autogen>");
         adaptor.addChild(ref, adaptor.create(NUMBER, Integer.toString(i)));
         adaptor.addChild(newPack, ref);
      }
    
      return newPack;
  }
}

topdown: ^(r=PACK DEFAULT) -> {fromDefault((Pack) $r)};		
