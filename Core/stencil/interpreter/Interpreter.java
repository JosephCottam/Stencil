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
import static java.lang.String.format;

import stencil.display.StencilPanel;
import stencil.parser.ParserConstants;
import stencil.parser.tree.*;
import stencil.parser.tree.util.Environment;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;

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
		
		Tuple fullResult = null;
		
		//Apply all rules
		try {
			for (Rule rule: rules) {
				Tuple result;
				
				try {result = rule.apply(source);}
				catch (Exception e) {throw new RuntimeException(String.format("Error invoking rule %1$d.", rule.getChildIndex()+1), e);}
				
				//TODO: Have rules throw exception (instead of return null)
				//TODO: Fix the creation issue.  Right now errors in dynamic rules are ignored (they will be retried later in the dynamic system)
				if (result == null && !rule.isDyanmic()) {
					if (abortOnError) {throw new RuleAbortException(rule);}
					else {return null;}
				}
				
				fullResult = Tuples.merge(result, fullResult);
			}
			
		} catch (RuleAbortException ra) {
			System.out.println("Rule aborted...");
			System.out.println(ra.getMessage());
			ra.printStackTrace();
			return null;			//TODO: Handle rule aborts.
		}
		return fullResult;
	}
	
	public void processTuple(Tuple source) throws Exception {
		for (Layer layer:program.getLayers()) {			
			for(Consumes group:layer.getGroups()) {
				boolean matches;
				try {matches = group.matches(source);}
				catch (Exception e) {throw new RuntimeException(format("Error applying filter in layer %1$s", layer.getName()), e);}
				
				
				if (matches) {
					Tuple result;
					Tuple local;
					
					Environment env = buildEnvironment(source,  null);
					try {local = process(group.getLocalRules(), env);}
					catch (Exception e) {throw new RuntimeException(format("Error processing locals in layer %1$s", layer.getName()), e);}
					
					env = buildEnvironment(source, local);
					try {
						result = process(group.getGlyphRules(), env);
						if (result != null && result.getPrototype().contains(ParserConstants.GLYPH_ID_FIELD)) {
							try {
								//TODO: What about locals in a dynamic binding?
								stencil.adapters.Glyph glyph = layer.getDisplayLayer().makeOrFind(result);
								for (Rule rule: group.getGlyphRules()) {if (rule.isDyanmic()) {panel.addDynamic(glyph, rule, source);}}
							} catch (Exception e) {
								throw new RuntimeException(format("Error updating layer %1$s with tuple %2$s", layer.getName(), result.toString()), e);
							}
						} 
					}
					catch (Exception e) {throw new RuntimeException(format("Error processing glyph rules in layer %1$s", layer.getName()), e);}
					
					try {
						Tuple viewUpdate = process(group.getViewRules(), env);
						if (viewUpdate != null) {Tuples.transfer(viewUpdate, View.global);}
					}
					catch (Exception e) {throw new RuntimeException(format("Error processing view rules in layer %1$s.", layer.getName()), e);}
					
					try{
						Tuple canvasUpdate = process(group.getCanvasRules(), env);
						if (canvasUpdate != null) {Tuples.transfer(canvasUpdate, Canvas.global);}
					} catch (Exception e) {throw new RuntimeException(format("Error processing canvas rules in layer %1$s.", layer.getName()), e);}

				}
			}
		}
	}

	private static final Environment buildEnvironment(Tuple stream, Tuple local) {
		Environment e = new Environment(ParserConstants.CANVAS_PREFIX, Canvas.global);
		e = e.append(ParserConstants.VIEW_PREFIX, View.global);
		
		if (stream.getPrototype().contains(Tuple.SOURCE_KEY)) {
			e = e.append(Converter.toString(stream.get(Tuple.SOURCE_KEY)), stream); //TODO: BAD JOSEPH.  Using named references			
		} else {
			e = e.append(stream);
		}
		
		if (local != null) {e = e.append(ParserConstants.LOCAL_PREFIX, local);}
		
		return e;
	}
	
	public StencilPanel getPanel() {return panel;}
}
