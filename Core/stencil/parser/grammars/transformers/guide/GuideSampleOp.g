tree grammar GuideSampleOp;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
	output = AST;
	superClass = TreeRewriteSequence;
}

@header {
  /**Determines the sample operator based on the specializer.
   * Retrieves it and annotates the guide with it.
   */

	package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import stencil.interpreter.guide.Samplers;
  import stencil.interpreter.guide.samplers.LayerSampler;
  import stencil.interpreter.tree.Freezer;
  import stencil.interpreter.tree.Specializer;
  import stencil.display.DisplayLayer;
  import static stencil.parser.ParserConstants.IDENTIFIER_FIELD;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

   //TODO: Move the semantic checks somewhere... 
   //Throws an exception if summarization guides are mal-formed    
   public DisplayLayer verifySummary(StencilTree g) {
      StencilTree selectors = g.find(LIST_SELECTORS);
      if (selectors.getChildCount() != 1) {throw new RuntimeException("Can only use one attribute on summarization guides.");}

      StencilTree sel = selectors.getChild(0);     
      String att = sel.getText(); //The data source on the only indicated attribute, must be ID
      if (!att.equals(IDENTIFIER_FIELD)) {throw new RuntimeException("Can only used ID as the attribute for summarization guides.");}
      return (DisplayLayer) ((Const) g.getAncestor(LAYER).find(CONST)).getValue(); 
   }

   public StencilTree samplers(StencilTree g) {
      StencilTree samples = (StencilTree) adaptor.create(LIST_GUIDE_SAMPLERS, "LIST_GUIDE_SAMPLES");
      if (g.getAncestor(GUIDE_SUMMARIZATION) !=null) {
          DisplayLayer l = verifySummary(g);
          Const cnst = (Const) adaptor.create(CONST, StencilTree.typeName(CONST));
          cnst.setValue(new LayerSampler(l));
          //HACK:  Java type-based constructor matching only does exact matching.  
          //        This fails my use case because the constructor is declared w.r.t an interface but l will always be an instance of some class

          StencilTree sample = (StencilTree) adaptor.create(SAMPLE_OPERATOR, "SAMPLE_OPERATOR");
          sample.addChild(cnst);
          samples.addChild(sample);
      } else {
          for (StencilTree sampleType: g.findAllDescendants(SAMPLE_TYPE)) {
            Const cnst = (Const) adaptor.create(CONST, StencilTree.typeName(CONST));
            cnst.setValue(Samplers.get(sampleType.getText()));
    
            StencilTree sample = (StencilTree) adaptor.create(SAMPLE_OPERATOR, "SAMPLE_OPERATOR");
            sample.addChild(cnst);
            samples.addChild(sample);
          }
      }
      
      return samples;
   }
   
}

topdown 
  : ^(g=GUIDE rest+=.*) -> ^(GUIDE $rest* {samplers($g)});
