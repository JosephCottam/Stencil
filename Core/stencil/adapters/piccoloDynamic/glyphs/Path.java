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
package stencil.adapters.piccoloDynamic.glyphs;

import static stencil.adapters.general.Strokes.StrokeProperty;

import edu.umd.cs.piccolo.nodes.PPath;
import java.awt.Shape;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Strokes;
import stencil.adapters.piccoloDynamic.util.*;

/**Path contains the mechanics for painting and stroking an attributed path.*/
public abstract class Path extends CommonNode {
	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		for (Attribute a : CommonNode.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.remove(StandardAttribute.WIDTH.name());
		PROVIDED_ATTRIBUTES.remove(StandardAttribute.HEIGHT.name());

		//Used to derive defaults for stroke-related properties

		for (StrokeProperty prop: StrokeProperty.values()) {
			PROVIDED_ATTRIBUTES.put(new Attribute(prop.name(), "getStrokePart", "setStrokePart", Path.class, true, prop.getDefaultValue(), prop.getType()));
		}
	}

	protected PPath path = new PPath(new java.awt.geom.GeneralPath());

	protected Path(String id, String implantation, Attributes attributes) {
		super(id, implantation, attributes);
		super.setChild(path);
	}

	public Object getStrokePart(String name) {return Strokes.get(name, path.getStroke(), path.getStrokePaint());}
	public void setStrokePart(String name, Object value) {
		Strokes.ColoredStroke rv = Strokes.modify(name, value, path.getStroke(), path.getStrokePaint());
		path.setStroke(rv.style);
		path.setStrokePaint(rv.paint);
	}

	public void setWeight(double value) {Strokes.setWeight(value, path.getStroke());}
	public double getWeight() {return (Double) Strokes.get(StrokeProperty.STROKE_WEIGHT, path.getStroke());}

	public void setPath(Shape newPath) {path.setPathTo(newPath);}
	public Shape getPath() {return path.getPathReference();}
}

