package stencil.adapters.java2D.data.guides;

import static stencil.parser.ParserConstants.SIMPLE_DEFAULT;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.adapters.java2D.data.glyphs.*;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.util.AutoguidePair;
import stencil.util.BasicTuple;

public class Sidebar implements Guide2D {
	private class Pair {
		String label;
		Object value;
		
		public Pair(String label, Object value) {
			this.label = label;
			this.value = value;
		}
		public String toString() {return "(" + label + "," + value + ")";}
	}
	
	public static final String IMPLANTATION_NAME = "SIDE_BAR";
	public static final String LABEL_PROPERTY_TAG = "label";
	public static final String EXAMPLE_PROPERTY_TAG = "example";
	
	private static final String defaultArguments = "[label.FONT_SIZE=1, label.FONT_COLOR=@color(BLACK), example.SIZE=.8, spacing=.25, displayOn=\"" + SIMPLE_DEFAULT + "\"]";
	public static final Specializer DEFAULT_ARGUMENTS;
	static {
		try {DEFAULT_ARGUMENTS = ParseStencil.parseSpecializer(defaultArguments);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}

	
	private Text prototypeLabel = new Text(null, "prototype");
	private Shape prototypeExample = new Shape(null, "prototype");
	private Collection<Glyph2D> marks;
	private float exampleWidth;
	private float exampleHeight;

	private float vSpacing;
	private float hSpacing;
	
	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	public double X;
	public double Y;
	
	private boolean autoPlace = true;
	
	//Public because of how the applyDefaualts system works
	public float spacing = .25f;

	//Public because of how the applyDefaualts system works
	public String displayOn;
	
	public Sidebar(String id, Specializer specializer, int idx) {
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(specializer, this);
		
		prototypeLabel = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeLabel);
		prototypeLabel = GuideUtils.applyDefaults(specializer, LABEL_PROPERTY_TAG, prototypeLabel);

		prototypeExample = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, EXAMPLE_PROPERTY_TAG, prototypeExample);
		prototypeExample = GuideUtils.applyDefaults(specializer, EXAMPLE_PROPERTY_TAG, prototypeExample);
		
		
		exampleHeight = Math.max((Float) prototypeLabel.get(StandardAttribute.HEIGHT.name(), Float.class), (Float)  prototypeExample.get("SIZE", Float.class));
		exampleWidth = (Float) prototypeExample.get("SIZE", Float.class);
		vSpacing = spacing * exampleHeight;
		hSpacing = spacing * exampleWidth;
		
		
		if (specializer.getMap().containsKey(StandardAttribute.X.name()) || specializer.getMap().containsKey(StandardAttribute.Y.name())) {autoPlace = false;}
		if (SIMPLE_DEFAULT.equals(displayOn)) {displayOn = id;}
	}

	public void setElements(List<AutoguidePair> elements) {
		List<Pair> listing = validate(elements);
		marks = createLabeledBoxes(listing);
		
		Rectangle2D bounds = GuideUtils.fullBounds(marks);
		if (autoPlace) {
			X = -1d * bounds.getWidth();
			Y = -1d * bounds.getHeight();
		}
		this.bounds = new Rectangle2D.Double(X,Y, bounds.getWidth(), bounds.getY());
	}
	
	public Rectangle2D getBoundsReference() {return bounds;}
	
	private Collection<Glyph2D> createLabeledBoxes(List<Pair> elements) {
		Collection<Glyph2D> marks = new ArrayList<Glyph2D>(elements.size() *2);
		for (int i=0; i< elements.size(); i++) {
			marks.addAll(createLabeledBox(elements.get(i), i));
		}
		return marks;
	}
	
	private Collection<Glyph2D> createLabeledBox(Pair contents, int idx) {
		float indexOffset = (idx *exampleHeight) + (idx * vSpacing);  

		String[] labelFields = new String[]{"X","Y","TEXT"};
		Object[] labelValues = new Object[]{exampleWidth + hSpacing, indexOffset, contents.label};
		Text label = prototypeLabel.update(new BasicTuple(labelFields, labelValues));
		
		String[] exampleFields = new String[]{"Y", displayOn};
		Object[] exampleValues = new Object[]{indexOffset, contents.value};
		Shape example = prototypeExample.update(new BasicTuple(exampleFields, exampleValues));
		
		return Arrays.asList(new Glyph2D[]{label, example});
	}

	
	/**Verify that all Autoguide pairs have exactly one result, and that result
	 * is some type of Number.
	 * 
	 * @param elements
	 * @return
	 */
	private List<Pair> validate(Collection<AutoguidePair> elements) {
		List<Pair> pairs = new ArrayList<Pair>(elements.size());
		Comparator c = new Comparator<Pair>() {
			public int compare(Pair p1, Pair p2) { 
				String l1 = p1.label;
				String l2 = p2.label;
				
				return l1.compareTo(l2);				
			}
			
		};
				
		for (AutoguidePair p: elements) {
			//TODO: Validate that the result is of the right type for the attribute it is to be applied to...
			Pair pair = new Pair(p.getInput()[0].toString(), p.getResult()[0]);
			pairs.add(pair);
		}
		
		Collections.sort(pairs, c);
		return pairs;
	}

	public void render(Graphics2D g, AffineTransform viewTransform) {
		g.translate(X, Y);
		AffineTransform localTransform = g.getTransform();
		if (marks == null) {return;}
		for (Glyph2D mark: marks) {mark.render(g, localTransform);}
		g.setTransform(viewTransform);
	}
}