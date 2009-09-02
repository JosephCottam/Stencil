package stencil.adapters.java2D.data.glyphs;


import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.util.regex.Pattern;

import static stencil.adapters.general.TextFormats.TextProperty;
import static stencil.adapters.general.TextFormats.Format;

import static stencil.adapters.Adapter.DEFAULT_GRAPHICS;
import static stencil.util.enums.EnumUtils.contains;
import stencil.adapters.general.TextFormats;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.types.Converter;

public final class Text extends Point {
	private static Pattern splitter = Pattern.compile("\n");
	private static final Attribute TEXT = new Attribute("Text", "");
	protected static final AttributeList attributes;
	static {
		attributes = new AttributeList(Point.attributes);

		attributes.add(TEXT);
		for (TextProperty p:TextProperty.values()) {attributes.add(new Attribute(p));}
	}

	private String text = (String) TEXT.getDefault();
	private Format format;
	private double width;
	private double height;
	
	public Text(String id) {super(id);}
	
	public Object get(String name) {
		if (name.equals("TEXT")) {return text;}
		else if (contains(TextProperty.class,name)) {return TextFormats.get(name, format);}
		else {return super.get(name);}
	}
	
	public void set(String name, Object value) {
		if (name.equals("TEXT")) {this.text = Converter.toString(value);}
		else if (contains(TextProperty.class, name)) {format = TextFormats.set(name, value, format);}
		else {super.set(name,value);}
		computeMetrics();
	}
	
	@Override
	protected AttributeList getAttributes() {return attributes;}

	public Double getHeight() {return height;}	
	public Double getWidth() {return width;}
	public String getImplantation() {return "TEXT";}


	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}	

	//TODO: Change to 'compute layout' and determine line breaks...if needed
	private final void computeMetrics() {
        FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(format.font);
        String[] lines = splitter.split(text);
        double maxWidth=0;
        for (String line: lines) {
        	double width = fm.stringWidth(line);
        	maxWidth = Math.max(maxWidth, width);
        }
        this.height = fm.getHeight() * lines.length;
        this.width = maxWidth;
	}

	
}
