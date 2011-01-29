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
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
     
   public StencilTree summarySample(StencilTree g) {
	   StencilTree p = g.getAncestor(PROGRAM);
	   StencilTree l = p.find(LIST_LAYERS).find(LAYER, (g.find(SELECTOR).getChild(0).getText()));
	   Const sample = (Const) adaptor.create(CONST, StencilTree.typeName(CONST));
	   DisplayLayer dl = (DisplayLayer) ((Const) l.find(CONST)).getValue(); 
     sample.setValue(new LayerSampler(dl));
     return sample;
   }
   
   public StencilTree directSample(StencilTree g) {
      Specializer spec = Freezer.specializer(g.find(SPECIALIZER));
      
      String sampleType = Samplers.CATEGORICAL;
      if (spec.containsKey("sample")) {
         sampleType = (String) spec.get("sample");
      }
      
      String dataType;
      if (sampleType.equals(Samplers.CATEGORICAL)) {dataType = "java.lang.String";}
      else {dataType = "java.lang.Integer";}
      
      //Opportunity to override defaut type
      if (spec.containsKey("Type")) {dataType = (String) spec.get("Type");}

      Class t;
      try {t = Class.forName(dataType);}
      catch (Exception e) {throw new RuntimeException("Invalid type specified for guide sampling: " + dataType);}
      
      Const sample = (Const) adaptor.create(CONST, StencilTree.typeName(CONST));
      sample.setValue(Samplers.get(t));
      return sample;
   }
}

topdown 
  : ^(GUIDE_DIRECT ^(g=GUIDE rest+=.*)) -> ^(GUIDE_DIRECT ^(GUIDE $rest* ^(SAMPLE_OPERATOR {directSample($g)})))
  | ^(GUIDE_SUMMARIZATION ^(g=GUIDE rest+=.*)) -> ^(GUIDE_SUMMARIZATION ^(GUIDE $rest* ^(SAMPLE_OPERATOR {summarySample(g)})));
