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
package stencil.adapters.java2D.data.glyphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.Tuple;
import stencil.types.color.ColorCache;
import stencil.types.gradient.GradientTuple;
import stencil.types.stroke.StrokeTuple;

public abstract class Stroked extends Basic {
	protected static final AttributeList ATTRIBUTES;
	protected static final Attribute<BasicStroke> PEN = new Attribute("PEN", StrokeTuple.DEFAULT_STROKE);
	protected static final Attribute<Color> PEN_COLOR = new Attribute("PEN_COLOR", Color.BLACK, Paint.class);
	static {
		ATTRIBUTES = new AttributeList(Basic.ATTRIBUTES);
		ATTRIBUTES.add(PEN);
		ATTRIBUTES.add(PEN_COLOR);
	}

	protected final Stroke outlineStyle;
	protected final Paint outlinePaint;
	protected final GradientTuple gradientSource;
	
	/**Hack job that allows a gradient to be
	 * generated when the bounds of the object are know...
	 */
	protected static final class MutablePaintWrapper implements Paint {
		private Paint backing;
		public MutablePaintWrapper(Paint backing) {this.backing= backing;}
		public void setBacking(Paint backing) {this.backing = backing;}
		public PaintContext createContext(ColorModel cm,
				Rectangle deviceBounds, Rectangle2D userBounds,
				AffineTransform xform, RenderingHints hints) {
			return backing.createContext(cm, deviceBounds, userBounds, xform, hints);
		}

		public int getTransparency() {return backing.getTransparency();}		
	}
	
	protected Stroked(String id, Stroked source) {
		super(id, source);
		
		this.outlinePaint = source.outlinePaint;
		this.outlineStyle = source.outlineStyle;
		this.gradientSource = null;
	}
	
	
	protected Stroked(String id) {this(id, PEN.defaultValue, PEN_COLOR.defaultValue);}
	protected Stroked(String id, Stroke outlineStyle, Paint outlinePaint) {
		super(id);
		this.outlineStyle = outlineStyle;
		this.outlinePaint = outlinePaint;
		this.gradientSource = null;
	}
	
	protected Stroked(Stroked source, Tuple option, AttributeList unsettables) {
		super(source, option, unsettables);
		outlineStyle = switchCopy(source.outlineStyle, safeGet(option, PEN));
		Paint maybeGradient = switchCopy(source.outlinePaint, safeGet(option, PEN_COLOR));
		if (maybeGradient instanceof GradientTuple) {
			outlinePaint = new MutablePaintWrapper(null);
			gradientSource = (GradientTuple) maybeGradient;
		} else {
			outlinePaint = maybeGradient;
			gradientSource = null;
		}
	}
	
	/**Given a stroked object, sets up the actual gradient...*/
	protected void fixGradient(Point2D start, Point2D end) {
		if (gradientSource != null) {
			((MutablePaintWrapper) outlinePaint).setBacking(gradientSource.getPaint(start, end));
		}
	}
	
	/**Gets fill-related properties.*/
	public Object get(String name) {
		if (PEN.is(name)) {return outlineStyle;}
		if (PEN_COLOR.is(name)) {return outlinePaint;}
		return super.get(name);
	}

	protected void render(Graphics2D g, Shape s) {
		if (outlinePaint != null && outlineStyle != null && !ColorCache.isTransparent(outlinePaint)) {
			g.setStroke(outlineStyle);
			g.setPaint(outlinePaint);
			g.draw(s);
		}
	}
}
