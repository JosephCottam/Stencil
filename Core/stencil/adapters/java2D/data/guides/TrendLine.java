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
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;

public class TrendLine  implements Guide2D {
	public static final String IMPLANTATION_NAME = "TREND_LINE";
	private static String[] UPDATE_NAMES = new String[]{"X.1", "Y.1", "X.2", "Y.2"};

	protected Glyph2D prototypeLine = new Line(null, "prototype");
	
	protected final TupleSorter sorter;
	protected final int x_idx;
	protected final int y_idx;
	
	protected final List<Glyph2D> marks = new ArrayList();
	protected final Rectangle2D bounds = new Rectangle2D.Double();

	
	public TrendLine(Guide guideDef) {
		TuplePrototype p = guideDef.getPrototype();		//Get input prototype
		x_idx = ArrayUtil.indexOf("X", TuplePrototypes.getNames(p));
		y_idx = ArrayUtil.indexOf("Y", TuplePrototypes.getNames(p));

		sorter=new TupleSorter(x_idx);
	}
	
	public synchronized void setElements(List<Tuple> elements) {
		marks.clear();
		if (elements.size() == 0) {return;}
		
		//First & final trendline (worst possible...)
		Collections.sort(elements, sorter);
		Tuple first = elements.get(0);
		Tuple last = elements.get(elements.size()-1);
		
		Point2D start = new Point2D.Double(Converter.toDouble(first.get(x_idx)), Converter.toDouble(first.get(y_idx)));
		Point2D end = new Point2D.Double(Converter.toDouble(last.get(x_idx)), Converter.toDouble(last.get(y_idx)));

		Tuple update = new PrototypedTuple(UPDATE_NAMES, new Double[]{start.getX(), start.getY(), end.getX(), end.getY()});
		Glyph2D mark = prototypeLine.update(Tuples.sift("line.", first));
		mark = mark.update(update);

		marks.add(mark);
		bounds.setRect(mark.getBoundsReference());
		
		
		
		//Linear regression
		
		//LOESS
	}

	public Rectangle2D getBoundsReference() {return bounds;}

	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		for (Glyph2D mark: marks) {
			mark.render(g, viewTransform);
		}
	}

}
