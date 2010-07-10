package stencil.types.gradient;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;

public class GradientTuple implements Tuple {
	private static String SELF = "self";
	private static final String[] FIELDS = new String[]{SELF};
	private static final Class[] TYPES = new Class[]{GradientTuple.class};
	private static final TuplePrototype PROTOTYPE = new SimplePrototype(FIELDS, TYPES);
	
	
	private final Color one;
	private final Color two;
	private final double length;
	private final boolean absolute;
	private final boolean cyclic;
	
	
	public GradientTuple(Color one, Color two, double length, boolean absolute, boolean cyclic) {
		this.one = one;
		this.two = two;
		this.length = length;
		this.absolute = absolute;
		this.cyclic = cyclic;
	}
	
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}

	public Object get(int idx) throws TupleBoundsException {
		if (idx==0) {return this;}
		throw new TupleBoundsException(idx,size());
	}

	@Override
	public TuplePrototype getPrototype() {return PROTOTYPE;}

	public boolean isDefault(String name, Object value) {return false;}

	public int size() {return PROTOTYPE.size();}
	
	public GradientPaint getPaint(Point2D start, Point2D end) {	
		double p;
		if (length >0 && absolute) {
			p = length/start.distance(end);			
		} else if (length > 0) {
			p = length * start.distance(end);
		} else {
			p =1;
		}
		
		double x = (end.getX() - start.getX()) * p + start.getX();
		double y = (end.getY() - start.getY()) * p + start.getY();
		end = new Point2D.Double(x,y);			
		
		return new GradientPaint(start, one, end, two, cyclic);
	}

}
