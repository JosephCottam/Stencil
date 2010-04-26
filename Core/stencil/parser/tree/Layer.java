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
import org.antlr.runtime.Token;
import stencil.display.DisplayLayer;
import stencil.parser.string.StencilParser;

public class Layer extends StencilTree {
	private List<Consumes> groups;

	private DisplayLayer displayLayer;

	
	public Layer(Token source) {super(source);}

	public String getName() {return token.getText();}
	
	public String getImplantation() {
		return  findChild(StencilParser.GLYPH, null).getText();
	}

	public List<Rule> getDefaults() {
		return (List<Rule>) findChild(StencilParser.LIST, "Defaults");
	}
	
	public List<Consumes> getGroups() {
		if (groups == null) {groups = (List<Consumes>) findChild(StencilParser.LIST, "Consumes");}
		return groups;
	}

	
	public DisplayLayer getDisplayLayer() {return displayLayer;}
	public void setDisplayLayer(DisplayLayer displayLayer) {this.displayLayer= displayLayer;}

	public Layer dupNode() {
		Layer l = (Layer) super.dupNode();
		l.displayLayer = this.displayLayer;
		return l;
	}
}
