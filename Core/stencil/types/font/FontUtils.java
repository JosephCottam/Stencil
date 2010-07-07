package stencil.types.font;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;

import stencil.module.operator.util.BasicProject;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.types.Converter;

public final class FontUtils extends BasicModule {
	public static final Font Bold(Font f) {return f.deriveFont(Font.BOLD);}
	public static final Font Italic(Font f) {return f.deriveFont(Font.ITALIC);}
	public static final Font Family(Font f, String family) {return new Font(family, f.getStyle(), f.getSize()).deriveFont(f.getSize2D());}
	public static final Font Plain(Font f) {return f.deriveFont(Font.PLAIN);}
	public static final Font Size(Font f, double size) {return f.deriveFont((float) size);}
	 
	
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
	public static class Fonts extends BasicProject {
		public Fonts(OperatorData opData) {super(opData);}
		public FontTuple query(String arg) {
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
	
	public FontUtils(ModuleData md) {super(md);}
}