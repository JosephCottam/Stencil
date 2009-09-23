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

public final class Text extends Point {
	private static final Pattern SPLITTER = Pattern.compile("\n");
	
	private static final double AUTO_SIZE = -1;
	private static final Attribute TEXT = new Attribute("TEXT", "");
	private static final Attribute WIDTH = new Attribute(StandardAttribute.WIDTH.name(), AUTO_SIZE, Double.class);
	private static final Attribute HEIGHT = new Attribute(StandardAttribute.HEIGHT.name(), AUTO_SIZE, Double.class);
	
	protected static final AttributeList attributes;
	static {
		attributes = new AttributeList(Point.attributes);

		attributes.add(TEXT);
		for (TextProperty p:TextProperty.values()) {attributes.add(new Attribute(p));}
	}

	private String text = (String) TEXT.defaultValue;
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
		else if (contains(TextProperty.class,name)) {return TextFormats.get(name, format);}
		else {return super.get(name);}
	}
	
	public void set(String name, Object value) {
			 if (TEXT.is(name)) 	{this.text = Converter.toString(value);}
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

	public void render(Graphics2D g, AffineTransform base) {
		super.preRender(g);
		
		g.setFont(format.font);
		g.setPaint(format.textColor);

		final DoubleDimension[] dims = computeMetrics(g);
		final String[] lines =SPLITTER.split(text);
		for (int i=0; i< lines.length; i++) {
			final DoubleDimension dim = dims[i];
			String line = lines[i];
			g.drawString(line, (float) horizontalOffset(dim.width), (float) dim.height *i);
		}
		
		super.postRender(g,base);
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
        if (autoHeight) {this.height = fm.getHeight() * lines.length;}
        if (autoWidth) {this.width = maxWidth;}

        return dims;
	}

	
}
