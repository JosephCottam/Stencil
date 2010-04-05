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
 
/**Operators over a properly formed-AST and ensures that
 * all guides are up-to date.
 */ 
tree grammar UpdateGuides;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
}

@header{
	package stencil.interpreter;
	
	import java.util.Arrays;
	import java.util.ArrayList;
	import java.lang.Iterable;
	
	import stencil.parser.tree.*;	
	import stencil.util.MultiPartName;
	import stencil.display.*;
	import stencil.operator.module.*;
	import stencil.tuple.prototype.*;
	import stencil.tuple.Tuple;
	import stencil.tuple.Tuples;
	import stencil.interpreter.guide.SampleSeed; 
	import stencil.interpreter.guide.SeedOperator;
}

@members{
	private StencilPanel panel; //Panel to take elements from
	
	public void updateGuides(StencilPanel panel) {
		this.panel = panel;
				
		downup(panel.getProgram().getCanvasDef().getGuides());
	}
	
	private void apply(Guide g) {
		Specializer details = g.getSpecializer();
		SeedOperator seedOp = g.getSeedOperator();
		String layerName = g.getSelector().getLayer();
		String attribute = g.getSelector().getAttribute();
		List<Tuple> sample, projection, pairs, results;
		
		if (seedOp != null) {
			SampleSeed seed = seedOp.getSeed();
		
			try {
				sample = g.getSampleOperator().sample(seed, details);
				projection = Interpreter.processAll(sample, g.getGenerator());
			} catch (Exception e) {throw new RuntimeException("Error creating guide sample.", e);}
		} else {
			sample = g.getSampleOperator().sample(null, details);
			projection = sample;	
		}
				
		try {results = Interpreter.processAll(projection, g.getRules());}
		catch (Exception e) {throw new RuntimeException("Error formatting guide results.", e);}
		
		
		DisplayLayer layer = panel.getLayer(layerName);	   
		DisplayGuide guide = layer.getGuide(attribute);
		//Check for null only required as long as guides are run in separate thread.  Can be removed when scheduling is improved.
		if (guide != null) {guide.setElements(results);}
	}

}

topdown: ^(g=GUIDE .*) {apply((Guide) g);};