tree grammar DefaultPack;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
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

	public Pack fromDefault(Pack pack) {
	  Rule rule = pack.getRule();
	  Target target = rule.getTarget();
    TuplePrototype targetPrototype = target.getPrototype();
    
    Function call = pack.getPriorCall();
	  if (call == null) {throw new DefaultPackExpansionException("Cannot use implicit pack in a call-free chain.");}
    TuplePrototype callPrototype = target.getPrototype();
    
    if (callPrototype.size() != targetPrototype.size()) {throw new DefaultPackExpansionException("Default pack cannot be created because tuple prototypes are of different lengths.");}

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
