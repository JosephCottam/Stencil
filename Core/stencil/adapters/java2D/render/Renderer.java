package stencil.adapters.java2D.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import stencil.adapters.general.Shapes;
import stencil.adapters.general.Registrations.Registration;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.util.ReferenceFieldDef;
import stencil.adapters.java2D.render.mixins.Implanter;
import stencil.display.Glyph;
import stencil.display.LayerView;
import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.color.ColorCache;
import stencil.types.color.ColorUtils;
import stencil.types.stroke.StrokeTuple;


/**Renderer translates the visual store into 
 * glyphs and places those glyphs on the canvas.
 * 
 * This class also contains utilities and common declarations for rendering.
 *
 */
public interface Renderer<T extends LayerView<? extends Glyph>> {
	static final SchemaFieldDef COORD = new SchemaFieldDef("COORD", 0d);

	//Collection of common fields
	public static final SchemaFieldDef ID = new SchemaFieldDef("ID", null, Comparable.class);
	public static final SchemaFieldDef BOUNDS = new SchemaFieldDef("BOUNDS", null, Rectangle2D.class, false);
	public static final SchemaFieldDef X = COORD.rename("X");
	public static final SchemaFieldDef Y = COORD.rename("Y");
	public static final SchemaFieldDef Z = COORD.rename("Z");
	public static final SchemaFieldDef IMPLANT = new SchemaFieldDef("IMPLANT", Implanter.ImplantBy.AREA);
	public static final SchemaFieldDef SIZE = new SchemaFieldDef("SIZE", 5d);
	public static final SchemaFieldDef FILL_COLOR = new SchemaFieldDef("FILL_COLOR", Color.BLACK, Paint.class);
	public static final SchemaFieldDef PEN_COLOR = new SchemaFieldDef("PEN_COLOR", new java.awt.Color(0,0,0, ColorCache.CLEAR_INT), Paint.class);
	public static final SchemaFieldDef OPAQUE_PEN_COLOR = new SchemaFieldDef("PEN_COLOR", Color.BLACK, Paint.class);
	public static final SchemaFieldDef PEN = new SchemaFieldDef("PEN", StrokeTuple.DEFAULT_STROKE);
	public static final SchemaFieldDef VISIBLE = new SchemaFieldDef("VISIBLE", true);
	public static final SchemaFieldDef REGISTRATION = new SchemaFieldDef("REGISTRATION", Registration.TOP_LEFT);
	public static final SchemaFieldDef ROTATION = new SchemaFieldDef("ROTATION", 0d);
	public static final SchemaFieldDef WIDTH = new SchemaFieldDef("WIDTH", 10d);
	public static final SchemaFieldDef HEIGHT = new SchemaFieldDef("HEIGHT", 10d);
	public static final ReferenceFieldDef CHILDREN = new ReferenceFieldDef("CHILD", new TuplePrototype());
	
	
	/**Draw each element of the layer on the given graphics object
	 * Assume that calcFields has been run on the contents of the layer passed.
	 * **/   
	public void render(T layer, Graphics2D g, AffineTransform viewTransform);
	   
	/**Determine the render-dependent values for the glyphs in a table share.
	 * This should be performed after all dynamic bindings are complete.
	 * Bounds is the most common field, but interesting anchor points or pre-rendered items are also possible.
	 ***/
	public void calcFields(TableShare share);
	
	public static final class Util {
		public static Color DEBUG_COLOR = null;
		private static final Stroke DEBUG_STROKE = new BasicStroke(.25f);

		public static final void debugRender(LayerView<? extends Glyph> layer, Graphics2D g) {
			if (DEBUG_COLOR == null) {return;}
			
			g.setStroke(DEBUG_STROKE);
			
			for (Glyph glyph: layer) { 
				//Bounds for clip calculation
				g.setPaint(DEBUG_COLOR);
				Rectangle2D r = glyph.getBoundsReference();
				g.draw(r);

				//Registration point (if X and Y are stored)
				try {
					g.setPaint(DEBUG_COLOR.darker());
					double scale=2;
					double x = (Double) glyph.get(X.name());
					double y = -(Double) glyph.get(Y.name());
					Shape cross = Shapes.getShape(StandardShape.CROSS, x-scale/2, y-scale/2, scale, scale);
					g.fill(cross);
				} catch  (Exception e) {/*Exception ignored, its just debug code.*/}
			}
			
			
			
			g.setPaint(ColorUtils.lighten(DEBUG_COLOR, .5));
			g.draw(layer.getBoundsReference());
		}
	}
}