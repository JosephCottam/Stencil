package stencil.adapters.java2D.data.glyphs;


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
		computeMetrics();
	}
	
	@Override
	protected AttributeList getAttributes() {return attributes;}

	public double getHeight() {
		return height;
	}	
	public double getWidth() {
		return width;
	}
	public String getImplantation() {return "TEXT";}


	@Override
	public void render(Graphics2D g) {
		AffineTransform rs = super.preRender(g);
		
		g.setFont(format.font);
		g.setPaint(format.textColor);
		g.drawString(text, 0, (float)getHeight());
		
		super.postRender(g,rs);
	}	

	//TODO: Change to 'compute layout' and determine line breaks...if needed
	private final void computeMetrics() {
		if (!autoHeight && !autoWidth) {return;}
		
        FontMetrics fm = REFERENCE_GRAPHICS.getFontMetrics(format.font);
        String[] lines = SPLITTER.split(text);
        double maxWidth=0;
        for (String line: lines) {
        	double width = fm.stringWidth(line);
        	maxWidth = Math.max(maxWidth, width);
        }
        if (autoHeight) {this.height = fm.getHeight() * lines.length;}
        if (autoWidth) {this.width = maxWidth;}
	}

	
}
