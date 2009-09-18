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
import java.awt.geom.Rectangle2D;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.ImplicitArgumentException;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.types.Converter;

public abstract class Poly extends Stroked {
	public static class PolyLine extends Poly {
		public PolyLine(String id) {super(id, false);}
		public String getImplantation() {return "POLY_LINE";}
	}
	public static class Polygon extends Poly {
		public Polygon(String id) {super(id, true);}
		public String getImplantation() {return "POLYGON";}
	}
	
	public static final Double DEFAULT_COORDINATE_VALUE = 0.0;
	private static final Double UNITIAILZED_COORDINATE  = Double.NaN;
	
	protected static final AttributeList attributes;
	protected static final Attribute Xn = new Attribute("Xn", 0, double.class);
	protected static final Attribute Yn = new Attribute("Yn", 0, double.class);
	
	static {
		attributes = new AttributeList(Stroked.attributes);

		attributes.add(Xn);
		attributes.add(Yn);
		
		attributes.remove(StandardAttribute.HEIGHT);
		attributes.remove(StandardAttribute.WIDTH);
	}
	
	private List<Point2D.Double> points = new CopyOnWriteArrayList<Point2D.Double>(); //TODO: Change to something faster when Polys become immutible..
	private GeneralPath cache;	
	private Rectangle2D cacheBounds;
	private boolean connect;
	
	public Poly(String id, boolean connect) {
		super(id);
		this.connect = connect;
	}
	
	
	protected AttributeList getAttributes() {return attributes;}

	public Object get(String name) {
		String base = baseName(name);
		
		if (base.equals(name)) {
			if (Xn.is(base)) {return pointsArrays()[0];}
			if (Yn.is(base)) {return pointsArrays()[1];}
		}
		
		if (Xn.is(base) || Yn.is(base)) {
			int index = (int) index(name, true);
			if (index == points.size()) {return UNITIAILZED_COORDINATE;}
			if (Xn.is(base)) {return points.get(index).x;}
			if (Yn.is(base)) {return points.get(index).y;}
		} 
		return super.get(name);
	}
	
	private Double[][] pointsArrays() {
		List<Point2D.Double> pts = points;
		Double[][] vs = new Double[2][pts.size()];
		
		for (int i=0; i< pts.size(); i++) {
			vs[0][i] = pts.get(i).x;
			vs[1][i] = pts.get(i).y;
		}
		return vs;
	}
	
	public void fromPointsArray(boolean x, Double[] halfPoints) {
		boolean clear = points.size()!=halfPoints.length;
		
		for (int i=0; i< halfPoints.length; i++) {
			Point2D.Double p = points.get(i);
			if (x) {
				p.x = halfPoints[i];
				if (clear) {p.y = DEFAULT_COORDINATE_VALUE;}
			} else {
				p.y = halfPoints[i];
				if (clear) {p.y = DEFAULT_COORDINATE_VALUE;}
			}		
		}
	}
	
	
	public void set(String name, Object value) {
		String base = baseName(name);

			 if (Xn.is(base) && Xn.is(name)) {fromPointsArray(true, (Double[]) value); cachePath();}
		else if (Yn.is(base) && Yn.is(name)) {fromPointsArray(true, (Double[]) value); cachePath();}
		else if (Xn.is(base) && !Xn.is(name)) {this.updatePoints(Converter.toDouble(value), Double.NaN, index(name, false)); cachePath();}
		else if (Yn.is(base) && !Yn.is(name)) {this.updatePoints(Double.NaN, Converter.toDouble(value), index(name, false)); cachePath();}
		else {super.set(name, value);}
	}
	
	public double getHeight() {return cacheBounds == null? 0 : cacheBounds.getHeight();}	
	public double getWidth() {return cacheBounds == null? 0 : cacheBounds.getWidth();}

	private void cachePath() {
		if (points.size() < 1) {return;} //Nothing to draw until there are two points...
		
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
		cache =p;
		
		cacheBounds = p.getBounds2D();
		this.x = cacheBounds.getX();
		this.y = cacheBounds.getY();
	}
	
	public void render(Graphics2D g, AffineTransform base) {
		GeneralPath p = cache;
		if (p == null) {return;}
		
		super.preRender(g);
		super.render(g, p);
		super.postRender(g, base);
	}
	
	/**What is the X/Y implicit argument (as a number)*/
	private double index(String att, boolean onlyInt) {
		if (Xn.is(att) && Yn.is(att)) {return -1;}
		
		try {
			double val;
			int nextIndex = points.size();
			boolean isX = att.startsWith(StandardAttribute.X.name());
			String arg = nameArgs(att);

			if (arg.equals(NEW_VALUE)) {
				//Add if its something new, re-use if its something old, but not yet completed
				if (nextIndex ==0) {val =nextIndex;}
				else if ((isX && !UNITIAILZED_COORDINATE.equals(points.get(nextIndex -1).x)) ||
				 	     (!isX && !UNITIAILZED_COORDINATE.equals(points.get(nextIndex -1).y))) {val = nextIndex;}
				else if ((isX && UNITIAILZED_COORDINATE.equals(points.get(nextIndex -1).x)) ||
						 (!isX && UNITIAILZED_COORDINATE.equals(points.get(nextIndex -1).y))) {val = nextIndex-1;}
				else {throw new Error("Reached a conditional case that is supposed to be impossible...");}

			} else if (arg.equals(FINAL_VALUE)) {val = points.size()-1;}
			else {val = Double.parseDouble(arg);}

			if (onlyInt && Math.floor(val) != val) {throw new ImplicitArgumentException(Line.class, att, "Can only reference integers in current call context.", null);}
			return val;
		} catch (Exception e) {throw new ImplicitArgumentException(this.getClass(), att, e);}
	}

	/**
	 *
	 * @param x Prototype x value (Nan if the current value, or generic default should be used)
	 * @param y Prototype y value (Nan if the current value, or generic default should be used)
	 * @param idxGuess Which point is being updated (may not be exact idx as fractional values indicate insertion)?
	 */
	protected void updatePoints(double x, double y, double idxGuess) {
		assert !(Double.isNaN(x) && Double.isNaN(y)) : "Cannot pass NaN for both and Y";

		Point2D.Double p;
		boolean updateX = !Double.isNaN(x);
		int idx = (int) Math.floor(idxGuess);

		//Before we move on, make sure the last thing added with 'new' is finsihed off
		if (points.size() >0) {
			p = points.get(points.size()-1);
			if (p.x == UNITIAILZED_COORDINATE) {p.x = DEFAULT_COORDINATE_VALUE;}
			if (p.y == UNITIAILZED_COORDINATE) {p.y = DEFAULT_COORDINATE_VALUE;}
		}

		if (Math.floor(idxGuess) != idx) {
			//Create a new points, put it between two existing ones
			idx = idx+1;
			p = new Point2D.Double(UNITIAILZED_COORDINATE, UNITIAILZED_COORDINATE);

			if (updateX) {p.x = x;}
			else {p.y = y;}

			if (idx < points.size()) {points.add(idx, p);}
			else {points.add(p);}


		} else {
			//Update existing point or append to the end
			if (idx < points.size()) {
				p = points.get(idx);
			} else {
				p = new Point2D.Double(UNITIAILZED_COORDINATE, UNITIAILZED_COORDINATE);
			}

			if (updateX) {p.x = x;}
			else {p.y = y;}

			if (idx < points.size()) {points.set(idx, p);}
			else {points.add(p);}
		}
	}
}
