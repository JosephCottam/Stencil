package stencil.operator.module.provided.layouts;

import stencil.operator.module.util.OperatorData;
import stencil.operator.util.BasicProject;
import stencil.parser.tree.Specializer;
import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;
import stencil.types.Converter;

public class CircularLayout extends BasicProject {
	private static final String START_ANGLE = "start";
	private static final String RADIUS = "r";
	private static final String ELEMENT_COUNT = "count";
	private static final String SIZE = "size";
	
	private double startAngle;	//Where to start the layout
	private double radius;		//How big should the circle be?
	private double size;		//If doing auto-layout, how many pixels in the circumference per element
	private int elementCount;	//How many elements to expect
	
	public CircularLayout(OperatorData opData, Specializer spec) {
		super(opData);
		startAngle = Converter.toDouble(spec.get(START_ANGLE));
		radius = Converter.toDouble(spec.get(RADIUS));
		size = Converter.toDouble(spec.get(SIZE));
		elementCount = Converter.toInteger(spec.get(ELEMENT_COUNT));
	}
	
	public Tuple configure(Double angle, Integer count, Double pad, Double radius) {
		this.startAngle   = angle != null  ? angle  : startAngle;
		this.radius       = radius != null ? radius : this.radius;
		this.size          = pad != null   ? pad    : this.size;
		this.elementCount = count != null  ? count  : elementCount;
		
		return new ArrayTuple(startAngle, elementCount, pad, radius);
	}
	
	public Tuple query(final int idx) {
		int index = idx +1;

		double percent = (double) index /(double) elementCount;
		double offset = Math.toRadians((percent *360) -90 + startAngle);
		
		double radius = this.radius;		
		if (radius <= 0) {
			double c = elementCount * size;
			radius = c/(2 * Math.PI);
		}
		
		double x = radius * Math.cos(offset);
		double y = radius * Math.sin(offset);
			
		return new ArrayTuple(x,y);
	}

	public Tuple map(int idx) {
		elementCount = Math.max(elementCount, idx);
		return query(idx);
	}
}
