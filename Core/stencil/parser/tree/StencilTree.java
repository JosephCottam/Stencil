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
import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.CommonTree;

import stencil.parser.string.StencilParser;


public class StencilTree extends CommonTree {
	protected int type = Integer.MIN_VALUE;
	public StencilTree(Token token) {super(token);}

	/**Gets the string name of the type given.
	 * Note: This uses a relatively slow method for lookup.
	 * @param type Type to be investigated
	 * @return The name associated with the type integer
	 */
	protected static String typeName(int type) {
		return StencilParser.tokenNames[type];
	}
	
	public static boolean verifyType(Tree tree, int type) {return tree.getType() == type;}

	/**Creates an array of accessors from the trees.  The objects in the array will be of the type passes*/
	public static List<? extends StencilTree> pack(List<Tree> trees, Class<? extends Object> type) {
		Constructor<? extends Object> c;

		try {
			c = type.getConstructor(new Class[]{Tree.class});
		} catch (Exception e) {throw new IllegalArgumentException("Could not find appropriate constructor in class type passed.");}

		StencilTree[] result = new StencilTree[trees.size()];
		for (int i=0; i<trees.size(); i++) {
			try{
				result[i] = (StencilTree) c.newInstance(new Object[]{trees.get(i)});
			} catch (Exception e) {throw new IllegalArgumentException("Error constructing wrapper accessor.", e);}
		}
		return Arrays.asList(result);
	}

	/**Creates an array of accessors from the trees.  The objects in the array be created by the method passed.
	 * The method passed must take a single argument of type Tree.*/
	public static List<? extends StencilTree> pack(List<Tree> trees, Class clss, String factoryMethod) {
		StencilTree[] result = new StencilTree[trees.size()];
		Method factory;
		try {factory = clss.getMethod(factoryMethod, Tree.class);}
		catch (Exception e) {throw new Error("Could not find " + factoryMethod + " method in class CallTarget");}

		for (int i=0; i<trees.size(); i++) {
			try{
				result[i] = (StencilTree) factory.invoke(null, trees.get(i));
			} catch (Exception e) {throw new IllegalArgumentException("Error constructing wrapper accessor.", e);}
		}
		return Arrays.asList(result);
	}

	
	public Tree dupNode() {
		try {
			Constructor c = this.getClass().getConstructor(Token.class);
			return (Tree) c.newInstance(this.getToken());
		} catch (Exception e) {
			throw new Error(String.format("Error reflectively duplicating node to node of same type (%1$s).", this.getClass().getName()), e);
		}
	}

	/**Extract a portion after a dot from a name*/
	public static String getSubName(String name) {
		return name.substring(name.indexOf(".")+1);
	}
	
	public final int getType() {
		if (type == Integer.MIN_VALUE) {type = token.getType();}
		return type;
	}
	
	public StencilTree getParent() {return (StencilTree) super.getParent();}
}
