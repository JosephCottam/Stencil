package stencil.adapters.java2D.render;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.adapters.java2D.render.mixins.Capper;
import stencil.adapters.java2D.render.mixins.Colorer;
import stencil.adapters.java2D.render.mixins.Implanter;
import stencil.adapters.java2D.render.mixins.Liner;
import stencil.adapters.java2D.render.mixins.Stroker;
import stencil.display.Glyph;
import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.TuplePrototype;

public class LineRenderer implements Renderer<TableView> {
	/**Basic expected table schema.**/
	public static final TuplePrototype<SchemaFieldDef> LINE_SCHEMA = new TuplePrototype(
			ID,
			X.rename("X1"),
			Y.rename("Y1"),
			X.rename("X2"),
			Y.rename("Y2"),
			new SchemaFieldDef("CAP1", "NONE"),
			new SchemaFieldDef("CAP2", "NONE"),
			PEN,
			OPAQUE_PEN_COLOR,
			VISIBLE,
			REGISTRATION,
			IMPLANT,
			Z,
			BOUNDS);

	/**Basic expected table schema.**/
	public static final TuplePrototype<SchemaFieldDef> ARC_SCHEMA = new TuplePrototype(
			ID,
			X.rename("X1"),
			Y.rename("Y1"),
			X.rename("X2"),
			Y.rename("Y2"),
			new SchemaFieldDef("CAP1", "NONE"),
			new SchemaFieldDef("CAP2", "NONE"),
			new SchemaFieldDef("ARC_HEIGHT", 10d),
			PEN,
			OPAQUE_PEN_COLOR,
			VISIBLE,
			REGISTRATION,
			IMPLANT,
			Z,
			BOUNDS);

	private final Colorer pen;
	private final Stroker stroker;
	private final Implanter implanter;
	private final Liner liner;
	private final Capper leftCap;
	private final Capper rightCap;

	private final int boundsIdx;

	public LineRenderer(TuplePrototype<SchemaFieldDef> schema) {
		pen = Colorer.Util.instance(schema, schema.indexOf(PEN_COLOR));
		stroker = Stroker.Util.instance(schema, schema.indexOf(PEN), schema.indexOf(PEN_COLOR));
		leftCap = Capper.Util.instance(schema, schema.indexOf("CAP1"), schema.indexOf(PEN), true);
		rightCap = Capper.Util.instance(schema, schema.indexOf("CAP2"), schema.indexOf(PEN), false);
		liner = Liner.Util.instance(schema, schema.indexOf("ARC_HEIGHT"), schema.indexOf("X1"), schema.indexOf("Y1"), schema.indexOf("X2"), schema.indexOf("Y2"));
		implanter = Implanter.Util.instance(schema, schema.indexOf(IMPLANT));
		boundsIdx = schema.indexOf(BOUNDS);
	}

	@Override
	public void render(TableView layer, Graphics2D g, AffineTransform viewTransform) {
		Stroke orig = g.getStroke();

		for (Glyph glyph: new TupleIterator(layer, layer.renderOrder(), true)) {
			if (!glyph.isVisible() || stroker instanceof Stroker.None) {continue;}

			g.setStroke(stroker.getStroke(glyph));
			pen.setColor(g, glyph);
			Shape line = liner.line(glyph);
			
			LineTransPair lineTrans= implantLine(implanter, viewTransform, line, glyph);
			g.setTransform(lineTrans.trans);
			
			g.draw(lineTrans.line);
			if (!(leftCap instanceof Capper.None)) {g.fill(leftCap.getCap(glyph, line));}
			if (!(rightCap instanceof Capper.None)) {g.fill(rightCap.getCap(glyph, line));}
			
		}
		
		g.setTransform(viewTransform);
		g.setStroke(orig);
		Renderer.Util.debugRender(layer, g);
	}

	@Override
	public void calcFields(TableShare share, AffineTransform viewTransform) {
		Rectangle2D[] bounds = new Rectangle2D[share.size()];
		Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);

		for(StoreTuple glyph: new TupleIterator(share, true)) {
			Shape line = liner.line(glyph);
			Stroke stroke = stroker.getStroke(glyph);
			
			Rectangle2D b = stroke.createStrokedShape(line).getBounds2D();
			bounds[glyph.row()] = b;
			ShapeUtils.add(fullBounds, b);
		}

		Column newCol = share.columns()[boundsIdx].replaceAll(bounds);
		share.setColumn(boundsIdx, newCol);
		share.setBounds(fullBounds);
	}
	
	/**Transform the line and the graphics object to handle the requested implantation relative to the view transform.**/
	public static LineTransPair implantLine(Implanter implanter, AffineTransform viewTransform, Shape line, Glyph glyph) {
		AffineTransform trans = viewTransform;
		if (!(implanter instanceof Implanter.Area)) {
			trans = implanter.implant(new AffineTransform(), viewTransform, glyph);
			try {
				line = trans.createInverse().createTransformedShape(line);
				trans.preConcatenate(viewTransform);
			} catch (NoninvertibleTransformException e) {e.printStackTrace();}//Report and move on
		}
		return new LineTransPair(line, trans);
	}
	
	/**Return values for the implantLine method*/
	public static final class LineTransPair {
		final Shape line;
		final AffineTransform trans;
		public LineTransPair(Shape line, AffineTransform trans) {
			this.line = line;
			this.trans = trans;
		}
	}

	
}
