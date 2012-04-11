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
  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.module.operator.wrappers.LayerOperator;
  import stencil.parser.string.util.Utilities;
  
  import static stencil.parser.ParserConstants.IDENTIFIER_FIELD;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

   public StencilTree samplers(StencilTree g) {
      StencilTree samples = (StencilTree) adaptor.create(LIST_GUIDE_SAMPLERS, "");
      if (g.getAncestor(GUIDE_SUMMARIZATION) !=null) {
          String layerName = g.getAncestor(LAYER).getText();
          LayerOperator layerOp = (LayerOperator) Utilities.findOperator(g, layerName);
          DisplayLayer l = layerOp.layer();
      
          Const cnst = (Const) adaptor.create(CONST, "Sample Operator");
          
          if (g.findAllDescendants(SAMPLE_TYPE).size() ==1) {
	          cnst.setValue(new LayerSampler(l));
          } else {
 			  cnst.setValue(new LayerCrossSampler(l, Freezer.specializer(g.find(SPECIALIZER))));
 	      }         
 	      //TODO: Add case to do group sampling...(Group sampler does multi-result tuple and guide types handle that appropriately)
 	      
          samples.addChild(cnst);
      } else {
          for (StencilTree sampleType: g.findAllDescendants(SAMPLE_TYPE)) {
            Const cnst = (Const) adaptor.create(CONST, "Sample Operator");
            cnst.setValue(Samplers.get(sampleType.getText()));
            samples.addChild(cnst);
          }
      }
      
      return samples;
   }
   
}

topdown 
  : ^(g=GUIDE rest+=.*) -> ^(GUIDE $rest* {samplers($g)});
