package stencil.modules.layouts;

import java.awt.geom.Point2D;

import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.types.Converter;

/**Lays elements out around an ellipse.
 * 
 * Ellipse can be constructed as a circle (ratio of 1.0).
 * 
 * Size of ellipse can be specified as either the radius of the  major axis
 * OR the number of pixels of circumference be given to each element .
 * 
 * @author jcottam
 *
 */
@Operator(name="CircularLayout", spec="[range: ALL, split: 0, pad: 1, start: 0, r: -1, count: -1, size: 10, ratio: 1, X: 0, Y: 0]")
public class Circular extends Layout {
	public static final String NAME = "CircularLayout";
	
	private static final String START_ANGLE = "start";
	private static final String RADIUS = "r";
	private static final String ELEMENT_COUNT = "count";
	private static final String SIZE = "size";
	private static final String RATIO = "ratio";
	
	
	private double startAngle;	//Where to start the layout
	private double radius;		//How big should the circle be?
	private double size;		//If doing auto-layout, how many pixels in the circumference per element
	private double ratio;		//Proportion of the X to the Y axis
	private int elementCount;	//How many elements to expect
	
	
	public Circular(OperatorData opData, Specializer spec) {
		super(opData, spec);
		startAngle = Converter.toDouble(spec.get(START_ANGLE));
		radius = Converter.toDouble(spec.get(RADIUS));
		size = Converter.toDouble(spec.get(SIZE));
		ratio = Math.pow(Math.sqrt(2), Converter.toDouble(spec.get(RATIO)));
		elementCount = Converter.toInteger(spec.get(ELEMENT_COUNT));
	}
	
	@Facet(memUse="WRITER", prototype="(angle, count, pad, r, ratio)")
	public Tuple configure(Double angle, Integer count, Double pad, Double radius, Double ratio, Double cx, Double cy) {
		this.startAngle   = angle != null  ? angle  : startAngle;
		this.radius       = radius != null ? radius : this.radius;
		this.size         = pad != null    ? pad    : this.size;
		this.elementCount = count != null  ? count  : this.elementCount;
		this.ratio		  = ratio != null  ? ratio  : this.ratio;
		this.origin = new Point2D.Double(cx != null ? cx : origin.getX(), cy != null ? cy : origin.getY());

		stateID++; //TODO: Only update stateID when something actually changes...
		return new ArrayTuple(startAngle, elementCount, pad, radius, ratio, origin);
	}
	
	@Facet(memUse="READER", prototype="(X,Y)")
	public Point2D query(final int idx) {
		int index = idx +1;

		double percent = (double) index /(double) elementCount;
		double angle = Math.toRadians((percent *360) -90 + startAngle);
		
		double radius = this.radius;		
		if (radius <= 0) {
			double c = elementCount * size;
			radius = c/(2 * Math.PI);
		}
		
		double x,y;
		double xRadius = radius;
		double yRadius = radius * ratio;
		x = xRadius * Math.cos(angle) + origin.getX();
		y = yRadius * Math.sin(angle) + origin.getY();			

		return new Point2D.Double(x,y);
	}
	
	@Facet(memUse="WRITER", prototype="(X,Y)")
	public Point2D map(int idx) {
		int max = Math.max(elementCount, idx);
		
		if (max != elementCount) {
			elementCount = max;
			stateID++;
		}
		
		return query(idx);
	}
}
