package stencil.adapters.java2D.render;

import static stencil.adapters.Adapter.REFERENCE_GRAPHICS;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.regex.Pattern;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.adapters.java2D.render.mixins.Colorer;
import stencil.adapters.java2D.render.mixins.Placer;
import stencil.adapters.java2D.render.mixins.Registerer;
import stencil.adapters.java2D.render.mixins.Rotater;
import stencil.adapters.java2D.render.mixins.Implanter;
import stencil.display.Glyph;
import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.font.FontTuple;
import stencil.util.DoubleDimension;

public class TextRenderer implements Renderer<TableView> {
	private static final String LAYOUT = "#Layout";
	
	/**Basic expected table schema.**/
	public static final TuplePrototype<SchemaFieldDef> SCHEMA = new TuplePrototype(
				ID,
				new SchemaFieldDef("TEXT", ""),
				new SchemaFieldDef("FONT", FontTuple.DEFAULT_FONT),
				X,
				Y,
				ROTATION,
				new SchemaFieldDef(WIDTH.name(), -1d),	//Distinct default value
				new SchemaFieldDef(HEIGHT.name(), -1d), //Distinct default value
				new SchemaFieldDef("JUSTIFY", LayoutEngine.Justification.CENTER),
				IMPLANT,
				FILL_COLOR.rename("COLOR"),
				VISIBLE,
				REGISTRATION,
				Z,
				BOUNDS,
				new SchemaFieldDef(LAYOUT, null, RenderDescription.class));
	
	/**Basic expected table schema.**/
	
	
    private final Colorer filler;
    private final Placer placer;
    private final Registerer reg;
    private final Implanter implanter;
    private final Rotater rotater;
    private final LayoutEngine layoutEngine;
    
    private final int boundsIdx;
    private final int layoutIdx;
    
    public TextRenderer(TuplePrototype<SchemaFieldDef> schema) {
  	    filler  = Colorer.Util.instance(schema, schema.indexOf("COLOR"));
	    placer  = Placer.Util.instance(schema, schema.indexOf(X), schema.indexOf(Y));
	    reg = Registerer.Util.instance(schema, schema.indexOf(REGISTRATION));
	    implanter = Implanter.Util.instance(schema, schema.indexOf(IMPLANT));
	    rotater  = Rotater.Util.instance(schema, schema.indexOf(ROTATION));
	    layoutEngine = new LayoutEngine(schema.indexOf("TEXT"), schema.indexOf("FONT"), schema.indexOf("JUSTIFY"));
	
	   	boundsIdx = schema.indexOf(BOUNDS);
	   	layoutIdx = schema.indexOf(LAYOUT);
    }
    
    
    private AffineTransform makeTransform(Glyph glyph, AffineTransform viewTransform, Rectangle2D bounds) {
		AffineTransform trans = new AffineTransform();
		trans = placer.place(trans, glyph);
		trans = implanter.implant(trans, viewTransform, glyph);
		trans = rotater.rotate(trans, glyph);
		trans = reg.register(trans, glyph, bounds);
		return trans;
    }

	@Override
	public void render(TableView layer, Graphics2D g, AffineTransform viewTransform) {
		for (Glyph glyph: new TupleIterator(layer, layer.renderOrder(),  true)) {
			Rectangle2D bounds = (Rectangle2D) glyph.get(boundsIdx);
			Rectangle clipBounds = bounds.getBounds();
			if (!glyph.isVisible() || !g.hitClip(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height)) {continue;}

			RenderDescription layout = (RenderDescription) glyph.get(layoutIdx);
			filler.setColor(g, glyph);

			
			AffineTransform trans = makeTransform(glyph, viewTransform, layout.bounds);
			trans.translate(-layout.bounds.getX(), -layout.bounds.getY());		//baseline correction
			g.transform(trans);
			for (int i=0 ;i< layout.lines.length; i++) {
				Point2D.Float p = layout.positions[i];
				GlyphVector v = layout.lines[i];
				g.drawGlyphVector(v, p.x, p.y);
			}
			g.setTransform(viewTransform);
		}
		Renderer.Util.debugRender(layer, g);
		g.setTransform(viewTransform);
	}
	
	@Override
	public void calcFields(TableShare share, AffineTransform viewTransform) {
		Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);
		Rectangle2D[] bounds = new Rectangle2D[share.size()];
		RenderDescription[] layouts = new RenderDescription[share.size()];
		
		for(StoreTuple glyph: new TupleIterator(share, true)) {
			RenderDescription layout = layoutEngine.layoutLines(glyph);
			Rectangle2D b = layout.bounds;

			AffineTransform trans = makeTransform(glyph, viewTransform, b);
			trans.translate(-b.getX(), -b.getY());		//baseline correction
			b = trans.createTransformedShape(b).getBounds2D();
			bounds[glyph.row()] = b;
			layouts[glyph.row()] = layout;
			
			if (implanter.inBounds(glyph)) {ShapeUtils.add(fullBounds, b);}
		}

		Column newBounds = share.columns()[boundsIdx].replaceAll(bounds);
		share.setColumn(boundsIdx, newBounds);
		share.setColumn(layoutIdx, share.columns()[layoutIdx].replaceAll(layouts));
		share.setBounds(fullBounds);
	}

	/**Results of performing a layout.  Includes the per-line glyph vector and positioning info, as well as global bounds.*/
	private static final class RenderDescription {
		final GlyphVector[] lines;
		final Point2D.Float[] positions;
		final Rectangle2D bounds;
		
		public RenderDescription(GlyphVector[] lines, Point2D.Float[] positions) {
			this.lines = lines;
			this.positions = positions;
			GeneralPath compound = new GeneralPath();
			for (int i=0; i< lines.length; i++) {
				Point2D.Float p = positions[i];
				compound.append(lines[i].getOutline(p.x, p.y), false);
			}
			bounds = compound.getBounds2D();
		}
	}

	
	/**Class for layout of text.**/
	private static final class LayoutEngine {
		/**Describes the valid justifications**/
		public static enum Justification {LEFT, RIGHT, CENTER}
		
		private static final Pattern SPLITTER = Pattern.compile("\n");
		
		private final int textField, fontField, justifyField;
		
		public LayoutEngine(int textField, int fontField, int justifyField) {
			this.textField = textField;
			this.fontField = fontField;
			this.justifyField = justifyField;
		}
				
		public RenderDescription layoutLines(Glyph glyph) {
			String text = (String) glyph.get(textField);
			Font font = (Font) glyph.get(fontField);
			LayoutEngine.Justification justify = (LayoutEngine.Justification) glyph.get(justifyField);
			
			LayoutEngine.LayoutDescription ld = LayoutEngine.computeLayout(text, font);
			
			return staticLines(text, ld, font, justify);
		}
		
	
		private static RenderDescription staticLines(String text, LayoutDescription ld, Font font, Justification justify) {
			Graphics2D g = REFERENCE_GRAPHICS;
			FontRenderContext context = g.getFontRenderContext();
			FontMetrics fm = g.getFontMetrics(font);
			final String[] lines =SPLITTER.split(text);
	
			GlyphVector[] vectors = new GlyphVector[lines.length];
			Point2D.Float[] points = new Point2D.Float[lines.length];
			
			for (int i=0; i< lines.length; i++) {
				final DoubleDimension dim = ld.dims[i];
				String line = lines[i];
				
				double x = horizontalOffset(dim.width, ld.fullWidth, justify);
				double y = dim.height * i + fm.getAscent();
				points[i] = new Point2D.Float((float) x, (float) y);
				
				int flags = Font.LAYOUT_LEFT_TO_RIGHT + Font.LAYOUT_NO_LIMIT_CONTEXT + Font.LAYOUT_NO_START_CONTEXT;
				vectors[i] = font.layoutGlyphVector(context, line.toCharArray(), 0, line.length(), flags); 
			}
			return new RenderDescription(vectors, points);
		}
		
		/**Get the appropriate metrics for the layout of the requested text.
		 */
		//TODO: Do width-based line wrapping, will have to pass in width to do so
		private static LayoutDescription computeLayout(String text, Font font) {
			Graphics2D g = REFERENCE_GRAPHICS;
			FontMetrics fm = g.getFontMetrics(font);
	
	        LayoutDescription ld = new LayoutDescription(SPLITTER.split(text));
	        
	        double height = fm.getHeight();
	        double maxWidth=0;
	        
	        for (int i=0; i<ld.lines.length; i++) {
	        	String line = ld.lines[i];
	        	double width = fm.getStringBounds(line, g).getWidth();
	        	ld.dims[i] = new DoubleDimension(width, height);
	        	
	        	maxWidth = Math.max(maxWidth, width);
	        }        
	
	        ld.fullWidth = maxWidth;
	        ld.fullHeight = height * ld.lines.length;
	        
	        return ld;
		}
	
		private static double horizontalOffset(double lineWidth, double spaceWidth, Justification justification) {
			switch (justification) {
			   case LEFT: return 0;
			   case RIGHT: return spaceWidth- lineWidth;
			   case CENTER: return spaceWidth/2.0 - lineWidth/2.0;
			   default: throw new RuntimeException("Unknown justification value: " + justification);
			}
		}
		
		/**Results of computing a layout.*/
		private static final class LayoutDescription {
			/**Individual lines of text to render.*/
			String[] lines;
			
			/**Metrics about each line.*/
			DoubleDimension[] dims;
	
			/**Length of the longest line.*/
			double fullWidth;
			
			/**Height of all lines added up.*/
			double fullHeight;
	
			public LayoutDescription(String[] lines) {
				this.lines = lines;
				dims = new DoubleDimension[lines.length];
			}
			
			@Override
			public String toString() {
				return String.format("%1$s[fw=%2$f, fh=%3$f]", super.toString(), fullWidth, fullHeight);
			}
		}
	}
}
