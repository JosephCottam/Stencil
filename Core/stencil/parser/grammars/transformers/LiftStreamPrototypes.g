tree grammar LiftStreamPrototypes;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
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
  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }

   private Tree makePrototype(StreamDef t) {
      Stream s = (Stream) adaptor.create(STREAM, t.getName());
      adaptor.addChild(s, adaptor.dupTree(t.getPrototype()));
      return s;
   }   

   private Tree addPrototypes(List prototypes, List<StreamDef> defs) {
      Tree listing = (Tree) adaptor.dupTree(prototypes);
      
      for (StreamDef sd: defs) {adaptor.addChild(listing, makePrototype(sd));}
      return listing;
   }
}

topdown 
  : ^(PROGRAM i=. gv=. sd=. o=. cd=. s=. r+=.*) 
      -> ^(PROGRAM $i $gv  {addPrototypes((List) $sd, (List) $s)} $o $cd $s $r*);
