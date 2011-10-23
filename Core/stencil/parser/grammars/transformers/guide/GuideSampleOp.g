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
  import stencil.interpreter.guide.samplers.*;
  import stencil.interpreter.tree.Freezer;
  import stencil.interpreter.tree.Specializer;
  import stencil.display.DisplayLayer;
  import static stencil.parser.ParserConstants.IDENTIFIER_FIELD;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

   public StencilTree samplers(StencilTree g) {
      StencilTree samples = (StencilTree) adaptor.create(LIST_GUIDE_SAMPLERS, "LIST_GUIDE_SAMPLES");
      if (g.getAncestor(GUIDE_SUMMARIZATION) !=null) {
          DisplayLayer l = (DisplayLayer) ((Const) g.getAncestor(LAYER).find(CONST)).getValue();
          Const cnst = (Const) adaptor.create(CONST, StencilTree.typeName(CONST));
          
          if (g.findAllDescendants(SAMPLE_TYPE).size() ==1) {
	          cnst.setValue(new LayerSampler(l));
          } else {
 			  cnst.setValue(new LayerCrossSampler(l, Freezer.specializer(g.find(SPECIALIZER))));
 	      }         
 	      //TODO: Add case to do group sampling...(Group sampler does multi-result tuple and guide types handle that appropriately)
 	      
          
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
