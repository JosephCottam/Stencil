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


import java.awt.geom.*;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.ImplicitArgumentException;
import stencil.adapters.piccoloDynamic.util.Attribute;
import stencil.adapters.piccoloDynamic.util.Attributes;
import edu.umd.cs.piccolo.util.PBounds;

public class Line extends Path {
	public static final String IMPLANTATION_NAME = "LINE";
	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		for (Attribute a : Path.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.Xn, "getXArray", "setXArray", Line.class, true, null, Double.class));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.Yn, "getYArray", "setYArray", Line.class, true, null, Double.class));

		//TODO: Add, with respect to registration and line field
//		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.WIDTH, "getWidth", "setWidth", Line.class, new Double(0)));
//		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.HEIGHT, "getHeight", "setHeight", Line.class, new Double(0)));
	}

	protected Line2D line = new Line2D.Double();

	public Line(String id) {
		super(id, IMPLANTATION_NAME, PROVIDED_ATTRIBUTES);
		applyDefaults();
		super.setPath(line);
	}

	/**Either move the line (attribute Y) or move the line end (Y1 or Y2)*/
	public void setYArray(String att, double value) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple Y in indexed Y accessor";

		int arg = index(att);

		switch(arg) {
			case 1: line.setLine(line.getX1(), value, line.getX2(), line.getY2()); break;
			case 2: line.setLine(line.getX1(), line.getY1(), line.getX2(), value); break;
			default: throw new ImplicitArgumentException(Line.class, baseName(att), arg);
		}
		super.setPath(line);
	}

	public Object getYArray(String att) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple Y in indexed Y accessor";

		int arg = index(att);

		switch(arg) {
			case 1: return line.getY1();
			case 2: return line.getY2();
			case -1: return new Double[]{line.getY1(), line.getY2()};
			default: throw new ImplicitArgumentException(Line.class, baseName(att), arg);
		}
	}

	/**Either move the line (attribute X) or move the line end (X1 or X2)*/
	public void setXArray(String att, double value) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple X in indexed X accessor";

		int arg = index(att);

		switch(arg) {
			case 1: line.setLine(value, line.getY1(), line.getX2(), line.getY2()); break;
			case 2: line.setLine(line.getX1(), line.getY1(), value, line.getY2()); break;
			default: throw new ImplicitArgumentException(Line.class, baseName(att), arg);
		}
		super.setPath(line);
	}

	public Object getXArray(String att) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple X in indexed X accessor";

		int arg = index(att);

		switch(arg) {
			case 1: return line.getX1();
			case 2: return line.getX2();
			case -1: return new Double[]{line.getX1(), line.getX2()};
			default: throw new ImplicitArgumentException(Line.class, baseName(att), arg);
		}
	}

	/**What is the X/Y implicit argument (as a number)*/
	private int index(String att) {
		try {
			String arg = nameArgs(att);
			if (arg == null) {return -1;}
			int val = Integer.parseInt(arg);
			return val;
		} catch (Exception e) {throw new ImplicitArgumentException(Line.class, att, e);}
	}

	public PBounds getBoundsReference() {return path.getBoundsReference();}

}
