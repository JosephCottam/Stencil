/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
 

tree grammar GuideSampleOp;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
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
	import stencil.interpreter.guide.samplers.IdentitySampler;
}

@members {
   public static final List<String> DIRECT_TYPES = Arrays.asList("sidebar", "axis");
   static {Collections.sort(DIRECT_TYPES);}
   
   public void setSampleOp(Guide g) {
	   String type = g.getGuideType();
	   if (DIRECT_TYPES.contains(g.getGuideType())) {
		   setDirectSample(g);
	   } else {
		   setSummarySample(g);
	   }
   }
   
   public void setSummarySample(Guide g) {
	   Program p = (Program) g.getAncestor(PROGRAM);
	   Layer l = p.getLayer(g.getLayer());
	   g.setSampleOperator(new IdentitySampler(l));
   }
   
   public void setDirectSample(Guide g) {
      Specializer spec = g.getSpecializer();
      
      String sampleType = (String) spec.getMap().get("sample").getValue();
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
  : ^(g=GUIDE .*) {setSampleOp((Guide) g);};
