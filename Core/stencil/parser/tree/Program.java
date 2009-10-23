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
package stencil.parser.tree;

import java.util.List;
import java.util.NoSuchElementException;
import org.antlr.runtime.Token;

import stencil.operator.module.ModuleCache;
import stencil.parser.string.StencilParser;

public class Program extends StencilTree {
	private static final int IMPORTS = 0;
	private static final int ORDER = 1;
	private static final int STREAMS = 2;
	private static final int CANVAS_DEF = 3;
	private static final int LAYERS = 4;
	private static final int OPERATORS =5;
	private static final int PYTHONS = 6;
	private static final int TEMPLATES = 7;
		
	private ModuleCache mc;	//TODO: Remove when all tuple references are positional
	
	public Program(Token source) {super(source);}

	/**What are the layers of this stencil?*/
	public List<Layer> getLayers() {
		assert verifyType(getChild(LAYERS), StencilParser.LIST);
		return (List) getChild(LAYERS);
	}

	/**Get the layer specified.
	 * @throws NoSuchElementException Name given that does not correspond to a layer name.
	 */
	public Layer getLayer(String name) {
		for (Layer layer:getLayers()) {
			if (layer.getName().equals(name)) {return layer;}
		}
		throw new NoSuchElementException(String.format("Layer %1$s not known", name));
	}
	
	public List<Import> getImports() {return (stencil.parser.tree.List<Import>) getChild(IMPORTS);}

	/**What order should streams be loaded in?
	 *
	 * If no order statement is made in the stencil, then
	 * it is assumed they will be loaded sequentially in the order
	 * they are mentioned in the stencil.
	 *
	 * @return
	 */
	public Order getStreamOrder() {return (Order) getChild(ORDER);}
	
	public CanvasDef getCanvasDef() {return (CanvasDef) getChild(CANVAS_DEF);}

	/**Get a list of all stream names used in this stencil.
	 * TODO: Provide a method to get all streams (not just externals)
	 * */
	public List<External> getExternals() {
		assert verifyType(getChild(STREAMS), StencilParser.LIST) : typeName(getChild(STREAMS).getType());
		return (List<External>) getChild(STREAMS);
	}

	/**List of all operators defined by this stencil.*/
	public List<Operator> getOperators() {
		assert verifyType(getChild(OPERATORS), StencilParser.LIST);
		return (List<Operator>) getChild(OPERATORS);
	}
	
	public List<Python> getPythons() {
		assert verifyType(getChild(PYTHONS), StencilParser.LIST);
		return (List<Python>) getChild(PYTHONS);
	}
	
	public List<OperatorTemplate> getOperatorTemplates() {
		assert verifyType(getChild(TEMPLATES), StencilParser.LIST);
		return (List<OperatorTemplate>) getChild(TEMPLATES);
	}	
	
	public void setModuleCache(ModuleCache mc) {this.mc = mc;}//TODO: Remove when all tuple references are positional
	public ModuleCache getModuleCache() {return mc;}//TODO: Remove when all tuple references are positional
}
