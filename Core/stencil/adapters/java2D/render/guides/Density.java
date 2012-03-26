package stencil.adapters.java2D.render.guides;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.Guide2D;
import stencil.display.SchemaFieldDef;
import stencil.interpreter.tree.Guide;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;

public class Density extends Guide2D {
	public static enum AXIS {X,Y}
	
	private  final TupleSorter sorter;

	private final Renderer renderer;
	private final PrototypedTuple updateMask;
	private final int binSpan = 1;		//TODO: Add control to specializer....
	private final int axis_idx;
	protected final AXIS axis;

	private Table data;


	public Density(Guide guideDef) {
		super(guideDef);
		TuplePrototype p = guideDef.rule().prototype();
		final String axisTag = guideDef.identifier().substring(guideDef.identifier().indexOf(":")+2); 
		axis = AXIS.valueOf(axisTag.substring(0,1));

		if (axis == AXIS.X) {
			axis_idx = ArrayUtil.indexOf("X", TuplePrototypes.getNames(p));
		} else {
			axis_idx = ArrayUtil.indexOf("Y", TuplePrototypes.getNames(p));	
		}
		sorter = new TupleSorter(axis_idx);		
		data = LayerTypeRegistry.makeTable(guideDef.identifier(), "SHAPE");
		
		renderer = LayerTypeRegistry.makeRenderer(data.prototype());
		updateMask = SchemaFieldDef.asTuple(data.prototype());
	}
	
	@Override
	public void setElements(List<PrototypedTuple> elements, Rectangle2D parentBounds, AffineTransform viewTransform) {
		data = LayerTypeRegistry.makeTable(guideDef.identifier(), "SHAPE");
		if (elements.size() !=0) {
			elements = new ArrayList(elements);	//Copy so sorting doesn't mess things up...
			Collections.sort(elements, sorter);
			double minX = parentBounds.getMinX();
			double minY = parentBounds.getMaxY();
			
			
			int[] binCounts = binCounts(elements);
			for (int i=0; i< binCounts.length; i++) {
				int count = binCounts[i];
				PrototypedTuple u = makeBar(i, count, minX, minY);
				PrototypedTuple m = Tuples.mergeAll(updateMask, u);	//TODO: Figure out how post-processing rules should be applied...
				data.update(m);
			}
		}
		Table.Util.genChange(data, renderer, viewTransform);
	}
	
	
	private int[] binCounts(List<? extends Tuple> elements) {
		return binCounts(elements, axis_idx, binSpan);
	}
	
	public static int[] binCounts(List<? extends Tuple> elements, int axis_idx, int binSpan) {
		double min = Converter.toDouble(elements.get(0).get(axis_idx));
		double max = Converter.toDouble(elements.get(elements.size()-1).get(axis_idx));
		double span = max - min;
		
		int binCount = (int) Math.ceil(span/binSpan) +1;
		int[] counts = new int[binCount];
		
		for (Tuple t: elements) {
			double axisV = Converter.toDouble(t.get(axis_idx));
			int bin = (int) (axisV/binSpan);
			if (bin <0) {bin =0;}
			if (bin == counts.length) {bin = counts.length-1;}
			
			counts[bin]++;
		}
		return counts;
	}
	
	private final String[] BAR_FIELDS = new String[]{"ID", "X","Y","WIDTH", "HEIGHT","REGISTRATION", "SHAPE"};
	public PrototypedTuple makeBar(int offset, int count, double minX, double minY) {
		double x,y,w,h;
		String r;
		String shape = "RECTANGLE";

		switch (axis){ 
			case X:
				x = offset*binSpan;
				y = -minY;
				h = count;
				w = binSpan;
				r = "TOP_LEFT";
				break;
			case Y:
				x = minX;
				y = offset*binSpan;
				h = binSpan;
				w = count;
				r = "TOP_RIGHT";
				break;
			default: 
				throw new RuntimeException("Density plot requested for invalid axis.");
		}
		
		Object[] values = new Object[]{offset,x,y,w,h,r, shape};
		return new PrototypedArrayTuple(BAR_FIELDS, values);
	}
	
	
	@Override
	public Rectangle2D getBoundsReference() {return data.getBoundsReference();}

	@Override
	public void render(Graphics2D g, AffineTransform viewTransform) {
		renderer.render(data.tenured(), g, viewTransform);
	}

}
