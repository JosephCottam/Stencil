tree grammar GuideSampleInSpec;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;	
  filter = true;
  output = AST;	
  superClass = TreeRewriteSequence;
}

@header {
  /**Adds the sample type to the specializer.*/
  
  package stencil.parser.string; 
  
  import stencil.parser.tree.*;
  import static stencil.adapters.java2D.render.guides.Guide2D.SAMPLE_KEY;
  import stencil.util.collections.ArrayUtil;
  
}

@members {
   public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
   
   public Object augment(StencilTree specializer, StencilTree selectors) {
      String[] sels = new String[selectors.getChildCount()];
      for (int i=0; i< selectors.getChildCount(); i++) {
         sels[i] = selectors.getChild(i).findDescendant(SAMPLE_TYPE).getText();
      } 
      
      Object rv = adaptor.dupTree(specializer);     
      Object entry = adaptor.create(MAP_ENTRY, SAMPLE_KEY);
      Const val  = (Const) adaptor.create(CONST, "");
      val.setValue(ArrayUtil.prettyString(sels));
      adaptor.addChild(entry, val);
      adaptor.addChild(rv, entry);
      return rv;
   } 
 }
     
     
topdown: ^(GUIDE id=. spec=. selectors=. rules=.) -> ^(GUIDE $id {augment(spec, selectors)} $selectors $rules);