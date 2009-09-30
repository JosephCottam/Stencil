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
package stencil.adapters.java2D.data.immutibles;

import static stencil.parser.ParserConstants.FINAL_VALUE;
import static stencil.parser.ParserConstants.NEW_VALUE;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import stencil.adapters.general.ImplicitArgumentException;
import stencil.adapters.general.Strokes;
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.streams.Tuple;

public abstract class Poly extends Stroked {
	public static class PolyLine extends Poly {
		public PolyLine(Table layer, String id) {super(layer, id, false);}
		public PolyLine(Table layer, PolyLine source, Tuple option) {
			super(layer, source, option, false);
		}
		
		public String getImplantation() {return "POLY_LINE";}
		
		public PolyLine update(Tuple t) throws IllegalArgumentException {
			return new PolyLine(this.getLayer(), this, t);
		}

		public PolyLine updateLayer(Table layer) {
			return new PolyLine(layer, this, Tuple.EMPTY_TUPLE);
		}
	}
	public static class Polygon extends Poly {
		public Polygon(Table layer,String id) {super(layer, id, true);}
		public Polygon(Table layer, Polygon source, Tuple option) {
			super(layer, source, option, true);
		}
		
		public String getImplantation() {return "POLYGON";}

		public Polygon update(Tuple t) throws IllegalArgumentException {
			return new Polygon(this.getLayer(), this, t);
		}

		public Polygon updateLayer(Table layer) {
			return new Polygon(layer, this, Tuple.EMPTY_TUPLE);
		}
	}
	
	public static final Double DEFAULT_COORDINATE_VALUE = 0.0;
	private static final Double UNITIAILZED_COORDINATE  = Double.NaN;
	
	protected static final AttributeList ATTRIBUTES = new AttributeList(Stroked.ATTRIBUTES);
	protected static final AttributeList UNSETTABLES = new AttributeList();
	protected static final Attribute<Double> Xn = new Attribute("Xn", 0d, double.class);
	protected static final Attribute<Double> Yn = new Attribute("Yn", 0d, double.class);
	
	static {
		ATTRIBUTES.add(Xn);
		ATTRIBUTES.add(Yn);
	}
	
	private final List<Point2D> points;
	private final GeneralPath path;	
	private final Rectangle2D bounds;
	private final boolean connect;
	
	public Poly(Table layer, String id, boolean connect) {
		super(layer, id, Strokes.DEFAULT_STROKE, Strokes.DEFAULT_PAINT);
		this.connect = connect;
		
		points = new ArrayList();
		path = buildPath(points, this.connect);
		bounds = path.getBounds2D();
	}
	
	protected Poly(Table layer, Poly source, Tuple option, boolean connect) {
		super(layer, source, option, UNSETTABLES);
		this.connect = connect;
		
		if (changesPoints(option)) {
			points = new ArrayList(source.points);
			updatePoints(points, option);
			path = buildPath(points, this.connect);
		} else {
			points = source.points;
			path = source.path;
		}
		
		bounds = path.getBounds2D();
	}
	
	
	protected AttributeList getAttributes() {return ATTRIBUTES;}	
	public Rectangle2D getBoundsReference() {return bounds;}

	public Object get(String name) {
		String base = baseName(name);
		
		if (base.equals(name)) {
			if (Xn.is(base)) {return bounds.getX();}
			if (Yn.is(base)) {return bounds.getY();}
		}
		
		if (Xn.is(base) || Yn.is(base)) {
			int index = (int) index(points, name, true);
			if (index == points.size()) {return UNITIAILZED_COORDINATE;}
			if (Xn.is(base)) {return points.get(index).getX();}
			if (Yn.is(base)) {return points.get(index).getY();}
		} 
		return super.get(name);
	}	
	
	/**Does the given tuple have up point-related updates?*/
	private static final boolean changesPoints(Tuple t) {
		for (String field: t.getFields()) {
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
	
	public void render(Graphics2D g, AffineTransform base) {
		if (path == null) {return;}
		
		super.render(g, path);
		super.postRender(g, null);
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
		List<IdxValuePair> updates = new ArrayList(option.getFields().size());
		
		for (String field: option.getFields()) {
			final String base = baseName(field);
			if (Xn.is(base) || Yn.is(base) && !base.equals(field)) {
				double idx = index(points, field, false);
				double value = (Double) option.get(field, Double.class);
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
		if (Xn.is(att) && Yn.is(att)) {return -1;}
		
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
