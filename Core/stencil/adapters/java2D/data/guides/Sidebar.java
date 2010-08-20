package stencil.adapters.java2D.data.guides;

import  static stencil.parser.ParserConstants.SIMPLE_DEFAULT;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.adapters.java2D.data.glyphs.*;
import stencil.parser.ParseStencil;
import stencil.parser.tree.Guide;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.types.Converter;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.util.collections.ArrayUtil;

public class Sidebar extends Guide2D {
	
	public static final String IMPLANTATION_NAME = "SIDE_BAR";
	public static final String LABEL_PROPERTY_TAG = "label";
	public static final String EXAMPLE_PROPERTY_TAG = "example";
	
	private static final String defaultArguments = "[sample: \"CATEGORICAL\", label.FONT: 4, label.FCOLOR: \"BLACK\", example.SIZE: 4, spacing: .25, displayOn: \"" + SIMPLE_DEFAULT + "\"]";
	public static final Specializer DEFAULT_ARGUMENTS;
	static {
		try {DEFAULT_ARGUMENTS = ParseStencil.parseSpecializer(defaultArguments);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}
	
	private Text prototypeLabel = new Text(null, "prototype");
	private Shape prototypeExample = new Shape(null, "prototype");
	private final Collection<Glyph2D> marks = new ArrayList();
	private float exampleWidth;
	private float exampleHeight;

	private float vSpacing;
	private float hSpacing;
	
	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	public Double X;
	public Double Y;
	
	private boolean autoPlace = true;
	
	//Public because of how the applyDefaualts system works
	/**How far apart should elements be?**/
	public float spacing = .25f;

	//Public because of how the applyDefaualts system works
	/**Which of the example graphic attributes should be used to display the attribute?**/
	public String displayOn;
	
	private final TupleSorter sorter;
	private final int label_idx;
	private final int value_idx;
	
	public Sidebar(Guide guideDef, int idx) {
		super(guideDef);
		Specializer specializer = guideDef.getSpecializer();
		
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(specializer, this);
		
		prototypeLabel = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeLabel);
		prototypeLabel = GuideUtils.applyDefaults(specializer, LABEL_PROPERTY_TAG, prototypeLabel);

		prototypeExample = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, EXAMPLE_PROPERTY_TAG, prototypeExample);
		prototypeExample = GuideUtils.applyDefaults(specializer, EXAMPLE_PROPERTY_TAG, prototypeExample);
		
		exampleHeight = Math.max(Converter.toFloat(prototypeLabel.get(StandardAttribute.HEIGHT.name())), Converter.toFloat(prototypeExample.get("SIZE")));
		exampleWidth = Converter.toFloat(prototypeExample.get("SIZE"));
		vSpacing = spacing * exampleHeight;
		hSpacing = spacing * exampleWidth;
		
		
		if (specializer.getMap().containsKey(StandardAttribute.X.name()) || specializer.getMap().containsKey(StandardAttribute.Y.name())) {autoPlace = false;}
		if (SIMPLE_DEFAULT.equals(displayOn)) {displayOn = guideDef.getSelector().getAttribute();}
		
		TuplePrototype p = guideDef.getPrototype();		//Get input prototype
		label_idx = ArrayUtil.indexOf("Input", TuplePrototypes.getNames(p));
		value_idx = ArrayUtil.indexOf("Output", TuplePrototypes.getNames(p));
		
		assert label_idx >=0 : "Input field not found for labeling in results prototype";
		assert value_idx >=0 : "Output field not found for labeling in results prototype";

		sorter = new TupleSorter(label_idx);
	}

	public synchronized void setElements(List<Tuple> elements, Rectangle2D parentBounds) {
		marks.clear();
		
		Collections.sort(elements, sorter);
		marks.addAll(createLabeledBoxes(elements));
		
		bounds = GuideUtils.fullBounds(marks);
		if (!bounds.isEmpty()) {
			if (autoPlace) {
				X = parentBounds.getMaxX() + bounds.getWidth();
				Y = parentBounds.getMinY() + bounds.getHeight();
			}
			this.bounds = new Rectangle2D.Double(bounds.getX()+X,bounds.getY()+Y, bounds.getWidth(), bounds.getHeight());
		}
	}
	
	public Rectangle2D getBoundsReference() {return bounds;}
	
	private Collection<Glyph2D> createLabeledBoxes(List<Tuple> elements) {
		Collection<Glyph2D> marks = new ArrayList<Glyph2D>(elements.size() *2);
		for (int i=0; i< elements.size(); i++) {
			Tuple t = elements.get(i);
			marks.addAll(createLabeledBox(t, i));
		}
		return marks;
	}
	
	private Collection<Glyph2D> createLabeledBox(Tuple contents, int idx) {
		float indexOffset = (idx * exampleHeight) + (idx * vSpacing);  
		
		String[] labelFields = new String[]{"X","Y","TEXT", "REGISTRATION"};
		Object[] labelValues = new Object[]{hSpacing, indexOffset, contents.get(label_idx), "LEFT"};
		Text label = prototypeLabel.update(new PrototypedTuple(labelFields, labelValues));
		label = label.update(Tuples.sift("label.", contents));
		
		String[] exampleFields = new String[]{"Y", displayOn, "REGISTRATION"};
		Object[] exampleValues = new Object[]{indexOffset, contents.get(value_idx), "RIGHT"};
		Shape example = prototypeExample.update(new PrototypedTuple(exampleFields, exampleValues));
		example = example.update(Tuples.sift("example.", contents));
		
		return Arrays.asList(new Glyph2D[]{label, example});
	}	


	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		g.translate(X, Y);
		AffineTransform localTransform = g.getTransform();
		for (Glyph2D mark: marks) {
			mark.render(g, localTransform);
		}
		g.setTransform(viewTransform);
	}
}
