package stencil.adapters.java2D.render.guides;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.SchemaFieldDef;
import stencil.interpreter.tree.Guide;
import stencil.parser.ParserConstants;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;


//TODO: Turn into a line or poly-point (depending on the complexity of the fit function)
///			Fit function could produce a map-merge tuple when it needs multiple points 
public class TrendLine extends Guide2D {
	private static enum SAMPLE_TYPE {LINEAR, SIMPLE, LAYER}
	private static final String[] SIMPLE_FIELDS = new String[]{"X1","Y1", "X2", "Y2","ID"};
	
	private  final TupleSorter sorter;
	private final int x_idx;
	private final int y_idx;

	private final Table data;
	private final Renderer renderer;
	private final PrototypedTuple updateMask;

	protected final SAMPLE_TYPE sampleType;


	public TrendLine(Guide guideDef) {
		super(guideDef);
		TuplePrototype p = guideDef.rule().prototype();
		x_idx = ArrayUtil.indexOf("X", TuplePrototypes.getNames(p));
		y_idx = ArrayUtil.indexOf("Y", TuplePrototypes.getNames(p));

		sorter=new TupleSorter(x_idx);
		String sampleString = (String) guideDef.selectors().get(ParserConstants.IDENTIFIER_FIELD);	//The only valid target for a summarization guide  
		SAMPLE_TYPE sampleType;
		
		try {sampleType = SAMPLE_TYPE.valueOf(sampleString.toUpperCase());}
		catch (Exception e) {throw new RuntimeException("Trend-line sample type unknown: " + sampleString);}
		if (sampleType == SAMPLE_TYPE.LAYER) {sampleType = SAMPLE_TYPE.SIMPLE;}
		this.sampleType = sampleType;
		
		
		data = LayerTypeRegistry.makeTable(guideDef.identifier(), "LINE");
		renderer = LayerTypeRegistry.makeRenderer(data.prototype());
		updateMask = SchemaFieldDef.asTuple(data.prototype());
	}
	
	@Override
	public void setElements(List<PrototypedTuple> elements, Rectangle2D parentBounds) {
		PrototypedTuple mark;
		switch (sampleType) {
			case SIMPLE: mark = firstAndFinal(elements); break; 
			case LINEAR: mark = linear(elements); break;
			default: mark = linear(elements); break;
		}
		
		data.update(Tuples.merge(updateMask, mark));		
		Table.Util.genChange(data, renderer);
	}


	/**	Least-squares linear regression
	 * implemented per http://en.wikipedia.org/wiki/Linear_least_squares		
	**/
	private final PrototypedTuple linear(List<PrototypedTuple> elements) {
		double sx=0, sx2=0, sy=0, sxy=0;
		final double n = elements.size();
		
		for (PrototypedTuple t: elements) {
			double x = Converter.toDouble(t.get(x_idx));
			double y = Converter.toDouble(t.get(y_idx));
			
			sx += x;
			sx2 += x*x;
			sy += y;
			sxy += (x*y);
		}
		
		double slope = ((n*(sxy)) - (sy*sx))/(n*(sx2) - (sx*sx));
		double intercept = (sy/n) - (slope * (sx/n));
		
		PointPair p=getBoundaries(elements);
		
		double x1 = p.start.getX(); 		
		double y1 = (slope*x1 + intercept);
		
		double x2 = p.end.getX();
		double y2 = (slope*x2 + intercept);
		
		return new PrototypedArrayTuple(SIMPLE_FIELDS, new Double[]{x1,y1,x2,y2, 0d});
	}

	
	/**Takes the left-most and right most values and draws a line between them.
	 * @param elements
	 * @return
	 */ 
	private final PrototypedTuple firstAndFinal(List<PrototypedTuple> elements) {
		PointPair p=getBoundaries(elements);
		return new PrototypedArrayTuple(SIMPLE_FIELDS, new Double[]{p.start.getX(), p.start.getY(), p.end.getX(), p.end.getY(), 0d});
	}
	
	private static final class PointPair {
		public final Point2D start;
		public final Point2D end;
		public PointPair(Point2D start, Point2D end) {
			this.start = start;
			this.end = end;
		}
	}

	private final PointPair getBoundaries(List<PrototypedTuple> elements) {
		if (elements.size() ==0) {return new PointPair(new Point2D.Double(0,0), new Point2D.Double(0,0));}
		
		Collections.sort(elements, sorter);
		PrototypedTuple first = elements.get(0);
		PrototypedTuple last = elements.get(elements.size()-1);

		Point2D start = new Point2D.Double(Converter.toDouble(first.get(x_idx)), Converter.toDouble(first.get(y_idx)));
		Point2D end = new Point2D.Double(Converter.toDouble(last.get(x_idx)), Converter.toDouble(last.get(y_idx)));
		return new PointPair(start,end);
	}
	
	@Override
	public Rectangle2D getBoundsReference() {return data.getBoundsReference();}

	@Override
	public void render(Graphics2D g, AffineTransform viewTransform) {
		renderer.render(data.tenured(), g, viewTransform);
	}

}
