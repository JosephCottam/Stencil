package stencil.operator.module.provided.layouts;

import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;
import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;
import stencil.types.Converter;

public class Circular extends Layout {
	public static final String NAME = "CircularLayout";
	
	private static final String START_ANGLE = "start";
	private static final String RADIUS = "r";
	private static final String ELEMENT_COUNT = "count";
	private static final String SIZE = "size";
	private static final String RATIO = "ratio";
	
	
	private double cx;			//Centerpoint X-value
	private double cy;			//Centerpoint Y-value
	private double startAngle;	//Where to start the layout
	private double radius;		//How big should the circle be?
	private double size;		//If doing auto-layout, how many pixels in the circumference per element
	private double ratio;		//Proportion of the X to the Y axis
	private int elementCount;	//How many elements to expect
	
	
	public Circular(OperatorData opData, Specializer spec) {
		super(opData);
		startAngle = Converter.toDouble(spec.get(START_ANGLE));
		radius = Converter.toDouble(spec.get(RADIUS));
		size = Converter.toDouble(spec.get(SIZE));
		ratio = Converter.toDouble(spec.get(RATIO));
		elementCount = Converter.toInteger(spec.get(ELEMENT_COUNT));
		cx = Converter.toDouble(spec.get(X));
		cy = Converter.toDouble(spec.get(Y));
	}
	
	public Tuple configure(Double angle, Integer count, Double pad, Double radius, Double ratio, Double cx, Double cy) {
		this.startAngle   = angle != null  ? angle  : startAngle;
		this.radius       = radius != null ? radius : this.radius;
		this.size         = pad != null    ? pad    : this.size;
		this.elementCount = count != null  ? count  : this.elementCount;
		this.ratio		  = ratio != null  ? ratio  : this.ratio;
		this.cx 		  = cx != null     ? cx     : this.cx;
		this.cy 		  = cy != null     ? cy     : this.cy;
		stateID++; //TODO: Only update stateID when something actually changes...
		return new ArrayTuple(startAngle, elementCount, pad, radius, ratio);
	}
	
	public Tuple query(final int idx) {
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
		x = xRadius * Math.cos(angle) + cx;
		y = yRadius * Math.sin(angle) + cy;			

		return new ArrayTuple(x,y);
	}
	
	public Tuple map(int idx) {
		int max = Math.max(elementCount, idx);
		
		if (max != elementCount) {
			elementCount = max;
			stateID++;
		}
		
		return query(idx);
	}
}
