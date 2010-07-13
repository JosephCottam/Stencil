package stencil.adapters.java2D.data.guides;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.adapters.java2D.data.glyphs.Line;
import stencil.parser.tree.Guide;
import stencil.tuple.Tuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;
import static stencil.adapters.java2D.data.guides.GuideUtils.SAMPLE_KEY;

public class TrendLine  extends Guide2D {
	public static final String IMPLANTATION_NAME = "TREND_LINE";
	private static String[] UPDATE_NAMES = new String[]{"X.1", "Y.1", "X.2", "Y.2"};
	private static enum SAMPLE_TYPE {LINEAR, SIMPLE}

	protected Glyph2D prototypeLine = new Line(null, "prototype");
	
	protected final TupleSorter sorter;
	protected final int x_idx;
	protected final int y_idx;
	
	protected final List<Glyph2D> marks = new ArrayList();
	protected final Rectangle2D bounds = new Rectangle2D.Double();
	protected final SAMPLE_TYPE sampleType;
	
	public TrendLine(Guide guideDef) {
		super(guideDef);
		TuplePrototype p = guideDef.getPrototype();		//Get input prototype
		x_idx = ArrayUtil.indexOf("X", TuplePrototypes.getNames(p));
		y_idx = ArrayUtil.indexOf("Y", TuplePrototypes.getNames(p));

		sorter=new TupleSorter(x_idx);
		
		if (guideDef.getSpecializer().getMap().containsKey(SAMPLE_KEY)) {
			String v = guideDef.getSpecializer().getMap().get(SAMPLE_KEY).toString();
			sampleType = SAMPLE_TYPE.valueOf(v);
		} else {
			sampleType = SAMPLE_TYPE.LINEAR;
		}
	}
	
	public synchronized void setElements(List<Tuple> elements) {
		marks.clear();
		if (elements.size() == 0) {return;}
		
		Glyph2D mark;
		
		switch (sampleType) {
			case SIMPLE: mark = firstAndFinal(elements); break; 
			case LINEAR: mark = linear(elements); break;
			default: mark = linear(elements); break;
		}
		
		marks.add(mark);
		bounds.setRect(mark.getBoundsReference());
	}

	/**	Least-squares linear regression
	 * implemented per http://en.wikipedia.org/wiki/Linear_least_squares		
	**/
	private final Glyph2D linear(List<Tuple> elements) {
		double sx=0, sx2=0, sy=0, sxy=0;
		double b1=0, b2=0;
		final double n = elements.size();
		
		for (Tuple t: elements) {
			double x = Converter.toDouble(t.get(x_idx));
			double y = Converter.toDouble(t.get(y_idx));
			
			sx += x;
			sx2 += x*x;
			sy += y;
			sxy += (x*y);
		}
		
		b2 = ((n*(sxy)) - (sy*sx))/(n*(sx2) - (sx*sx));
		b1 = (sy/n) - (b2 * (sx/n));
		
		double slope = b2;
		double intercept = b1;

		PointPair p=getBoundaries(elements);
		
		double x1 = p.start.getX(); 		
		double y1 = slope*x1 + intercept;
		
		double x2 = p.end.getX();
		double y2 = slope*x2 + intercept;
		
		Glyph2D mark = prototypeLine.update(Tuples.sift("line.", elements.get(0)));
		Tuple update = new PrototypedTuple(UPDATE_NAMES, new Double[]{x1,y1,x2,y2});
		mark = mark.update(update);
		return mark;
	}

	
	/**Takes the left-most and right most values and draws a line between them.
	 * @param elements
	 * @return
	 */ 
	private final Glyph2D firstAndFinal(List<Tuple> elements) {
		PointPair p=getBoundaries(elements);

		Tuple update = new PrototypedTuple(UPDATE_NAMES, new Double[]{p.start.getX(), p.start.getY(), p.end.getX(), p.end.getY()});
		Glyph2D mark = prototypeLine.update(Tuples.sift("line.", elements.get(0)));
		mark = mark.update(update);
		return mark;
	}
	
	private static final class PointPair {
		public final Point2D start;
		public final Point2D end;
		public PointPair(Point2D start, Point2D end) {
			this.start = start;
			this.end = end;
		}
	}

	private final PointPair getBoundaries(List<Tuple> elements) {
		Collections.sort(elements, sorter);
		Tuple first = elements.get(0);
		Tuple last = elements.get(elements.size()-1);

		Point2D start = new Point2D.Double(Converter.toDouble(first.get(x_idx)), Converter.toDouble(first.get(y_idx)));
		Point2D end = new Point2D.Double(Converter.toDouble(last.get(x_idx)), Converter.toDouble(last.get(y_idx)));
		return new PointPair(start,end);
	}
	
	
	public Rectangle2D getBoundsReference() {return bounds;}

	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		for (Glyph2D mark: marks) {
			mark.render(g, viewTransform);
		}
	}

}
