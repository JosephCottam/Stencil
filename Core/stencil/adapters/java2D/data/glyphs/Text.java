package stencil.adapters.java2D.data.glyphs;


import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.util.regex.Pattern;

import static stencil.adapters.general.TextFormats.TextProperty;
import static stencil.adapters.general.TextFormats.Format;

import static stencil.adapters.Adapter.REFERENCE_GRAPHICS;
import static stencil.util.enums.EnumUtils.contains;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.TextFormats;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.streams.Tuple;
import stencil.util.DoubleDimension;

public class Text extends Basic {
	protected static final AttributeList ATTRIBUTES = new AttributeList(Basic.ATTRIBUTES);
	protected static final AttributeList UNSETTABLES = new AttributeList();
	private static final String IMPLANTATION = "TEXT";
	
	private static final Pattern SPLITTER = Pattern.compile("\n");
	
	private static final double AUTO_SIZE = -1;
	private static final Attribute<String> TEXT = new Attribute("TEXT", "");
	private static final Attribute<Double> X = new Attribute("X", 0d);
	private static final Attribute<Double> Y = new Attribute("Y", 0d);
	private static final Attribute<Double> ROTATION = new Attribute("ROTATION", 0d);
	private static final Attribute<Double> WIDTH = new Attribute(StandardAttribute.WIDTH.name(), AUTO_SIZE, Double.class);
	private static final Attribute<Double> HEIGHT = new Attribute(StandardAttribute.HEIGHT.name(), AUTO_SIZE, Double.class);
	protected static final Attribute<String> SCALE_BY = new Attribute("SCALE_BY", "ALL");
		
	static {
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(TEXT);
		ATTRIBUTES.add(SCALE_BY);
		ATTRIBUTES.add(ROTATION);
		ATTRIBUTES.add(HEIGHT);
		ATTRIBUTES.add(WIDTH);
		
		for (TextProperty p:TextProperty.values()) {ATTRIBUTES.add(new Attribute(p));}
	}

	private final String text;
	private final String scaleBy;
	private final Format format;
	
	private final boolean autoWidth;
	private final boolean autoHeight;
	
	private final Rectangle2D bounds;
	private final double rotation;
	
	private final Rectangle2D drawBounds;
	private final GeneralPath renderedText;
	
	public Text(DisplayLayer layer, String id) {
		super(layer, id);
		
		text = TEXT.defaultValue;
		scaleBy = SCALE_BY.defaultValue;
		format = new Format();
		rotation = ROTATION.defaultValue;
		autoWidth = true;
		autoHeight = true;

		bounds = new Rectangle2D.Double(X.defaultValue, Y.defaultValue, WIDTH.defaultValue, HEIGHT.defaultValue);
		drawBounds  = (Rectangle2D) bounds.clone();
		renderedText = new GeneralPath();
	}
	
	protected Text(String id, Text source) {
		super(id, source);
		
		this.text = source.text;
		this.scaleBy = source.scaleBy;
		this.format = source.format;
		this.autoHeight = source.autoHeight;
		this.autoWidth = source.autoWidth;
		this.bounds = source.bounds;
		this.rotation = source.rotation;
		this.drawBounds = source.drawBounds;
		this.renderedText = source.renderedText;
	}
	
	protected Text(Text source, Tuple option) {
		this(source, option, UNSETTABLES);
	}

	protected Text(Text source, Tuple option, AttributeList unsettables) {
		super(source, option, unsettables);

		scaleBy = switchCopy(source.scaleBy, safeGet(option, SCALE_BY));
		text = switchCopy(source.text, safeGet(option, TEXT));
		rotation = switchCopy(source.rotation, safeGet(option, ROTATION));
		format = TextFormats.make(source, option);
		
		if (option.hasField(HEIGHT.name)) {
			double height = (Double) option.get(HEIGHT.name, Double.class);
			autoHeight = (height <= AUTO_SIZE);
		} else {autoHeight = source.autoHeight;}
		
		if (option.hasField(WIDTH.name)) {
			double width = (Double) option.get(WIDTH.name, Double.class);
			autoWidth = (width <= AUTO_SIZE);
		}else {autoWidth = source.autoWidth;}

		if (text.equals(source.text)) {
			this.renderedText = source.renderedText;
			this.drawBounds = source.drawBounds;
			this.bounds = source.bounds;
		} else {
			renderedText = layoutText(text, format);
			Rectangle2D layoutBounds = renderedText.getBounds2D();
			
			double width, height;
			if (autoWidth) {width = layoutBounds.getWidth();} 
			else {width = switchCopy(source.bounds.getWidth(), safeGet(option, WIDTH));}
			 
			if (autoHeight) {height = layoutBounds.getHeight();}
			else {height = switchCopy(source.bounds.getHeight(), safeGet(option, HEIGHT));}

			Point2D topLeft = mergeRegistrations(source, option, width, height, X, Y);
			bounds = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), width, height);
			
			GeneralPath layout = (GeneralPath) renderedText.clone();
			layout.transform(AffineTransform.getRotateInstance(Math.toRadians(rotation)));
			layout.transform(AffineTransform.getTranslateInstance(topLeft.getX(), topLeft.getY()));
			drawBounds = layout.getBounds2D();
		}
	}

	
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	public String getImplantation() {return IMPLANTATION;}

	
	public Object get(String name) {
		if (TEXT.is(name)) {return text;}
		if (SCALE_BY.is(name)) {return scaleBy;}
		if (X.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getX();}
		if (Y.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getY();}
		if (ROTATION.is(name)) {return rotation;}
		if (HEIGHT.is(name)) {return bounds.getHeight();}
		if (WIDTH.is(name)) {return bounds.getWidth();}
		if (contains(TextProperty.class,name)) {return TextFormats.get(name, format);}
		return super.get(name);
	}

	protected void fixScale(Graphics2D g) {
		AffineTransform trans = g.getTransform();
		
		double scale = -1;
		
		if (scaleBy.equals("X")) {scale = trans.getScaleX();}
		else if (scaleBy.equals("Y")) {scale = trans.getScaleY();}
		else if (scaleBy.equals("LARGEST")) {scale = Math.min(trans.getScaleX(), trans.getScaleY());}
		else if (scaleBy.equals("NONE")) {scale = 1;}
		else if (scaleBy.equals("SMALLEST")){scale = Math.min(trans.getScaleX(), trans.getScaleY());}
		else if (scaleBy.equals("ALL")){return;} //Scale by all...do nothing to the current transform
		else {throw new IllegalArgumentException("Attempted to use SCALE_BY of an unknown value " + scaleBy);}

		double scaleXBy = trans.getScaleX() == 0 ? 1 : scale/trans.getScaleX();
		double scaleYBy = trans.getScaleY() == 0 ? 1 : scale/trans.getScaleY();
		
		g.scale(scaleXBy, scaleYBy);
	}
	
	public void render(Graphics2D g, AffineTransform base) {
		g.setFont(format.font);
		g.setPaint(format.textColor);

		g.translate(bounds.getX(), bounds.getY());
		g.rotate(Math.toRadians(this.rotation));
		g.fill(renderedText);
		super.postRender(g, base);
	}	
	
	private static GeneralPath layoutText(String text, Format format) {
		Graphics2D g = REFERENCE_GRAPHICS;
		FontRenderContext context = g.getFontRenderContext();
		DoubleDimension[] dims = computeLayout(g, text, format.font);
		final String[] lines =SPLITTER.split(text);

		GeneralPath compound = new GeneralPath();
		
		for (int i=0; i< lines.length; i++) {
			final DoubleDimension dim = dims[i];
			String line = lines[i];
			
			double x = horizontalOffset(dim.width, dims[dims.length-1].width, format.justification);
			double y = dim.height * i + (dim.height*.8); //TODO: get standard ascent instead...this is just an approximation of baseline to top of ascent
			
			java.awt.Shape s = format.font.createGlyphVector(context, line).getOutline((float) x, (float) y);
			compound.append(s, false);
		}

		return compound;
	}
	
	/**Get the appropriate metrics for the layout of the requested text.
	 * The last entry of the returned dimensions array DOES NOT CORRESPONDE TO A SINGLE LINE;
	 * it is the overall dimensions.  Iterate on the lines, not on the dimensions when working with dimensions. 
	 * 
	 */
	//TODO: Do width-based line wrapping
	private static DoubleDimension[] computeLayout(Graphics2D g, String text, Font font) {
        Graphics2D g2 = g ==null? REFERENCE_GRAPHICS: g;
		FontMetrics fm = g2.getFontMetrics(font);
        String[] lines = SPLITTER.split(text);
        DoubleDimension[] dims = new DoubleDimension[lines.length +1];
        
        double height = fm.getHeight();
        double maxWidth=0;
        
        for (int i=0; i<lines.length; i++) {
        	String line = lines[i];
        	double width = fm.getStringBounds(line, g2).getWidth();
        	dims[i] = new DoubleDimension(width, height);
        	
        	maxWidth = Math.max(maxWidth, width);
        }        
        
        double fullHeight = height * lines.length;
        dims[lines.length] = new DoubleDimension(maxWidth, fullHeight);
        return dims;
	}

	private static double horizontalOffset(double lineWidth, double spaceWidth, float justification) {
		if (justification == Component.LEFT_ALIGNMENT) {return 0;}

		if (justification == Component.RIGHT_ALIGNMENT) {return spaceWidth- lineWidth;}
		
		if (justification == Component.CENTER_ALIGNMENT) {return spaceWidth/2.0 - lineWidth/2.0;}
		
		throw new RuntimeException("Unknown justification value: " + justification);
	}

	
	public Rectangle2D getBoundsReference() {return drawBounds;}

	
	public Text update(Tuple t) throws IllegalArgumentException {return new Text(this, t);}
	public Text updateID(String id) {return new Text(id, this);}
}
