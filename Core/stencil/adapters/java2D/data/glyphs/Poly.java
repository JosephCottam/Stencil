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

import static stencil.parser.ParserConstants.FINAL_VALUE;
import static stencil.parser.ParserConstants.NEW_VALUE;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import stencil.adapters.general.ImplicitArgumentException;
import stencil.adapters.general.Strokes;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;

public abstract class Poly extends Stroked {
	public static class PolyLine extends Poly {
		private static final String IMPLANTATION = "POLY_LINE";
		
		public PolyLine(DisplayLayer layer, String id) {super(layer, id, false);}
		protected PolyLine(String id, PolyLine source) {super(id, source);}
		protected PolyLine(PolyLine source, Tuple option) {
			super(source, option, false);
		}
		
		public String getImplantation() {return IMPLANTATION;}
		
		public PolyLine update(Tuple t) throws IllegalArgumentException {
			if (Tuples.transferNeutral(t, this)) {return this;}
			return new PolyLine(this, t);
		}

		public PolyLine updateID(String id) {return new PolyLine(id, this);}
		
	}
	public static class Polygon extends Poly {
		public static final String IMPLANTATION = "POLYGON";
		
		public Polygon(DisplayLayer layer,String id) {super(layer, id, true);}
		protected Polygon(String id, Polygon source) {super(id, source);}
		protected Polygon(Polygon source, Tuple option) {
			super(source, option, true);
		}
		
		public String getImplantation() {return IMPLANTATION;}

		public Polygon update(Tuple t) throws IllegalArgumentException {
			if (Tuples.transferNeutral(t, this)) {return this;}
			return new Polygon(this, t);
		}

		public Polygon updateID(String id) {return new Polygon(id, this);}
	}
	
	
	public static final Double DEFAULT_COORDINATE_VALUE = 0.0;
	private static final Double UNITIAILZED_COORDINATE  = Double.NaN;
	
	protected static final AttributeList ATTRIBUTES = new AttributeList(Stroked.ATTRIBUTES);
	protected static final AttributeList UNSETTABLES = new AttributeList();
	protected static final Attribute<Double> Xn = new Attribute("Xn", 0d, double.class);
	protected static final Attribute<Double> Yn = new Attribute("Yn", 0d, double.class);
	protected static final Attribute<Double> X = new Attribute("X", 0d, double.class);
	protected static final Attribute<Double> Y = new Attribute("Y", 0d, double.class);
	protected static final Attribute<String> SCALE_BY = new Attribute("SCALE_BY", "ALL");

	static {
		ATTRIBUTES.add(Xn);
		ATTRIBUTES.add(Yn);
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(SCALE_BY);
		
		UNSETTABLES.add(X);
		UNSETTABLES.add(Y);
	}
	
	private final List<Point2D> points;
	private final GeneralPath path;	
	private final boolean connect;
	private final String scaleBy;
	
	public Poly(DisplayLayer layer, String id, boolean connect) {
		super(layer, id, Strokes.DEFAULT_STROKE, Strokes.DEFAULT_PAINT);
		this.connect = connect;
		
		points = new ArrayList();
		path = buildPath(points, this.connect);
		scaleBy = SCALE_BY.defaultValue;
		super.updateBoundsRef(path.getBounds2D());
	}
	
	
	
	protected Poly(String id, Poly source) {
		super(id, source);
		this.points = source.points;
		this.path = source.path;
		this.connect = source.connect;
		this.scaleBy = source.scaleBy;
	}



	protected Poly(Poly source, Tuple option, boolean connect) {
		super(source, option, UNSETTABLES);
		this.connect = connect;
		
		this.scaleBy = switchCopy(source.scaleBy, safeGet(option, SCALE_BY));
		
		if (changesPoints(option)) {
			points = new ArrayList(source.points);
			updatePoints(points, option);
			path = buildPath(points, this.connect);
		} else {
			points = source.points;
			path = source.path;
		}
		
		super.updateBoundsRef(path.getBounds2D());
	}
	
	
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}

	public Object get(String name) {
		String base = baseName(name); 
		
		if (X.is(name)) {return bounds.getX();}
		if (Y.is(name)) {return bounds.getY();}
		
		if (Xn.is(base) || Yn.is(base)) {
			int index = (int) index(points, name, true);
			if (index == points.size()) {return UNITIAILZED_COORDINATE;}
			if (index < 0) {
				if (Xn.is(base)) {return halfPoints(true);}
				else {return halfPoints(false);}
			}
			if (Xn.is(base)) {return points.get(index).getX();}
			if (Yn.is(base)) {return points.get(index).getY();}
		} 
		
		if (SCALE_BY.is(name)) {return scaleBy;}
		
		return super.get(name);
	}	

	/**What are the X or Y half of points in this tuple?
	 * 
	 * @param x Return the x portion?  (false implies return the y portion)
	 * @return
	 */
	private final Double[] halfPoints(boolean x) {
		Double[] pts = new Double[points.size()];
		for (int i=0; i< pts.length; i++) {
			Point2D p = points.get(i);
			pts[i] = x ? p.getX() : p.getY(); 
		}
		return pts;
	}
	
	/**Does the given tuple have up point-related updates?*/
	private static final boolean changesPoints(Tuple t) {
		for (String field: t.getPrototype()) {
			String base = baseName(field);
			if (Xn.is(base) || Yn.is(base) && !base.equals(field)) {return true;}
		}
		return false;
	}
	
	private static GeneralPath buildPath(List<Point2D> points, boolean connect) {
		if (points.size() == 0) {return new GeneralPath();} //Nothing to draw until there are two points...
		
		GeneralPath p = new GeneralPath();
		Point2D prior = points.get(0);
		Point2D first = prior;
		for (int i=1; i< points.size(); i++) {
			Point2D current = points.get(i);

			Line2D l = new Line2D.Double(prior, current);
			p.append(l, false);
			prior = current;
		}			
		if (connect) {p.append(new Line2D.Double(prior, first), false);}
		return p;
	}
	
	//TODO: Combine with LINE fixTransform for a more general line-implantation scaling
	private GeneralPath fixTransform(Graphics2D g) {
		if (scaleBy.equals("ALL")) {return path;}
		
		if (scaleBy.equals("NONE")) {
			AffineTransform vt = g.getTransform();
			List newPoints = new ArrayList(points.size());
			
			for (Point2D oldPoint: points) {
				Point2D newPoint = vt.transform(oldPoint, null);
				newPoints.add(newPoint);
			}
			g.setTransform(IDENTITY_TRANSFORM);
			return buildPath(newPoints, connect);
		}
		throw new IllegalArgumentException("Can only scale polyline by ALL or NONE.");
		
	}
	
	public void render(Graphics2D g, AffineTransform base) {
		if (path == null) {return;}
		
		GeneralPath p = fixTransform(g);
		super.render(g, p);
		super.postRender(g, base);
	}
	
	private static final class IdxValuePair {
		final double idx;
		final double value;
		final boolean x;
		final boolean isInsertion;
		
		IdxValuePair(double idx, double value, boolean x) {
			this.idx = idx;
			this.value = value;
			this.x = x;
			isInsertion = (idx == Math.ceil(idx) || idx ==0);//Fractional values and insert at the start of the list
		}
		
		int realIndex() {return (int) Math.ceil(idx);}
		
		Point2D update(Point2D original) {
			if (x) {
				return new Point2D.Double(value, original.getY());
			} else {
				return new Point2D.Double(original.getX(), value);
			}
		}
	}
	
	private static final void updatePoints(List<Point2D> points, Tuple option) {
		List<IdxValuePair> updates = new ArrayList(option.getPrototype().size());
		
		for (String field: option.getPrototype()) {
			final String base = baseName(field);
			if (Xn.is(base) || Yn.is(base) && !base.equals(field)) {
				double idx = index(points, field, false);
				double value = Converter.toDouble(option.get(field));
				updates.add(new IdxValuePair(idx, value, Xn.is(base)));
			}
		}

		for (IdxValuePair p: updates) {
			if (!p.isInsertion) {
				int index = p.realIndex();
				Point2D original = points.get(index);
				points.set(index, p.update(original));
			}
		}
		
		List<Point2D> added = new ArrayList(updates.size());
		for (IdxValuePair p: updates) {
			if (p.isInsertion) {
				int index = p.realIndex();
				Point2D candidate = null;
				if (index < points.size()) {candidate = points.get(index);}
				
				if (!added.contains(candidate)) {
					candidate = new Point2D.Double();
					candidate = p.update(candidate);
					points.add(index, candidate);
					added.add(candidate);
				} else {
					candidate = p.update(candidate);
					points.set(index, candidate);					
				}
			}
		}
	}

	/**What is the X/Y implicit argument (as a number)*/
	private static double index(List<Point2D> points, String att, boolean onlyInt) {
		if (Xn.is(att) || Yn.is(att)) {return -1;}
		
		try {
			double val;
			String arg = nameArgs(att);

			if (arg.equals(NEW_VALUE)) {val = points.size();}
			else if (arg.equals(FINAL_VALUE)) {val = points.size()-1;}
			else {val = Double.parseDouble(arg);}

			if (onlyInt && Math.floor(val) != val) {throw new ImplicitArgumentException(Line.class, att, "Can only reference integers in current call context.", null);}
			return val;
		} catch (Exception e) {throw new ImplicitArgumentException(Poly.class, att, e);}
	}
}
