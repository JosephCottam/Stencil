tree grammar GuideLegendGeom;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
/** Sets the type of object to use in a legend's examples.
 *  This is determined by the layer type (with some translation rules).
 * 
 **/

  package stencil.parser.string;
  
  import stencil.parser.tree.*;
  import stencil.interpreter.tree.Freezer;
  import static stencil.adapters.java2D.render.guides.Legend.GEOM_TAG;
  import static stencil.display.DisplayLayer.TYPE_KEY;
}

@members{
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

   public StencilTree extendSpec(StencilTree spec) {
      String type = (String)  Freezer.specializer(spec.getAncestor(LAYER).find(SPECIALIZER)).get(TYPE_KEY);
      Object entry = adaptor.create(MAP_ENTRY, GEOM_TAG);
      
      type=type.toUpperCase();
      
      adaptor.addChild(entry, adaptor.create(STRING, type));
      adaptor.addChild(spec, entry);
      return spec;
   }
}

topdown: ^(GUIDE type=. spec=. rest+=.*) {type.getText().toLowerCase().contains("legend")}?
            -> ^(GUIDE $type {extendSpec($spec)} $rest*);
