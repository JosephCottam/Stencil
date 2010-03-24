package stencil.adapters.java2D.data.glyphs;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

public class Slice extends Filled {
	private static final AttributeList ATTRIBUTES = new AttributeList(Filled.ATTRIBUTES);
	protected static final AttributeList UNSETTABLES = new AttributeList();

	public static final String IMPLANTATION = "SLICE";
	
	private static final Attribute<Double> X = new Attribute("X", 0d);
	private static final Attribute<Double> Y = new Attribute("Y", 0d);
	private static final Attribute<Double> START = new Attribute("START", 0d);
	private static final Attribute<Double> END = new Attribute("END", 360d);
	private static final Attribute<Double> HEIGHT = new Attribute("HEIGHT", 1);
	private static final Attribute<Double> OUTERX = new Attribute("OUTERX", 1);
	private static final Attribute<Double> OUTERY = new Attribute("OUTERY", 1);
	
	static {
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(START);
		ATTRIBUTES.add(END);
		ATTRIBUTES.add(HEIGHT);
		ATTRIBUTES.add(OUTERX);
		ATTRIBUTES.add(OUTERY);
		
		UNSETTABLES.add(OUTERX);
		UNSETTABLES.add(OUTERY);
	}
	
	private final double x,y;
	private final double start, end;
	private final double height;
	
	private final Arc2D glyph;
	
	public Slice(DisplayLayer layer, String id) {
		super(layer, id);
		x = X.defaultValue;
		y = Y.defaultValue;
		start = START.defaultValue;
		end = END.defaultValue;
		height = HEIGHT.defaultValue;
		super.updateBoundsRef(bounds());
		glyph = makeArc();
	}
	
	private Slice(String id, Slice source) {
		super(id, source);
		
		this.x = source.x;
		this.y = source.y;
		this.start = source.start;
		this.end = source.end;
		this.height= source.height;
		super.updateBoundsRef(source.getBoundsReference());
		glyph = makeArc();
	}
	
	private Slice(Slice source, Tuple option) {
		super(source, option, UNSETTABLES);
		
		this.x = switchCopy(source.x, safeGet(option, X));
		this.y = switchCopy(source.y, safeGet(option, Y));
		this.start = switchCopy(source.start, safeGet(option, START));
		this.end = switchCopy(source.end, safeGet(option, END));
		this.height = switchCopy(source.height, safeGet(option, HEIGHT));		
		super.updateBoundsRef(bounds());
		glyph = makeArc();
	}
	
	public String getImplantation() {return IMPLANTATION;} 
	public AttributeList getPrototype() {return ATTRIBUTES;}
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}

	public Object get(String name) {
		if (X.is(name)) 	  {return x;}
		else if (Y.is(name)) {return y;}
		else if (START.is(name)) {return start;}
		else if (END.is(name)) {return end;}
		else if (HEIGHT.is(name)) {return height;}
		
		else if (OUTERX.is(name)) {return outer().getX();}
		else if (OUTERY.is(name)) {return outer().getY();}
		
		else{return super.get(name);}		
	}
	
	private Point2D outer() {
		Point2D start = glyph.getStartPoint();
		Point2D end = glyph.getEndPoint();
		double x = (start.getX() + end.getX())/2;
		double y = (start.getY() + end.getY())/2;
		return new Point2D.Double(x,y);
	}
	
	private Rectangle2D bounds() {
		double bx = x -(height/2);
		double by = y +(height/2);
		return new Rectangle2D.Double(bx,by,height, height);
	}
	
	private Arc2D makeArc(){return new Arc2D.Double(bounds, start, end-start, Arc2D.PIE);}
	
	public void render(Graphics2D g, AffineTransform base) {
		super.render(g, glyph);
		super.postRender(g, null);
	}

	public Slice update(Tuple t) throws IllegalArgumentException {
		if (Tuples.transferNeutral(t, this)) {return this;}
		return new Slice(this, t);
	}
	
	public Slice updateID(String id) {return new Slice(id, this);}
}
