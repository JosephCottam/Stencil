package stencil.adapters.java2D.data.immutibles;


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
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
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.streams.Tuple;
import stencil.util.DoubleDimension;

public class Text extends Point {
	
	public static final class TextShape extends Text {
		protected static final AttributeList ATTRIBUTES = new AttributeList(Text.ATTRIBUTES);
		protected static final AttributeList UNSETTABLES = new AttributeList();
		private static final String IMPLANTATION = "TEXT_SHAPE";
		
		static {
			UNSETTABLES.add(SCALE_BY);
		}
		
		public TextShape(Table layer, String id) {super(layer, id);}
		public TextShape(Table layer, TextShape source, Tuple option) {
			super(layer, source, option, UNSETTABLES);
		}
		
		public final String getImplantation() {return IMPLANTATION;}
		protected final void fixScale(Graphics2D g) {/*deliberately does nothing*/}
	}
	
	protected static final AttributeList ATTRIBUTES = new AttributeList(Point.ATTRIBUTES);
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
	protected static final Attribute<String> SCALE_BY = new Attribute("SCALE_BY", "SMALLEST");
		
	static {
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(TEXT);
		ATTRIBUTES.add(SCALE_BY);
		ATTRIBUTES.add(ROTATION);
		
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
	
	public Text(Table layer, String id) {
		super(layer, id);
		
		text = TEXT.defaultValue;
		scaleBy = SCALE_BY.defaultValue;
		format = new Format();
		rotation = ROTATION.defaultValue;
		autoWidth = true;
		autoHeight = true;

		bounds = new Rectangle2D.Double(X.defaultValue, Y.defaultValue, WIDTH.defaultValue, HEIGHT.defaultValue);
		drawBounds  = (Rectangle2D) bounds.clone(); 
	}
	
	protected Text(Table layer, Text source, Tuple option) {
		this(layer, source, option, UNSETTABLES);
	}

	protected Text(Table layer, Text source, Tuple option, AttributeList unsettables) {
		super(layer, source, option, unsettables);

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
				
		DoubleDimension[] sizes = computeLayout(null);
		 
		double width, height;
		if (autoWidth) {width = sizes[sizes.length-1].width;} 
		else {width = switchCopy(source.bounds.getWidth(), safeGet(option, WIDTH));}
		 
		if (autoHeight) {height = sizes[sizes.length-1].height;}
		else {height = switchCopy(source.bounds.getHeight(), safeGet(option, HEIGHT));}

		Point2D topLeft = mergeRegistrations(source, option, width, height, X, Y);
		bounds = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), width, height);
		
		GeneralPath p = new GeneralPath(new Rectangle2D.Double(0,0, width, height));
		p.transform(AffineTransform.getRotateInstance(Math.toRadians(rotation)));
		p.transform(AffineTransform.getTranslateInstance(topLeft.getX(), topLeft.getY()));
		drawBounds = p.getBounds2D();
	}

	
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	public String getImplantation() {return IMPLANTATION;}

	
	public Object get(String name) {
		if (TEXT.is(name)) {return text;}
		if (SCALE_BY.is(name)) {return scaleBy;}
		if (X.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getX();}
		if (Y.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getY();}
		if (contains(TextProperty.class,name)) {return TextFormats.get(name, format);}
		return super.get(name);
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
		
		fixScale(g);
		
		g.setFont(format.font);
		g.setPaint(format.textColor);

		final DoubleDimension[] dims = computeLayout(g);
		final String[] lines =SPLITTER.split(text);

		g.translate(bounds.getX(), bounds.getY());
		g.rotate(Math.toRadians(this.rotation));
		
		for (int i=0; i< lines.length; i++) {
			final DoubleDimension dim = dims[i];
			String line = lines[i];
			
			double x = horizontalOffset(dim.width);
			double y = dim.height * i + (dim.height*.8); //TODO: get standard ascent instead...this is just an approximation of baseline to top of ascent

			g.drawString(line, (float) x, (float)y);

			//TODO: Stop drawing when you run out of height
		}
		
		super.postRender(g, base);
	}	
	
	private double horizontalOffset(double lineWidth) {
		if (format.justification == Component.LEFT_ALIGNMENT) {return 0;}

		if (format.justification == Component.RIGHT_ALIGNMENT) {
			return bounds.getWidth() - lineWidth;
		}
		if (format.justification == Component.CENTER_ALIGNMENT) {return bounds.getWidth()/2.0 - lineWidth/2.0;}
		throw new RuntimeException("Unknown justification value: " + format.justification);
	}

	/**Get the appropriate metrics for the layout of the requested text.
	 * The last entry of the returned dimensions array DOES NOT CORRESPONDE TO A SINGLE LINE;
	 * it is the overall dimensions.  Iterate on the lines, not on the dimensions when working with dimensions. 
	 * 
	 */
	//TODO: Do width-based line wrapping
	private DoubleDimension[] computeLayout(Graphics2D g) {
        Graphics2D g2 = g ==null? REFERENCE_GRAPHICS: g;
		FontMetrics fm = g2.getFontMetrics(format.font);
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

	public Rectangle2D getBoundsReference() {return drawBounds;}

	
	public Text update(Tuple t) throws IllegalArgumentException {return new Text(this.layer, this, t);}
	public Text updateLayer(Table layer) {return new Text(layer, this, Tuple.EMPTY_TUPLE);}

	
}
