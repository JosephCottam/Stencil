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

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.CommonTree;

import stencil.parser.string.StencilParser;


public class StencilTree extends CommonTree {
	public StencilTree(Token token) {super(token);}

	/**Gets the string name of the type given.
	 * Note: This uses a relatively slow method for lookup.
	 * @param type Type to be investigated
	 * @return The name associated with the type integer
	 */
	protected static String typeName(int type) {
		return StencilParser.tokenNames[type];
	}
	
	
	/**Find child of this given type.  
	 * This is a proxy for getfirstchildWithType, provided for symmetry with other findChild variants.
	 **/
	public StencilTree findChild(int type) {return (StencilTree) this.getFirstChildWithType(type);}
	
	
	/**Find a child with the given type and the given content value.
	 * Content value is treated as a string literal (not a pattern or prefix).
	 **/
	public CommonTree findChild(int type, String content) {
		for (int i=0; children != null && i < children.size(); i++) {
			CommonTree t = (CommonTree) children.get(i);
			if (t.getType() == type 
				&& (content == null || content.equals(t.getText()))) {
				return t;
			}
		}
		return null;
	}

	/**Find all direct descendants of the given type.*/
	public java.util.List<? extends CommonTree> findChildren(Integer... types) {
		return searchDescendants(false, types);
	}
	
	/**Recursively look for descendants of the given type.*/
	public java.util.List<? extends CommonTree> findDescendants(Integer... types) {
		return searchDescendants(true, types);
	}

	private java.util.List<? extends CommonTree> searchDescendants(boolean recursive, Integer... type) {
		java.util.List<CommonTree> results = new ArrayList();
		if (this.getChildren() == null) {return results;}
		ArrayList types = new ArrayList(Arrays.asList(type));
		Collections.sort(types);
		
		for (Object child: getChildren()) {
			if (child instanceof CommonTree && types.contains(((Tree) child).getType())) {
				results.add((CommonTree) child);
			}
			if (recursive && child instanceof StencilTree) {
				results.addAll(((StencilTree) child).findDescendants(type));
			}
		}
		return results;
	}
	
	public static boolean verifyType(Tree tree, int type) {return tree.getType() == type;}
	
	public StencilTree dupNode() {
		try {
			Constructor c = this.getClass().getConstructor(Token.class);
			return (StencilTree) c.newInstance(this.getToken());
		} catch (Exception e) {
			throw new Error(String.format("Error reflectively duplicating node to node of same type (%1$s).", this.getClass().getName()), e);
		}
	}

	public StencilTree getParent() {return (StencilTree) super.getParent();}
}
