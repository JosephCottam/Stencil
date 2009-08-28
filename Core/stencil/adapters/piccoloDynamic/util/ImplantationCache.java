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
package stencil.adapters.piccoloDynamic.util;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;

import stencil.adapters.piccoloDynamic.glyphs.*;

/**Factory class for producing new PNodes based on an implantation identifier.*/
public class ImplantationCache {
	private static interface Maker {
		/**Create an new instance.*/
		public Node make(String id);
		
		/**Would invoking this maker return an object of a different type than the one presented?
		 * Sometimes more than one implantation may be handled by the same class (e.g. a line-strip
		 * vs. a polygon may on differ in the implicit link between the last specified coordinate and the first).
		 * This method lets you see if you need to build something new, or just tweak some parameters.**/
		public boolean required(Node original);
	}

	/**Simple class for instantiating built-in glyph types.*/
	private static class ClassMaker implements Maker {
		private Class clss;
		public ClassMaker(Class clss) {
			if (clss == null) {throw new IllegalArgumentException("No class was specified.  Class is required.");}
			this.clss = clss;
			try {
				Constructor c = clss.getConstructor(String.class);
				c.newInstance("junk id");
			} catch (Exception e) {
				throw new IllegalArgumentException("Error with class use in default class maker. Exception using using class " + clss.getCanonicalName() + ".", e);
			}
		}

		public Node make(String id) {
			try {
				Constructor c = clss.getConstructor(String.class);
				return (Node) c.newInstance(id);
			} catch (Exception e) {throw new IllegalArgumentException(String.format("Error instantating object of class %1$s with id %1$s.", clss.getName(), id));}
		}

		public boolean required(Node original) {
			return !original.getClass().equals(clss);
		}

	}


	public static ImplantationCache instance = new ImplantationCache();

	protected Map<String, Maker> types;

	private ImplantationCache() {
		types = new HashMap<String, Maker>();
		reset();
	}

	public void reset() {
		add(Image.IMPLANTATION_NAME, Image.class);
		add(Shape.IMPLANTATION_NAME, Shape.class);
		add(Text.IMPLANTATION_NAME, Text.class);
		add(Line.IMPLANTATION_NAME, Line.class);
		add(Arc.IMPLANTATION_NAME, Arc.class);
		add(AbstractPoly.Poly.IMPLANTATION_NAME, AbstractPoly.Poly.class);
		add(AbstractPoly.PolyLine.IMPLANTATION_NAME, AbstractPoly.PolyLine.class);
		add(Pie.IMPLANTATION_NAME, Pie.class);
	}

	public void add(String implantation, Class clss) {add(implantation, new ClassMaker(clss));}
	public void add(String implantation, Maker maker) {types.put(implantation, maker);}
	
	/**Create a new instance of a PNode based on the implantation name.
	 * 
	 * @param implantation Name of the implantation to produce
	 * @param id Id the new node will have.
	 * @throws ImplantationException Thrown if the requested name has not been registered
	 * @return
	 */
	public Node instance(String implantation, String id) throws ImplantationException {
		Maker maker = types.get(implantation);
		if (maker == null) {throw new ImplantationException(implantation, id, "Implantation not found in cache.");}
		
		return maker.make(id);		
	}
}
