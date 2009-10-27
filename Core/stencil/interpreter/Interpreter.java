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
package stencil.interpreter;

import java.util.List;
import stencil.display.StencilPanel;
import stencil.parser.ParserConstants;
import stencil.parser.tree.*;
import stencil.streams.Tuple;
import stencil.util.Tuples;

public class Interpreter {
	private final StencilPanel panel;
	private final Program program;
	
	private static boolean abortOnError = false;
	
	public Interpreter(StencilPanel panel) {
		this.panel = panel;
		this.program = panel.getProgram();
	}
	
	public static Tuple process(List<Rule> rules, Tuple source) throws Exception {
		if (rules == null || rules.size() ==0 || source == null) {return Tuples.EMPTY_TUPLE;}
		
		Tuple glyphBuffer = null;
		Tuple viewBuffer = null;
		Tuple canvasBuffer = null;
		Tuple localBuffer = null;
		
		//Apply all rules
		try {
			for (Rule rule: rules) {
				Tuple result;
				
				try {result = rule.apply(source);}
				catch (Exception e) {throw new RuntimeException(String.format("Error invoking rule %1$d.", rule.getChildIndex()+1), e);}
				
				//TODO: Have rules throw exception (instead of return null)
				if (result == null) {
					if (abortOnError) {throw new RuleAbortException(rule);}
					else {return null;}
				}
											
				//Merge into final storage for glyph
				if (rule.getTarget() instanceof Glyph) {glyphBuffer = Tuples.merge(result, glyphBuffer);}
				else if (rule.getTarget() instanceof View) {viewBuffer = Tuples.merge(result, viewBuffer);}
				else if (rule.getTarget() instanceof Canvas) {canvasBuffer = Tuples.merge(result, canvasBuffer);}
				else if (rule.getTarget() instanceof Local) {localBuffer = Tuples.merge(result, localBuffer);}
			}
			
			
			if (viewBuffer != null) {Tuples.transfer(viewBuffer, View.global, false);}
			if (canvasBuffer != null) {Tuples.transfer(viewBuffer, Canvas.global, false);}
		
		} catch (RuleAbortException ra) {
			System.out.println("Rule aborted...");
			System.out.println(ra.getMessage());
			ra.printStackTrace();
			return null;			//TODO: Handle rule aborts.
		}
		return glyphBuffer;
	}
	
	public void processTuple(Tuple source) throws Exception {
		for (Layer layer:program.getLayers()) {			
			for(Consumes group:layer.getGroups()) {
				boolean matches;
				try {matches = group.matches(source);}
				catch (Exception e) {throw new RuntimeException(String.format("Error applying filter in layer %1$s", layer.getName()), e);}

				if (matches) {
					Tuple result;
					try {result = process(group.getRules(), source);}
					catch (Exception e) {throw new RuntimeException(String.format("Error processing in layer %1$s", layer.getName()), e);}

					if (result == null) {continue;} //Move on to the next group if the result was empty...
 
					if (result.hasField(ParserConstants.GLYPH_ID_FIELD)) {
						String id = (String) result.get(ParserConstants.GLYPH_ID_FIELD, String.class);
						stencil.adapters.Glyph glyph = layer.getDisplayLayer().makeOrFind(id);							
						glyph = panel.transfer(result, glyph);
						
						for (Rule rule: group.getRules()) {if (rule.isDyanmic()) {panel.addDynamic(glyph, rule, source);}}
					} 
				}
			}
		}
	}

	public StencilPanel getPanel() {return panel;}
}
