package stencil.adapters.java2D.data.glyphs;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.lang.ref.SoftReference;
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
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;
import stencil.util.DoubleDimension;

public final class Text extends Basic {
	
	/**Results of computing a layout.*/
	private static final class LayoutDescription {
		/**Individual lines of text to render.*/
		String[] lines;
		
		/**Metrics about each line.*/
		DoubleDimension[] dims;

		/**Length of the longest line.*/
		double fullWidth;
		
		/**Height of all lines added up.*/
		double fullHeight;

		public LayoutDescription(String[] lines) {
			this.lines = lines;
			dims = new DoubleDimension[lines.length];
		}		
	}
	
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
	
	private final Rectangle2D horizontalBounds;
	private final double rotation;
	
	private final SoftReference<GeneralPath> renderedTextRef;
	
	public Text(DisplayLayer layer, String id) {
		super(layer, id);
		
		text = TEXT.defaultValue;
		scaleBy = SCALE_BY.defaultValue;
		format = new Format();
		rotation = ROTATION.defaultValue;
		autoWidth = true;
		autoHeight = true;

		horizontalBounds = new Rectangle2D.Double(X.defaultValue, Y.defaultValue, WIDTH.defaultValue, HEIGHT.defaultValue);
		super.updateBoundsRef((Rectangle2D) horizontalBounds.clone());
		renderedTextRef = new SoftReference(new GeneralPath());
	}
	
	protected Text(String id, Text source) {
		super(id, source);
		
		this.text = source.text;
		this.scaleBy = source.scaleBy;
		this.format = source.format;
		this.autoHeight = source.autoHeight;
		this.autoWidth = source.autoWidth;
		this.horizontalBounds = source.horizontalBounds;
		this.rotation = source.rotation;
		this.renderedTextRef = source.renderedTextRef;
	}

	protected Text(Text source, Tuple option, AttributeList unsettables) {
		super(source, option, unsettables);

		scaleBy = switchCopy(source.scaleBy, safeGet(option, SCALE_BY));
		text = switchCopy(source.text, safeGet(option, TEXT));
		rotation = switchCopy(source.rotation, safeGet(option, ROTATION));
		format = TextFormats.make(source, option);
		
		if (option.getPrototype().contains(HEIGHT.name)) {
			double height = Converter.toDouble(option.get(HEIGHT.name));
			autoHeight = (height <= AUTO_SIZE);
		} else {autoHeight = source.autoHeight;}
		
		if (option.getPrototype().contains(WIDTH.name)) {
			double width = Converter.toDouble(option.get(WIDTH.name));
			autoWidth = (width <= AUTO_SIZE);
		}else {autoWidth = source.autoWidth;}

		//If there was no change to layout, just copy it; otherwise, recompute it
		if (text.equals(source.text)
			&& format.equals(source.format)
			&& !option.getPrototype().contains(X.name)
			&& !option.getPrototype().contains(Y.name) 
			&& !option.getPrototype().contains(WIDTH.name)
			&& !option.getPrototype().contains(HEIGHT.name)) {
			
			this.renderedTextRef = source.renderedTextRef;
			super.updateBoundsRef(source.bounds);
			this.horizontalBounds = source.horizontalBounds;
		} else {
			LayoutDescription ld = computeLayout(text, format);
			
			if (!autoWidth) {
				ld.fullWidth = switchCopy(source.horizontalBounds.getWidth(), safeGet(option, WIDTH));
			}
			 
			if (!autoHeight) { 
				ld.fullHeight = switchCopy(source.horizontalBounds.getHeight(), safeGet(option, HEIGHT));
			}
			
			Point2D topLeft = mergeRegistrations(source, option, ld.fullWidth, ld.fullHeight, X, Y);
			horizontalBounds = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), ld.fullWidth, ld.fullHeight);
			Point2D reg = Registrations.topLeftToRegistration(registration, horizontalBounds);

			GeneralPath renderedText = renderText();

			GeneralPath layout = (GeneralPath) renderedText.clone();
			layout.transform(AffineTransform.getTranslateInstance(reg.getX(), reg.getY()));
			super.updateBoundsRef(layout.getBounds2D());
			renderedTextRef = new SoftReference(renderedText);
		}
	}
	
	private GeneralPath renderText() {
		LayoutDescription ld = computeLayout(text, format);
		GeneralPath renderedText = layoutText(text, ld, format);
		Point2D reg = new Point2D.Double((Double) get("X"), (Double) get("Y"));
		renderedText.transform(AffineTransform.getTranslateInstance(horizontalBounds.getX()-reg.getX(), horizontalBounds.getY()-reg.getY()));
		renderedText.transform(AffineTransform.getRotateInstance(Math.toRadians(rotation)));
		return renderedText;
	}
	
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}

	public String getImplantation() {return IMPLANTATION;}

	
	public Object get(String name) {
		if (TEXT.is(name)) {return text;}
		if (SCALE_BY.is(name)) {return scaleBy;}
		if (X.is(name)) {return Registrations.topLeftToRegistration(registration, horizontalBounds).getX();}
		if (Y.is(name)) {return Registrations.topLeftToRegistration(registration, horizontalBounds).getY();}
		if (ROTATION.is(name)) {return rotation;}
		if (HEIGHT.is(name)) {return horizontalBounds.getHeight();}
		if (WIDTH.is(name)) {return horizontalBounds.getWidth();}
		if (contains(TextProperty.class,name)) {return TextFormats.get(name, format);}
		return super.get(name);
	}

	
	//TODO: Convert this to a more general POINT scaling method, look at line/polyline for inspiration
	protected void fixScale(Graphics2D g) {
		AffineTransform trans = g.getTransform();
		
		double scale = -1;
		
		if (scaleBy.equals("ALL")) {return;} //Scale by all...do nothing to the current transform
		else if (scaleBy.equals("X")) {scale = trans.getScaleX();}
		else if (scaleBy.equals("Y")) {scale = trans.getScaleY();}
		else if (scaleBy.equals("LARGEST")) {scale = Math.min(trans.getScaleX(), trans.getScaleY());}
		else if (scaleBy.equals("NONE")) {scale = 1;}
		else if (scaleBy.equals("SMALLEST")){scale = Math.min(trans.getScaleX(), trans.getScaleY());}
		else {throw new IllegalArgumentException("Attempted to use SCALE_BY of an unknown value " + scaleBy);}

		double scaleXBy = trans.getScaleX() == 0 ? 1 : scale/trans.getScaleX();
		double scaleYBy = trans.getScaleY() == 0 ? 1 : scale/trans.getScaleY();
		
		g.scale(scaleXBy, scaleYBy);
	}
	
	public void render(Graphics2D g, AffineTransform base) {
		g.setFont(format.font);
		g.setPaint(format.textColor);
		
		
		//Figure out where to render the thing while potentially ignoring the view transform.
		//The following guarantees that the registration point is at the same spot that it would have
		//been had it been rendered exactly under the view transform
		Point2D p = Registrations.topLeftToRegistration(registration, horizontalBounds);
		Point2D p2 = new Point2D.Double();
		fixScale(g);
		
		base.transform(p,p2);	//Figure out where to render
		try {g.getTransform().inverseTransform(p2, p2);}
		catch (Exception e) {p2 = p;}		
		g.translate(p2.getX(), p2.getY());
		
		GeneralPath renderedText = renderedTextRef.get();
		if (renderedText == null) {renderedText = renderText();}
		
		g.fill(renderedText);	//Render
		super.postRender(g, base);
	}	
	
	private static GeneralPath layoutText(String text, LayoutDescription ld, Format format) {
		Graphics2D g = REFERENCE_GRAPHICS;
		FontRenderContext context = g.getFontRenderContext();
		FontMetrics fm = g.getFontMetrics(format.font);
		final String[] lines =SPLITTER.split(text);

		GeneralPath compound = new GeneralPath();
		
		for (int i=0; i< lines.length; i++) {
			final DoubleDimension dim = ld.dims[i];
			String line = lines[i];
			
			double x = horizontalOffset(dim.width, ld.fullWidth, format.justification);
			double y = dim.height * i + fm.getAscent();
			
			java.awt.Shape s = format.font.createGlyphVector(context, line).getOutline((float) x, (float) y);
			compound.append(s, false);
		}

		return compound;
	}
	
	/**Get the appropriate metrics for the layout of the requested text.
	 */
	//TODO: Do width-based line wrapping, will have to pass in width to do so
	private static LayoutDescription computeLayout(String text, Format format) {
		Graphics2D g = REFERENCE_GRAPHICS;
		FontMetrics fm = g.getFontMetrics(format.font);

        LayoutDescription ld = new LayoutDescription(SPLITTER.split(text));
        
        double height = fm.getHeight();
        double maxWidth=0;
        
        for (int i=0; i<ld.lines.length; i++) {
        	String line = ld.lines[i];
        	double width = fm.getStringBounds(line, g).getWidth();
        	ld.dims[i] = new DoubleDimension(width, height);
        	
        	maxWidth = Math.max(maxWidth, width);
        }        

        ld.fullWidth = maxWidth;
        ld.fullHeight = height * ld.lines.length;
        
        return ld;
	}

	private static double horizontalOffset(double lineWidth, double spaceWidth, float justification) {
		if (justification == Component.LEFT_ALIGNMENT) {return 0;}

		if (justification == Component.RIGHT_ALIGNMENT) {return spaceWidth- lineWidth;}
		
		if (justification == Component.CENTER_ALIGNMENT) {return spaceWidth/2.0 - lineWidth/2.0;}
		
		throw new RuntimeException("Unknown justification value: " + justification);
	}
	
	public Text update(Tuple t) throws IllegalArgumentException {
		if (Tuples.transferNeutral(t, this)) {return this;}
		
		return new Text(this, t, UNSETTABLES);
	}
	public Text updateID(String id) {return new Text(id, this);}
}
