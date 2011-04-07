package stencil.adapters.java2D.data;

import stencil.adapters.java2D.data.glyphs.Arc;
import stencil.adapters.java2D.data.glyphs.Image;
import stencil.adapters.java2D.data.glyphs.Line;
import stencil.adapters.java2D.data.glyphs.Pie;
import stencil.adapters.java2D.data.glyphs.Poly;
import stencil.adapters.java2D.data.glyphs.Shape;
import stencil.adapters.java2D.data.glyphs.Slice;
import stencil.adapters.java2D.data.glyphs.Text;
import stencil.tuple.Tuple;

public interface Glyph2D extends stencil.display.Glyph, Renderable {
	/**Calculate an updated version of this glyph based on the passed tuple.
	 * The returned glyph will have field values equal to the passed tuple
	 * for any field that appears in the passed tuple and current value
	 * for any field that does not.  All fields in the passed
	 * tuple must appear in the current tuple or an illegalArugmentException is thrown.
	 */
	public Glyph2D update(Tuple t) throws IllegalArgumentException;
	
	/** Copy the current glyph, but replace its name.*/
	public Glyph2D updateID(String id);
	
	/**Accessor method used in updates; provided to reduce cost of name-based lookup.*/
	public String getID();
	
	/**Is the glyph current toggeled for rendering?*/
	public boolean isVisible();
	
	public double getZ();
	
	
	public static final class Util {
		private Util() {}
		/**Get an instance of the requested glyph type.
		 * 
		 * TODO: Use reflection so these are not explicitly encoded like this.
		 * **/
		public static Glyph2D instance(String glyphType, String id) {
			Glyph2D prototype = null;
			try {
				if (glyphType.equals(Shape.IMPLANTATION)) {
					prototype = new Shape(id);
				} else if (glyphType.equals(Line.IMPLANTATION)) {
					prototype = new Line(id);
				} else if (glyphType.equals(Text.IMPLANTATION)) {
					prototype = new Text(id);
				} else if (glyphType.equals(Pie.IMPLANTATION)) {
					prototype = new Pie(id);
				} else if (glyphType.equals(Image.IMPLANTATION)) {
					prototype = new Image(id);
				} else if (glyphType.equals(Poly.PolyLine.IMPLANTATION)) {
					prototype = new Poly.PolyLine(id);
				} else if (glyphType.equals(Poly.Polygon.IMPLANTATION)) {
					prototype = new Poly.Polygon(id);
				} else if (glyphType.equals(Arc.IMPLANTATION)) {
					prototype = new Arc(id);
				} else if (glyphType.equals(Slice.IMPLANTATION)) {
					prototype = new Slice(id);
				} 
			} catch (Throwable e) {throw new RuntimeException("Error instantiating table for implantation: " + glyphType, e);}
			if (prototype == null) {throw new IllegalArgumentException("Glyph type not know: " + glyphType);}
			return prototype;
		}
	}
}
