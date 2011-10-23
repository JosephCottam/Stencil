tree grammar GuideSampleInSpec;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;	
  filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Adds the sample type(s) and source field(s) to the specializer.*/
  
  package stencil.parser.string; 
  
  import stencil.parser.tree.*;
  import static stencil.adapters.java2D.render.guides.Guide2D.SAMPLE_KEY;
  import static stencil.adapters.java2D.render.guides.Guide2D.FIELDS_KEY;
  import stencil.util.collections.ArrayUtil;
  
}

@members {
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
   
   public StencilTree augment(StencilTree specializer, StencilTree selectors) {
      String[] samples = new String[selectors.getChildCount()];
      String[] sources = new String[selectors.getChildCount()];
      
      for (int i=0; i< selectors.getChildCount(); i++) {
         samples[i] = selectors.getChild(i).findDescendant(SAMPLE_TYPE).getText();
         sources[i] = selectors.getChild(i).getText();
      } 
      
      StencilTree rv = (StencilTree) adaptor.dupTree(specializer);     
      adaptor.addChild(rv, entry(SAMPLE_KEY, samples));
      adaptor.addChild(rv, entry(FIELDS_KEY, sources));
      return rv;
   } 
   
   private StencilTree entry(String key, String[] vals) {
      StencilTree entry = (StencilTree) adaptor.create(MAP_ENTRY, key);
      Const val  = (Const) adaptor.create(CONST, "");
      val.setValue(ArrayUtil.prettyString(vals));
      adaptor.addChild(entry, val);
      return entry;
   }
 }
     
     
topdown: ^(GUIDE id=. spec=. selectors=. rules=.) -> ^(GUIDE $id {augment(spec, selectors)} $selectors $rules);