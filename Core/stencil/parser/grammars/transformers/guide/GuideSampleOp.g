tree grammar GuideSampleOp;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
	superClass = TreeFilterSequence;
}

@header {
  /**Determines the sample operator based on the specializer.
   * Retrieves it and annotates the guide with it.
   */

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import stencil.interpreter.guide.Samplers;
	import java.util.Arrays;
	import java.util.Collections;
	import stencil.interpreter.guide.samplers.LayerSampler;
}

@members {
   public static void apply (Tree t) {
     apply(t, new Object(){}.getClass().getEnclosingClass());
   }
     
   public void setSummarySample(Guide g) {
	   Program p = (Program) g.getAncestor(PROGRAM);
	   Layer l = p.getLayer(g.getSelector().getPath().get(0).getID());
	   g.setSampleOperator(new LayerSampler(l));
   }
   
   public void setDirectSample(Guide g) {
      Specializer spec = g.getSpecializer();
      
      boolean categorical=true;
      
      String sampleType = Samplers.CATEGORICAL;
      if (spec.getMap().containsKey("sample")) {
         sampleType = (String) spec.getMap().get("sample").getValue();
      }
      
      String dataType;
      if (sampleType.equals(Samplers.CATEGORICAL)) {dataType = "java.lang.String";}
      else {dataType = "java.lang.Integer";}
      if (spec.getMap().containsKey("Type")) {dataType = spec.getMap().get("Type").toString();}

      Class t;
      try {t = Class.forName(dataType);}
      catch (Exception e) {throw new RuntimeException("Invalid type specified for guide sampling: " + dataType);}
      
      g.setSampleOperator(Samplers.get(t));
   }
}

topdown 
  : ^(GUIDE_DIRECT ^(g=GUIDE .*)) {setDirectSample((Guide) g);}
  | ^(GUIDE_SUMMARIZATION ^(g=GUIDE .*)) {setSummarySample((Guide) g);};
