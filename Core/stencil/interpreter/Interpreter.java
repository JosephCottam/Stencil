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

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;

import stencil.display.StencilPanel;
import stencil.interpreter.NeedsGuides;
import stencil.interpreter.UpdateGuides;
import stencil.operator.module.ModuleCache;
import stencil.parser.ParserConstants;
import stencil.parser.tree.*;
import stencil.streams.Tuple;
import stencil.types.Converter;
import stencil.util.Tuples;

public class Interpreter {
	private final StencilPanel panel;
	private final Program program;
	
	private final NeedsGuides needsGuides;
	private final UpdateGuides updateGuides;
	
	private boolean abortOnError = false;
	
	public Interpreter(StencilPanel panel) {
		this.panel = panel;
		this.program = panel.getProgram();
		
		ModuleCache c = program.getModuleCache();

		TreeNodeStream treeTokens = new CommonTreeNodeStream(program);
		
		NeedsGuides ng = null;
		UpdateGuides ug = null;
		
		for (Layer l: program.getLayers()) {
			if (l.getGuides().size() >0) {				
				ng = new NeedsGuides(treeTokens);
				ug = new UpdateGuides(treeTokens);
				ug.setModuleCache(c);//TODO: Remove when all tuple references are positional
				break;	
			}
		}
		
		needsGuides = ng;
		updateGuides = ug;
	}
	
	public void processTuple(Tuple source) throws Exception {
		for (Layer layer:program.getLayers()) {
			
			groups: for(Consumes group:layer.getGroups()) {
				
				boolean matches;
				try {matches = group.matches(source);}
				catch (Exception e) {throw new RuntimeException(String.format("Error applying filter in layer %1$s", layer.getName()), e);}

				if (matches) {
					Tuple buffer = null;
					
					//Apply all rules
					try {
						stencil.adapters.Glyph glyph;

						for (Rule rule: group.getRules()) {
							boolean created = false;
							boolean preExisting = false;
							Tuple result;
							
							//Apply one rule
							try {result = rule.apply(source);}
							catch (Exception e) {throw new RuntimeException(String.format("Error invoking rule %1$d on layer %2$s.", rule.getChildIndex()+1, layer.getName()), e);}
							
							//TODO: Have rules throw exception (instead of return null)
							if (result == null) {
								if (abortOnError) {throw new RuleAbortException(rule);}
								else {
									if (created && !preExisting && buffer !=null) {
										String id = Converter.toString(buffer.get(ParserConstants.GLYPH_ID_FIELD));
										layer.getDisplayLayer().remove(id);	 //This won't be possible if we remove the remove from layer in the compiled version...try a two-phase 'reserved' and 'created' hold if something requests a 'reserved' value
									}
									continue groups;
								}
							}
														
							//Merge into final storage for glyph
							if (rule.getTarget() instanceof Glyph) {
								buffer = Tuples.merge(result, buffer);

								//We create the actual glyph early on in the process so we can refer to 'self' in 
								//any rule.  This is especially important for dynamic bindings
								if (!created && buffer.hasField(ParserConstants.GLYPH_ID_FIELD)) {
									String id = (String) buffer.get(ParserConstants.GLYPH_ID_FIELD, String.class);
									preExisting = (layer.getDisplayLayer().find(id) != null); //TODO: This probably needs to be synchronzied with the creation...against what, I'm not sure
									glyph = layer.getDisplayLayer().makeOrFind(id);	
									created = true;
								}
							}

							
						}
						
						if (buffer != null && buffer.hasField(ParserConstants.GLYPH_ID_FIELD)) {
							String id = (String) buffer.get(ParserConstants.GLYPH_ID_FIELD, String.class);
							glyph = layer.getDisplayLayer().makeOrFind(id);							
							panel.transfer(buffer, glyph);
						} else {
							//If ID is not set, then it is an 'fyi' tuple, rule-set only ran for side-effects
							//TODO: Remove when stream-stream transform works
							continue;
						}
						
						for (Rule rule: group.getRules()) {if (rule.isDyanmic()) {panel.addDynamic(glyph, rule, source);}}
					
					} catch (RuleAbortException ra) {
						//TODO: Handle (at least report) rule aborts.
					}					
				}
			}
		}
		
		//Refresh the guides (if required)!
		while (needsGuides != null && needsGuides.check(program)) {
			updateGuides.updateGuides(panel);
		}
		panel.repaint();
	}

	public StencilPanel getPanel() {return panel;}

	public void preRun() {
		//TODO: Modify when view and canvas can have multiple instances
		View.Global.setView(panel.getView());
		Canvas.Global.setCanvas(panel.getCanvas());
	}
}
