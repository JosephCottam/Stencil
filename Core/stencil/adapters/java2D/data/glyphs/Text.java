package stencil.adapters.java2D.data.glyphs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.lang.ref.SoftReference;
import java.util.regex.Pattern;

import static stencil.adapters.Adapter.REFERENCE_GRAPHICS;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Registrations;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.types.font.FontTuple;
import stencil.util.DoubleDimension;

public final class Text extends Basic {
	/**Describes the valid justifications**/
	public static enum Justification {LEFT, RIGHT, CENTER}
	
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
		
		public String toString() {
			return String.format("%1$s[fw=%2$f, fh=%3$f]", super.toString(), fullWidth, fullHeight);
		}
	}
	
	protected static final TuplePrototype PROTOTYPE;
	protected static final AttributeList ATTRIBUTES = new AttributeList(Basic.ATTRIBUTES);
	protected static final AttributeList UNSETTABLES = new AttributeList();
	public static final String IMPLANTATION = "TEXT";
	
	private static final Pattern SPLITTER = Pattern.compile("\n");
	
	private static final double AUTO_SIZE = -1;
	private static final Attribute<String> TEXT = new Attribute("TEXT", "");
	private static final Attribute<Double> X = new Attribute("X", 0d);
	private static final Attribute<Double> Y = new Attribute("Y", 0d);
	private static final Attribute<Double> ROTATION = new Attribute("ROTATION", 0d);
	private static final Attribute<Double> WIDTH = new Attribute(StandardAttribute.WIDTH.name(), AUTO_SIZE, Double.class);
	private static final Attribute<Double> HEIGHT = new Attribute(StandardAttribute.HEIGHT.name(), AUTO_SIZE, Double.class);
	private static final Attribute<Justification> JUSTIFY = new Attribute("JUSTIFY", Justification.LEFT); 
	private static final Attribute<Color>  COLOR = new Attribute("COLOR", Color.BLACK);
	private static final Attribute<Font>   FONT = new Attribute("FONT", FontTuple.DEFAULT_FONT);
	
	protected static final Attribute<String> SCALE_BY = new Attribute("SCALE_BY", "ALL");
		
	static {
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(TEXT);
		ATTRIBUTES.add(SCALE_BY);
		ATTRIBUTES.add(ROTATION);
		ATTRIBUTES.add(HEIGHT);
		ATTRIBUTES.add(WIDTH);
		ATTRIBUTES.add(FONT);
		ATTRIBUTES.add(COLOR);
		ATTRIBUTES.add(JUSTIFY);
	
		PROTOTYPE = new SimplePrototype(ATTRIBUTES.getNames(), ATTRIBUTES.getTypes());
	}

	private final String text;
	private final String scaleBy;
	private final Font font;
	private final Color color;
	private final Justification justify;
	
	private final boolean autoWidth;
	private final boolean autoHeight;
	
	private final Rectangle2D horizontalBounds;
	private final double rotation;
	
	private SoftReference<GeneralPath> renderedTextRef;
	
	public Text(String id) {
		super(id);
		
		text = TEXT.defaultValue;
		scaleBy = SCALE_BY.defaultValue;
		font = FONT.defaultValue;
		color = COLOR.defaultValue;
		justify = JUSTIFY.defaultValue;
		rotation = ROTATION.defaultValue;
		autoWidth = true;
		autoHeight = true;

		horizontalBounds = new Rectangle2D.Double(X.defaultValue, Y.defaultValue, WIDTH.defaultValue, HEIGHT.defaultValue);
		super.updateBoundsRef((Rectangle2D) horizontalBounds.clone());
	}
	
	protected Text(String id, Text source) {
		super(id, source);
		
		this.text = source.text;
		this.scaleBy = source.scaleBy;
		this.font = source.font;
		this.color = source.color;
		this.justify = source.justify;
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
		font = switchCopy(source.font, safeGet(option, FONT));
		color = switchCopy(source.color, safeGet(option, COLOR));
		justify = switchCopy(source.justify, safeGet(option, JUSTIFY));

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
			&& font.equals(source.font)
			&& color.equals(source.color)
			&& justify.equals(source.justify)
			&& !option.getPrototype().contains(X.name)
			&& !option.getPrototype().contains(Y.name) 
			&& !option.getPrototype().contains(WIDTH.name)
			&& !option.getPrototype().contains(HEIGHT.name)) {
			
			super.updateBoundsRef(source.bounds);
			this.horizontalBounds = source.horizontalBounds;
		} else {
			LayoutDescription ld = computeLayout(text, font);
			
			if (!autoWidth) {
				ld.fullWidth = switchCopy(source.horizontalBounds.getWidth(), safeGet(option, WIDTH));
			}
			 
			if (!autoHeight) { 
				ld.fullHeight = switchCopy(source.horizontalBounds.getHeight(), safeGet(option, HEIGHT));
			}
			
			double x = switchCopy((Double) source.get(X.name), safeGet(option, X));
			double y = switchCopy((Double) source.get(Y.name), safeGet(option, Y));		
			Point2D reg = new Point2D.Double(x,y);
			
			Point2D topLeft = Registrations.registrationToTopLeft(registration, x,y, ld.fullWidth, ld.fullHeight);
			horizontalBounds = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), ld.fullWidth, ld.fullHeight);
			
			GeneralPath renderedText = renderText();

			GeneralPath layout = (GeneralPath) renderedText.clone();
			layout.transform(AffineTransform.getTranslateInstance(reg.getX(), reg.getY()));
			Rectangle2D layoutBounds = layout.getBounds2D();
			if (layoutBounds.isEmpty()) {
				layoutBounds = new Rectangle2D.Double(reg.getX(), reg.getY(), 0d, 0d);
			}
			super.updateBoundsRef(layoutBounds);
		}
	}
	
	private GeneralPath renderText() {
		LayoutDescription ld = computeLayout(text, font);
		GeneralPath renderedText = layoutText(text, ld, font, justify);
		Point2D reg = new Point2D.Double((Double) get("X"), (Double) get("Y"));
		renderedText.transform(AffineTransform.getTranslateInstance(horizontalBounds.getX()-reg.getX(), horizontalBounds.getY()-reg.getY()));
		renderedText.transform(AffineTransform.getRotateInstance(Math.toRadians(rotation)));
		return renderedText;
	}
	
	public AttributeList getPrototype() {return ATTRIBUTES;}
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
		if (FONT.is(name)) {return font;}
		if (COLOR.is(name)) {return color;}
		if (JUSTIFY.is(name)) {return justify;}
		return super.get(name);
	}

	
	//TODO: Convert this to a more general POINT scaling method, look at line/polyline for inspiration
	//TODO: Merge this (and the line/polyline version) into the general.ScaleWith version
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
		if (!visible) {return;}
		
		g.setFont(font);
		g.setPaint(color);
		
		
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
		

		GeneralPath renderedText;
		if (renderedTextRef == null) {
			renderedText = renderText();
			renderedTextRef = new SoftReference(renderedText);
		} else {
			renderedText = renderedTextRef.get();
		}
		if (renderedText == null) {renderedText = renderText();}
		
		g.fill(renderedText);	//Render
		super.postRender(g, base);
	}
	
	private static GeneralPath layoutText(String text, LayoutDescription ld, Font font, Justification justify) {
		Graphics2D g = REFERENCE_GRAPHICS;
		FontRenderContext context = g.getFontRenderContext();
		FontMetrics fm = g.getFontMetrics(font);
		final String[] lines =SPLITTER.split(text);

		GeneralPath compound = new GeneralPath();
		
		for (int i=0; i< lines.length; i++) {
			final DoubleDimension dim = ld.dims[i];
			String line = lines[i];
			
			double x = horizontalOffset(dim.width, ld.fullWidth, justify);
			double y = dim.height * i + fm.getAscent();
			
			int flags = Font.LAYOUT_LEFT_TO_RIGHT + Font.LAYOUT_NO_LIMIT_CONTEXT + Font.LAYOUT_NO_START_CONTEXT;
			GlyphVector v = font.layoutGlyphVector(context, line.toCharArray(), 0, line.length(), flags);
			compound.append(v.getOutline((float) x, (float) y), false);
		}

		return compound;
	}
	
	/**Get the appropriate metrics for the layout of the requested text.
	 */
	//TODO: Do width-based line wrapping, will have to pass in width to do so
	private static LayoutDescription computeLayout(String text, Font font) {
		Graphics2D g = REFERENCE_GRAPHICS;
		FontMetrics fm = g.getFontMetrics(font);

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

	private static double horizontalOffset(double lineWidth, double spaceWidth, Justification justification) {
		switch (justification) {
		   case LEFT: return 0;
		   case RIGHT: return spaceWidth- lineWidth;
		   case CENTER: return spaceWidth/2.0 - lineWidth/2.0;
		   default: throw new RuntimeException("Unknown justification value: " + justification);
		}
	}
	
	public Text update(Tuple t) throws IllegalArgumentException {
		if (Tuples.transferNeutral(t, this)) {return this;}
		
		return new Text(this, t, UNSETTABLES);
	}
	public Text updateID(String id) {return new Text(id, this);}
}
