
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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;

public abstract class Poly extends Stroked {
	public static class PolyLine extends Poly {
		public static final String IMPLANTATION = "POLY_LINE";
		
		public PolyLine(String id) {super(id, false);}
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
		
		public Polygon(String id) {super(id, true);}
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
	
	protected static final AttributeList ATTRIBUTES = new AttributeList(Stroked.ATTRIBUTES);
	protected static final AttributeList UNSETTABLES = new AttributeList();
	protected static final Attribute<Double> XS = new Attribute("XS", DEFAULT_COORDINATE_VALUE, double.class);
	protected static final Attribute<Double> YS = new Attribute("YS", DEFAULT_COORDINATE_VALUE, double.class);
	protected static final Attribute<Double> X = new Attribute("X", 0d, double.class);
	protected static final Attribute<Double> Y = new Attribute("Y", 0d, double.class);
	protected static final Attribute<String> SCALE = new Attribute("SCALE", "ALL");
	protected static final String IDX = "IDX";	//Psudo-property.  Only ever set, never read; identifies WHICH points are being modified by other properties
	
	
	static {
		ATTRIBUTES.add(XS);
		ATTRIBUTES.add(YS);
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(SCALE);
		
		UNSETTABLES.add(X);
		UNSETTABLES.add(Y);
	}
	
	private final List<Double> xs;
	private final List<Double> ys;
	private final GeneralPath path;	
	private final boolean connect;
	private final String scaleBy;
	
	public Poly(String id, boolean connect) {
		super(id);
		this.connect = connect;
		
		xs = new ArrayList();
		ys = new ArrayList();
		path = buildPath(xs,ys, this.connect);
		scaleBy = SCALE.defaultValue;
		super.updateBoundsRef(path.getBounds2D());
	}
	
	
	
	protected Poly(String id, Poly source) {
		super(id, source);
		this.xs = source.xs;
		this.ys = source.ys;
		this.path = source.path;
		this.connect = source.connect;
		this.scaleBy = source.scaleBy;
	}



	protected Poly(Poly source, Tuple option, boolean connect) {
		super(source, option, UNSETTABLES);
		this.connect = connect;
		
		this.scaleBy = switchCopy(source.scaleBy, safeGet(option, SCALE));
		
		if (changesPoints(option)) {
			xs = new ArrayList(source.xs);
			ys = new ArrayList(source.ys);
			updatePoints(xs, ys, option);
			path = buildPath(xs,ys, this.connect);
		} else {
			this.xs = source.xs;
			this.ys = source.ys;
			path = source.path;
		}
		
		super.updateBoundsRef(path.getBounds2D());
	}
	
	
	public AttributeList getPrototype() {return ATTRIBUTES;}
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}

	public Object get(String name) {		
		if (X.is(name)) {return bounds.getX();}
		if (Y.is(name)) {return bounds.getY();}
		if (XS.is(name)) {return xs;}
		if (YS.is(name)) {return ys;}
		if (SCALE.is(name)) {return scaleBy;}
		
		return super.get(name);
	}	

	/**Does the given tuple have up point-related updates?*/
	private static final boolean changesPoints(Tuple t) {
		for (String field: TuplePrototypes.getNames(t.getPrototype())) {
			if (XS.is(field) || YS.is(field)) {return true;}
		}
		return false;
	}
	
	private static GeneralPath buildPath(List<Double> xs, List<Double> ys, boolean connect) {
		assert xs.size() == ys.size() : "Path components of different sizes";
		
		if (xs.size() == 0) {return new GeneralPath();} //Nothing to draw until there are two points...
		
		GeneralPath p = new GeneralPath();
		Point2D prior = new Point2D.Double(xs.get(0), ys.get(0));
		Point2D first = prior;
		for (int i=1; i< xs.size(); i++) {
			Point2D current = new Point2D.Double(xs.get(i), ys.get(i));
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
			GeneralPath newPath = (GeneralPath) path.clone();
			newPath.transform(vt);
			g.setTransform(IDENTITY_TRANSFORM);
			return newPath;
		}
		throw new IllegalArgumentException("Can only scale polyline by ALL or NONE.");
		
	}
	
	public void render(Graphics2D g, AffineTransform base) {
		if (path == null || !visible) {return;}
		GeneralPath p = fixTransform(g);
		super.render(g, p);
		super.postRender(g, base);
	}
	
	private static final void updatePoints(List<Double> xs, List<Double> ys, Tuple option) {
		Object index = option.get(IDX);
		int idx;
		
		//TODO: Support for multi-point updates
		if (index instanceof Tuple) {throw new RuntimeException("Support for multi-update not complete.");}
		if (index.equals("before")) {idx = -1;}
		else if (index.equals("after")) {idx = xs.size();}
		else if (index.equals("first")) {idx = 0;}
		else if (index.equals("last")) {idx = xs.size()-1;}
		else {idx = Converter.toInteger(index.toString());}
		
		if (idx <0) {idx = xs.size() - idx;}//Wrapp around...

		double defaultX = idx < xs.size() ? xs.get(idx) : DEFAULT_COORDINATE_VALUE;
		double defaultY = idx < ys.size() ? ys.get(idx) : DEFAULT_COORDINATE_VALUE;
 		
		double newX = Tuples.safeGet(XS.name, option, option.getPrototype(), defaultX);
		double newY = Tuples.safeGet(YS.name, option, option.getPrototype(), defaultY);

		while (idx>=xs.size()) {	//Ensure requested length
			xs.add(DEFAULT_COORDINATE_VALUE);
			ys.add(DEFAULT_COORDINATE_VALUE);
		}
		
		xs.set(idx, newX);
		ys.set(idx, newY);
	}
}
