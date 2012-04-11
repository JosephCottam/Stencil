tree grammar LiftStreamPrototypes;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header {
   /**Creates a stream prototype for internally defined and system streams . **/

   package stencil.parser.string;
	
   import stencil.parser.tree.*;
   import stencil.parser.string.util.TreeRewriteSequence;
   import static stencil.parser.ParserConstants.SYSTEM_STREAM_TYPE;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

  public StencilTree downup(Object t) {
    t = downup(t, this, "streamStreamLift"); 
    t = downup(t, this, "addSystemStreams");
    return (StencilTree) t; 
  }

   private Object makePrototype(StencilTree streamDef) {
      assert streamDef.getType() == STREAM_DEF : "Did not recieve stream def when expected.";
     
      Object s = adaptor.create(STREAM, streamDef.getText());
      adaptor.addChild(s, adaptor.dupTree(streamDef.find(TUPLE_PROTOTYPE)));
      adaptor.addChild(s, adaptor.create(ID, SYSTEM_STREAM_TYPE));
      adaptor.addChild(s, adaptor.create(SPECIALIZER, ""));
      return s;
   }   

   private Tree addPrototypes(StencilTree prototypes, StencilTree defs) {
      Tree listing = (Tree) adaptor.dupTree(prototypes);
      
      for (StencilTree sd: defs) {adaptor.addChild(listing, makePrototype(sd));}
      return listing;
   }
}

//Add stream defs for internally defined streams
streamStreamLift
  : ^(PROGRAM i=. gv=. sd=. o=. cd=. vd=. s=. r+=.*) 
      -> ^(PROGRAM $i $gv {addPrototypes($sd, $s)} $o $cd $vd $s $r*);

  
//Add stream defs for system streams
addSystemStreams
  : ^(LIST_STREAM_DECLS defs+=.*) 
  		-> ^(LIST_STREAM_DECLS 
  				^(STREAM["#Render"] 
  					^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID["#COUNT"] DEFAULT))
  					ID[SYSTEM_STREAM_TYPE]
  					SPECIALIZER) 
  			$defs*);