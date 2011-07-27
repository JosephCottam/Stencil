package stencil.adapters.java2D.render;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.adapters.java2D.render.mixins.Colorer;
import stencil.adapters.java2D.render.mixins.Implanter;
import stencil.adapters.java2D.render.mixins.Stroker;
import stencil.display.Glyph;
import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.prototype.TuplePrototype;

//TODO: Support filled poly
public class PolyRenderer implements Renderer<TableView> {
	/**Basic expected table schema.**/
	public static final TuplePrototype<SchemaFieldDef> SCHEMA = new TuplePrototype(
				ID,
				X,
				Y,
				new SchemaFieldDef("GROUP", ""),	//What group does this belong to?
				new SchemaFieldDef("ORDER", null, Object.class),	//What relative order in this group is this entry
				new SchemaFieldDef("CONNECT", false),
				new SchemaFieldDef("SEGMENT", false),
				OPAQUE_PEN_COLOR,
				PEN,
				VISIBLE,
				IMPLANT,
				Z,
				BOUNDS);
	

	
    private final Colorer lineColor;
    private final Stroker stroker;
    private final Connector connector;
    private final Implanter implanter;
    private final int groupIdx;
    private final int orderIdx;
    private final int connectedIdx;
    private final int segmentedIdx;
    private final int xIdx, yIdx;
    private final int boundsIdx;
    private final Comparator orderSorter;
    
   
    
	public PolyRenderer(TuplePrototype<SchemaFieldDef> schema) {
		xIdx = schema.indexOf(X);
		yIdx = schema.indexOf(Y);
		groupIdx = schema.indexOf("GROUP");
		orderIdx = schema.indexOf("ORDER");
		connectedIdx = schema.indexOf("CONNECT");
		segmentedIdx = schema.indexOf("SEGMENT");

		
		lineColor = Colorer.Util.instance(schema, schema.indexOf(OPAQUE_PEN_COLOR));
		stroker = Stroker.Util.instance(schema, schema.indexOf(PEN), schema.indexOf(PEN_COLOR));
		implanter = Implanter.Util.instance(schema, schema.indexOf(IMPLANT));
		connector = new Connector(xIdx, yIdx);
		orderSorter = new TupleSorter(orderIdx);
		
		boundsIdx = schema.indexOf(BOUNDS);
	}

	@Override
	public void render(TableView layer, Graphics2D g, AffineTransform viewTransform) {
		if (stroker instanceof Stroker.None) {return;}
		
		//Gather
		Map<String, List<Glyph>> groups = new HashMap();
		for (Glyph glyph: new TupleIterator(layer, layer.renderOrder())) {
			String groupID = (String) glyph.get(groupIdx);
			List<Glyph> group = groups.get(groupID);
			if (group == null) {
				group = new ArrayList(); 
				groups.put(groupID, group);
			}
			group.add(glyph);
		}
	
		//Sort
		for (List<Glyph> group: groups.values()) {
			Collections.sort(group, orderSorter);
		}
		
		//Render
		for (List<Glyph> group: groups.values()) {
			if (group.size() <2) {continue;}
			if (((Boolean)group.get(0).get(segmentedIdx))) {segmented(group, g, viewTransform);}
			else {unsegmented(group, g, viewTransform);}
		}
		Renderer.Util.debugRender(layer, g);
	}
	

	@Override
	public void calcFields(TableShare share) {
		Rectangle2D[] bounds = new Rectangle2D[share.size()];
		Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);
		for(StoreTuple glyph: new TupleIterator(share, true)) {
			Rectangle2D b = new Rectangle2D.Double((Double) glyph.get(xIdx), -((Double) glyph.get(yIdx)),1,1);
			bounds[glyph.row()] = b;
			ShapeUtils.add(fullBounds, b);
		}
		Column newCol = share.columns()[boundsIdx].replaceAll(bounds);
		share.setColumn(boundsIdx, newCol);
		share.setBounds(fullBounds);
	}
	
	private void unsegmented(List<Glyph> group, Graphics2D g, AffineTransform viewTransform) {
		//Construct a single path

		GeneralPath path = new GeneralPath();
		Glyph start = group.get(0);
		path.moveTo((Double) start.get(xIdx), -((Double) start.get(yIdx)));
		for (int i=1; i< group.size(); i++) {
			Glyph point = group.get(i);
			if (!start.isVisible()) {continue;}
			path.lineTo((Double) point.get(xIdx), -((Double) point.get(yIdx)));
		}
		if ((Boolean) start.get(connectedIdx)) {
			Glyph end = group.get(group.size()-1);
			if (end.isVisible()) {
				path.lineTo((Double) start.get(xIdx), -((Double) start.get(yIdx)));
			}
		}
		
		Shape s = LineRenderer.implantLine(implanter, viewTransform, path, start, g);
		
		//Render the path
		Stroke orig = g.getStroke();
		g.setStroke(stroker.getStroke(start));
		lineColor.setColor(g, start);
		g.draw(s);
		g.setStroke(orig);
		g.setTransform(viewTransform);
	}
	
	private void segmented(List<Glyph> group, Graphics2D g, AffineTransform viewTransform) {
		Stroke orig = g.getStroke();
		for (int i=0; i< group.size(); i++) {
			Glyph start = group.get(i);
			if (!start.isVisible()) {continue;}
	
			int endIdx = (i+1)%(group.size());
			Glyph end = group.get(endIdx);
			if (endIdx == 0 && !((Boolean) end.get(connectedIdx))) {continue;}
	
			g.setStroke(stroker.getStroke(start));
			lineColor.setColor(g, start);
			Shape line = connector.line(start, end);
			line = LineRenderer.implantLine(implanter, viewTransform, line, start, g);
			g.draw(line);
			g.setTransform(viewTransform);
		}
		g.setStroke(orig);
	}
	
	private final class Connector {
		final int xIdx, yIdx;
		public Connector(int xIdx, int yIdx) {
			this.xIdx = xIdx;
			this.yIdx = yIdx;
		}
		public Line2D line(Tuple start, Tuple end) {
			Double y1 = (Double) start.get(yIdx);
			Double y2 = ((Double) end.get(yIdx));
			return new Line2D.Double((Double) start.get(xIdx), -y1, (Double) end.get(xIdx), -y2);
		}
	}
	
}
