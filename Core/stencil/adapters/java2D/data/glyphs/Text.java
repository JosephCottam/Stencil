package stencil.adapters.java2D.data.glyphs;


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.util.regex.Pattern;

import static stencil.adapters.general.TextFormats.TextProperty;
import static stencil.adapters.general.TextFormats.Format;

import static stencil.adapters.Adapter.REFERENCE_GRAPHICS;
import static stencil.util.enums.EnumUtils.contains;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.TextFormats;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.types.Converter;
import stencil.util.DoubleDimension;

public class Text extends Point {
	
	public static final class TextShape extends Text {
		protected static final AttributeList attributes;
		static {
			attributes = new AttributeList(Text.attributes);
			attributes.remove("SCALE_BY");
		}
		
		public TextShape(String id) {super(id);}

		protected final void fixScale(Graphics2D g) {/*deliberately does nothing*/}
	}
	
	private static final Pattern SPLITTER = Pattern.compile("\n");
	
	private static final double AUTO_SIZE = -1;
	private static final Attribute TEXT = new Attribute("TEXT", "");
	private static final Attribute WIDTH = new Attribute(StandardAttribute.WIDTH.name(), AUTO_SIZE, Double.class);
	private static final Attribute HEIGHT = new Attribute(StandardAttribute.HEIGHT.name(), AUTO_SIZE, Double.class);
	private static final Attribute SCALE_BY = new Attribute("SCALE_BY", "SMALLEST");
	
	protected static final AttributeList attributes;
	static {
		attributes = new AttributeList(Point.attributes);
		
		attributes.add(TEXT);
		attributes.add(SCALE_BY);
		
		for (TextProperty p:TextProperty.values()) {attributes.add(new Attribute(p));}
	}

	private String text = (String) TEXT.defaultValue;
	private String scaleBy = (String) SCALE_BY.defaultValue;
	private Format format = new Format();
	
	private boolean autoWidth = true;
	private boolean autoHeight = true;
	
	private double width = (Double) WIDTH.defaultValue;
	private double height= (Double) HEIGHT.defaultValue;
	
	public Text(String id) {super(id);}
	
	protected AttributeList getAttributes() {return attributes;}
	public double getHeight() {return height;}	
	public double getWidth() {return width;}
	public String getImplantation() {return "TEXT";}

	
	public Object get(String name) {
		if (TEXT.is(name)) {return text;}
		if (SCALE_BY.is(name)) {return scaleBy;}
		if (contains(TextProperty.class,name)) {return TextFormats.get(name, format);}
		return super.get(name);
	}
	
	public void set(String name, Object value) {
			 if (TEXT.is(name)) 	{this.text = Converter.toString(value);}
		else if (SCALE_BY.is(name)) {this.scaleBy = Converter.toString(value).toUpperCase();}
		else if (WIDTH.is(name)) 	{this.width = Converter.toDouble(value); autoWidth = (this.width ==AUTO_SIZE);}
		else if (HEIGHT.is(name)) 	{this.height = Converter.toDouble(value); autoHeight = (this.height ==AUTO_SIZE);}
		else if (contains(TextProperty.class, name)) {
			Class c = attributes.get(name).type;
			value = Converter.convert(value, c);
			format = TextFormats.set(name, value, format);
		}
		else {super.set(name,value);}
		computeMetrics(null);
	}

	protected void fixScale(Graphics2D g) {
		AffineTransform trans = g.getTransform();
		double scale = -1;
		if (trans.getScaleX() != trans.getScaleY()) {
			if (scaleBy.equals("X")) {scale = trans.getScaleX();}
			else if (scaleBy.equals("Y")) {scale = trans.getScaleY();}
			else if (scaleBy.equals("LARGEST")) {scale = Math.min(trans.getScaleX(), trans.getScaleY());}
			else {scale = Math.min(trans.getScaleX(), trans.getScaleY());}

			g.scale(scale/trans.getScaleX(), scale/trans.getScaleY());
		}
	}
	
	public void render(Graphics2D g, AffineTransform base) {
		super.preRender(g);
		
		fixScale(g);
		
		g.setFont(format.font);
		g.setPaint(format.textColor);

		final DoubleDimension[] dims = computeMetrics(g);
		final String[] lines =SPLITTER.split(text);
		for (int i=0; i< lines.length; i++) {
			final DoubleDimension dim = dims[i];
			String line = lines[i];
			g.drawString(line, (float) horizontalOffset(dim.width), (float) dim.height *(i+1));
		}
		
		super.postRender(g, base);
	}	
	
	private double horizontalOffset(double lineWidth) {
		if (format.justification == Component.LEFT_ALIGNMENT) {return 0;}

		if (format.justification == Component.RIGHT_ALIGNMENT) {
			return width - lineWidth;
		}
		if (format.justification == Component.CENTER_ALIGNMENT) {return width/2.0 - lineWidth/2.0;}
		throw new RuntimeException("Unknown justification value: " + format.justification);
	}

	//TODO: Change to 'compute layout' and determine line breaks...if needed
	private DoubleDimension[] computeMetrics(Graphics2D g) {
        
		FontMetrics fm = REFERENCE_GRAPHICS.getFontMetrics(format.font);
        String[] lines = SPLITTER.split(text);
        DoubleDimension[] dims = new DoubleDimension[lines.length];
        double height = fm.getHeight();
        double maxWidth=0;
        for (int i=0; i<lines.length; i++) {
        	String line = lines[i];
        	double width;
        	if (g==null) {width= fm.stringWidth(line);}
        	else {width = fm.getStringBounds(line, g).getWidth();}
        	
        	dims[i] = new DoubleDimension(width, height);
        	
        	maxWidth = Math.max(maxWidth, width);
        }
        if (autoHeight) {this.height = height * lines.length;}
        if (autoWidth) {this.width = maxWidth;}

        return dims;
	}

	
}
