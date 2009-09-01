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

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.ImplicitArgumentException;
import stencil.adapters.piccoloDynamic.util.Attribute;
import stencil.adapters.piccoloDynamic.util.Attributes;
import stencil.types.Converter;

import edu.umd.cs.piccolo.util.PBounds;
import static stencil.parser.ParserConstants.FINAL_VALUE;
import static stencil.parser.ParserConstants.NEW_VALUE;

public abstract class AbstractPoly extends Path {
	public static final Double DEFAULT_COORDINATE_VALUE = 0.0;
	private static final Double INVALID_COORDINATE_VALUE = Double.NaN;
	private static final Double UNITIAILZED_COORDINATE  = Double.NaN;

	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		for (Attribute a : Path.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.Xn, "getXArray", "setXArray", AbstractPoly.class, true, null, Double.class));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.Yn, "getYArray", "setYArray", AbstractPoly.class, true, null, Double.class));
	}

	public static class PolyLine extends AbstractPoly {
		public static final String IMPLANTATION_NAME = "POLY_LINE";
		public PolyLine(String id) {super(id, IMPLANTATION_NAME, false);}
	}

	public static class Poly extends AbstractPoly {
		public static final String IMPLANTATION_NAME = "POLY";
		public Poly(String id) {super(id, IMPLANTATION_NAME, false);}
	}


	protected ArrayList<Point2D.Double> points;
	protected boolean closePath;

	protected AbstractPoly(String id, String implantationName,boolean closePath) {
		super(id, implantationName, PROVIDED_ATTRIBUTES);
		points = new ArrayList<Point2D.Double>(5);
		this.closePath = closePath;
	}


	/**Either move the line (attribute Y) or move the line end (Y1 or Y2)*/
	public void setYArray(String att, double value) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple Y in indexed Y accessor";
		double idx = index(att, false);
		double y = Converter.toDouble(value);
		updatePoints(INVALID_COORDINATE_VALUE, y, idx);
	}


	public Object getYArray(String att) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple Y in indexed Y accessor";
		int idx = (int) index(att, true);

		if (idx >= points.size()) {return INVALID_COORDINATE_VALUE;}
		if (idx >= 0) {return points.get(idx).y;}
		
		Double[] ys = new Double[points.size()];
		for (int i=0; i< points.size(); i++) {ys[i] = points.get(i).y;}
		return ys;
		
	}

	/**Either move the line (attribute X) or move the line end (X1 or X2)*/
	public void setXArray(String att, double value) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple X in indexed X accessor";
		double idx = index(att, false);
		double x = Converter.toDouble(value);
		updatePoints(x, INVALID_COORDINATE_VALUE, idx);
	}

	public Object getXArray(String att) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple X in indexed X accessor";
		int idx = (int) index(att, true);
		if (idx >= points.size()) {return INVALID_COORDINATE_VALUE;}
		if (idx >= 0) {return points.get(idx).x;}

		
		Double[] xs = new Double[points.size()];
		for (int i=0; i< points.size(); i++) {xs[i] = points.get(i).y;}
		return xs;
	}


	/**What is the X/Y implicit argument (as a number)*/
	private double index(String att, boolean onlyInt) {
		if (isXn(att) || isYn(att)) {return -1;}
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
		} catch (Exception e) {throw new ImplicitArgumentException(Line.class, att, e);}
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

		buildPath();
	}

	/**Construct a path from the points supplied.*/
	protected void buildPath() {
		if (points.size() == 0) {super.setPath(new GeneralPath()); return;}

		Point2D.Double p = points.get(0);
		GeneralPath newPath = new GeneralPath();

		newPath.moveTo((float) p.x, (float)p.y);
		for (int i=1; i< points.size(); i++) {
			p = points.get(i);
			newPath.lineTo((float) p.x, (float)p.y);
		}

		if (closePath) {newPath.closePath();}

		super.setPath(newPath);
		this.signalBoundsChanged();
	}

	public PBounds getBoundsReference() {return path.getBoundsReference();}

	private boolean isXn(String att) {return att.equals(StandardAttribute.Xn.name());}
	private boolean isYn(String att) {return att.equals(StandardAttribute.Yn.name());}
}
