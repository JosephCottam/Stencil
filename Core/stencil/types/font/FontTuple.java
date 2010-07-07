package stencil.types.font;

import java.awt.Font;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.util.collections.ArrayUtil;

public final class FontTuple implements Tuple {
	
	static final String SELF = "self";
	static final String FAMILY = "family";
	static final String SIZE = "size";
	static final String BOLD = "bold";
	static final String ITALIC = "italic";	
	
	static final String DEFAULT_FAMILY = "Helvetica";
	static final Double DEFAULT_SIZE  = 12d;
	static final Boolean DEFAULT_BOLD = false;
	static final Boolean DEFAULT_ITALIC = false;
	public static final Font DEFAULT_FONT = new Font(DEFAULT_FAMILY, Font.PLAIN, DEFAULT_SIZE.intValue());
	
	private static final String[] FIELDS = new String[]{SELF, FAMILY, SIZE, BOLD, ITALIC};
	private static Class[] TYPES = new Class[] {Font.class, String.class, Double.class, Boolean.class, Boolean.class};
	private static TuplePrototype PROTOTYPE = new SimplePrototype(FIELDS, TYPES);
	public static final int SELF_IDX = ArrayUtil.indexOf(SELF, FIELDS);
	public static final int FONT_IDX = ArrayUtil.indexOf(FAMILY, FIELDS);
	public static final int SIZE_IDX = ArrayUtil.indexOf(SIZE, FIELDS);
	public static final int BOLD_IDX = ArrayUtil.indexOf(BOLD, FIELDS);
	public static final int ITALIC_IDX = ArrayUtil.indexOf(ITALIC, FIELDS);
	
	private final Font font;
	
	public FontTuple(String name) {this(name, DEFAULT_SIZE, false, false);}
	public FontTuple(String name, double size) {this(name, size, false, false);}
	public FontTuple(String name, double size, boolean bold, boolean italic) {
		int style = Font.PLAIN;
		if (bold || italic) {
			style = (bold ? Font.BOLD : 0) + (italic ? Font.ITALIC : 0); 
		}		
		
		Font f = new Font(name, style, DEFAULT_SIZE.intValue());
		if (size != DEFAULT_SIZE) {f = f.deriveFont((float) size);}
		this.font = f;
	}

	public FontTuple(Font f) {this.font = f;}
	
	public Object get(final String name) throws InvalidNameException {
		return Tuples.namedDereference(name, this);
	}

	public Object get(final int idx) throws TupleBoundsException {
		if (idx == SELF_IDX) {return font;}
		if (idx == FONT_IDX) {return font.getFamily();}
		if (idx == SIZE_IDX) {return font.getSize2D();}
		if (idx == BOLD_IDX) {return font.isBold();}
		if (idx == ITALIC_IDX) {return font.isItalic();}
		throw new TupleBoundsException(idx, this);
	}

	public Font getFont() {return font;}
	public TuplePrototype getPrototype() {return PROTOTYPE;}

	public boolean isDefault(String name, Object value) {
		if (name.equals(FAMILY)) {return DEFAULT_FAMILY.equals(value);}
		if (name.equals(SIZE)) {return DEFAULT_SIZE.equals(value);}
		if (name.equals(BOLD)) {return DEFAULT_BOLD.equals(value);}
		if (name.equals(ITALIC)) {return DEFAULT_ITALIC.equals(value);}
		return false;
	}

	public int size() {return FIELDS.length;}
}
