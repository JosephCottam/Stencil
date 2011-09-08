package stencil.adapters.java2D.render;

import static stencil.adapters.Adapter.REFERENCE_GRAPHICS;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
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
				BOUNDS);
	
	/**Basic expected table schema.**/
	
	
    private final Colorer filler;
    private final Placer placer;
    private final Registerer reg;
    private final Implanter implanter;
    private final Rotater rotater;
    private final LayoutEngine layout;
    
    private final int boundsIdx;
    
    public TextRenderer(TuplePrototype<SchemaFieldDef> schema) {
  	    filler  = Colorer.Util.instance(schema, schema.indexOf("COLOR"));
	    placer  = Placer.Util.instance(schema, schema.indexOf(X), schema.indexOf(Y));
	    reg = Registerer.Util.instance(schema, schema.indexOf(REGISTRATION));
	    implanter = Implanter.Util.instance(schema, schema.indexOf(IMPLANT));
	    rotater  = Rotater.Util.instance(schema, schema.indexOf(ROTATION));
	    layout = new LayoutEngine(schema.indexOf("TEXT"), schema.indexOf("FONT"), schema.indexOf("JUSTIFY"));
	
	   	boundsIdx = schema.indexOf(BOUNDS);
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
			if (!glyph.isVisible()) {continue;}
			GeneralPath p = layout.layout(glyph);

			AffineTransform trans = makeTransform(glyph, viewTransform, p.getBounds2D());
			g.transform(trans);

			filler.setColor(g, glyph);
			g.fill(p);
			g.setTransform(viewTransform);
		}
		Renderer.Util.debugRender(layer, g);
		g.setTransform(viewTransform);
	}
	
	@Override
	public void calcFields(TableShare share, AffineTransform viewTransform) {
		Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);
		Rectangle2D[] bounds = new Rectangle2D[share.size()];
		
		for(StoreTuple glyph: new TupleIterator(share, true)) {
			GeneralPath p = layout.layout(glyph);
			AffineTransform trans = makeTransform(glyph, viewTransform, p.getBounds2D());
			p.transform(trans);

			Rectangle2D b = p.getBounds2D(); 
			bounds[glyph.row()] = b;
			
			if (implanter.inBounds(glyph)) {ShapeUtils.add(fullBounds, b);}
		}

		Column newBounds = share.columns()[boundsIdx].replaceAll(bounds);
		share.setColumn(boundsIdx, newBounds);
		
		share.setBounds(fullBounds);
	}

	/**Class for layout of text.**/
	public static final class LayoutEngine {
		/**Describes the valid justifications**/
		public static enum Justification {LEFT, RIGHT, CENTER}
		
		private static final Pattern SPLITTER = Pattern.compile("\n");
		
		private final int textField, fontField, justifyField;
		
		public LayoutEngine(int textField, int fontField, int justifyField) {
			this.textField = textField;
			this.fontField = fontField;
			this.justifyField = justifyField;
		}
		
		public GeneralPath layout(Glyph glyph) {
			String text = (String) glyph.get(textField);
			Font font = (Font) glyph.get(fontField);
			LayoutEngine.Justification justify = (LayoutEngine.Justification) glyph.get(justifyField);
			
			LayoutEngine.LayoutDescription ld = LayoutEngine.computeLayout(text, font);
			GeneralPath renderedText = LayoutEngine.layoutText(text, ld, font, justify);
			Rectangle2D bounds = renderedText.getBounds2D();
			renderedText.transform(AffineTransform.getTranslateInstance(-bounds.getX(), -bounds.getY()));  //Undo the shift caused by baselining
			return renderedText;
		}
	
		private static GeneralPath layoutText(String text, LayoutDescription ld, Font font, Justification justify) {
			Graphics2D g = REFERENCE_GRAPHICS;
			FontRenderContext context = g.getFontRenderContext();
			FontMetrics fm = g.getFontMetrics(font);
			final String[] lines =SPLITTER.split(text);
	
			GeneralPath compound = new GeneralPath();
			
			for (int i=0; i< lines.length; i++) {
				final DoubleDimension dim = ld.dims[i];
				String line = lines[i];
				
				double x = horizontalOffset(dim.width, ld.fullWidth, justify);
				double y = dim.height * i + fm.getAscent();
				
				int flags = Font.LAYOUT_LEFT_TO_RIGHT + Font.LAYOUT_NO_LIMIT_CONTEXT + Font.LAYOUT_NO_START_CONTEXT;
				GlyphVector v = font.layoutGlyphVector(context, line.toCharArray(), 0, line.length(), flags);
				compound.append(v.getOutline((float) x, (float) y), false);
			}
	
			return compound;
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
		public static final class LayoutDescription {
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
			
			public String toString() {
				return String.format("%1$s[fw=%2$f, fh=%3$f]", super.toString(), fullWidth, fullHeight);
			}
		}
	}
}
