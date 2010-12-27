package stencil.types.font;

import java.util.ArrayList;
import java.util.Arrays;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.types.Converter;

@Description("Create and modify fonts (a font is family+size+style).")
@Module
public final class FontUtils extends BasicModule {
	@Operator @Facet(memUse="FUNCTION", prototype="(Font font)")
	public static final java.awt.Font Bold(java.awt.Font f) {return f.deriveFont(java.awt.Font.BOLD);}

	@Operator @Facet(memUse="FUNCTION", prototype="(Font font)")
	public static final java.awt.Font Italic(java.awt.Font f) {return f.deriveFont(java.awt.Font.ITALIC);}

	@Operator @Facet(memUse="FUNCTION", prototype="(Font font)")
	public static final java.awt.Font Family(java.awt.Font f, String family) {return new java.awt.Font(family, f.getStyle(), f.getSize()).deriveFont(f.getSize2D());}

	@Operator @Facet(memUse="FUNCTION", prototype="(Font font)")
	public static final java.awt.Font Plain(java.awt.Font f) {return f.deriveFont(java.awt.Font.PLAIN);}

	@Operator @Facet(memUse="FUNCTION", prototype="(Font font)")	
	public static final java.awt.Font Size(java.awt.Font f, double size) {return f.deriveFont((float) size);}
	 
	
	/**Fonts are specified with a comma-separated list (order does not matter).
	 *   
	 * The words "bold" or "italic" will toggle on the indicated value.
	 * A numeric value will be taken as the size.
	 * A string value will be taken as the family.
	 * Extra values may be given, but they will likely be ignored...
	 * 
	 * @author jcottam
	 *
	 */
	@Operator
	public static class Font extends AbstractOperator {
		public Font(OperatorData opData) {super(opData);}
		
	    @Facet(memUse="FUNCTION", prototype="(Font font)", alias={"map","query","argumentParser"})
		public FontTuple query(String arg) {return toTuple(arg);}
		public static FontTuple toTuple(String arg) {
			final ArrayList<String> parts = new ArrayList(Arrays.asList(arg.trim().toLowerCase().split(",\\s+")));
			String family = FontTuple.DEFAULT_FAMILY;
			double size = -1;
			final boolean bold = parts.remove("bold");
			final boolean italic = parts.remove("italic");

			for (int i=0;i<parts.size(); i++) {
				try {
					size = Converter.toDouble(parts.get(i));
					parts.remove(i);
					break;
				}
				catch (Exception e) {}				
			}
			
			if (parts.size()==1) {family = parts.get(0);}
			else if (parts.size() >0) {throw new RuntimeException("Ill formed font specifier: " + arg);}
			return new FontTuple(family, size, bold, italic);
		}
	}
}