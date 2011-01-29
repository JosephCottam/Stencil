tree grammar LiftStreamPrototypes;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
   /**Creates a stream prototype for internally defined streams. **/

   package stencil.parser.string;
	
   import stencil.parser.tree.*;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

   private Object makePrototype(StencilTree streamDef) {
      assert streamDef.getType() == STREAM_DEF : "Did not recieve stream def when expected.";
     
      Object s = adaptor.create(STREAM, streamDef.getText());
      adaptor.addChild(s, adaptor.dupTree(streamDef.find(TUPLE_PROTOTYPE)));
      return s;
   }   

   private Tree addPrototypes(StencilTree prototypes, StencilTree defs) {
      Tree listing = (Tree) adaptor.dupTree(prototypes);
      
      for (StencilTree sd: defs) {adaptor.addChild(listing, makePrototype(sd));}
      return listing;
   }
}

topdown 
  : ^(PROGRAM i=. gv=. sd=. o=. cd=. s=. r+=.*) 
      -> ^(PROGRAM $i $gv {addPrototypes($sd, $s)} $o $cd $s $r*);
