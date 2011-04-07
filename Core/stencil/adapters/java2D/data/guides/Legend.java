package stencil.adapters.java2D.data.guides;

import  static stencil.parser.ParserConstants.GUIDE_LABEL;

import java.awt.Font;
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
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.types.Converter;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.util.collections.ArrayUtil;

import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;
import static stencil.parser.ParserConstants.SEPARATOR;
import static stencil.display.DisplayLayer.TYPE_KEY;

public class Legend extends Guide2D {
	public static final String GEOM_TAG = "layer" + SEPARATOR + TYPE_KEY;
	public static final String LABEL_PROPERTY_TAG = "label";
	
	private static final String defaultArguments = "[label.FONT: 4, label.FCOLOR: \"BLACK\", element.SIZE: 4, spacing: .25]";
	public static final Specializer DEFAULT_ARGUMENTS;
	static {
		try {DEFAULT_ARGUMENTS = ParseStencil.specializer(defaultArguments);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}
	
	private Text prototypeLabel = new Text("prototype");
	private final Glyph2D prototypeExample;
	private final Collection<Glyph2D> marks = new ArrayList();
	private float exampleWidth;
	private float exampleHeight;

	private float vSpacing;
	private float hSpacing;
	
	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	public double X=0d;
	public double Y=0d;
	
	private boolean autoPlace = true;
	
	//Public because of how the applyDefaualts system works
	/**How far apart should elements be?**/
	public float spacing = .25f;
	
	private final TupleSorter sorter;
	private final int label_idx;
	private final String guideLabel;
	
	
	public Legend(Guide guideDef, int idx) {
		super(guideDef);
		
		Specializer specializer = guideDef.specializer();
		
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(specializer, this);
		
		prototypeLabel = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeLabel);
		prototypeLabel = GuideUtils.applyDefaults(specializer, LABEL_PROPERTY_TAG, prototypeLabel);

		
		Glyph2D prototype = new Shape("prototype");  //CONSTRUCT BASED ON GEOM....
		prototype = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, GUIDE_ELEMENT_TAG, prototype);
		prototypeExample = GuideUtils.applyDefaults(specializer, GUIDE_ELEMENT_TAG, prototype);
		
		guideLabel = (String) specializer.get(GUIDE_LABEL);
		
		exampleHeight = Math.max(Converter.toFloat(prototypeLabel.get(StandardAttribute.HEIGHT.name())), Converter.toFloat(prototypeExample.get("SIZE")));
		exampleWidth = Converter.toFloat(prototypeExample.get("SIZE"));
		vSpacing = spacing * exampleHeight;
		hSpacing = spacing * exampleWidth;
		
		
		if (specializer.containsKey(StandardAttribute.X.name()) || specializer.containsKey(StandardAttribute.Y.name())) {autoPlace = false;}
		
		TuplePrototype p = guideDef.resultsPrototype();
		label_idx = ArrayUtil.indexOf("Input", TuplePrototypes.getNames(p));
		
		assert label_idx >=0 : "Input field not found for labeling in results prototype";

		sorter = new TupleSorter(label_idx);
	}

	public synchronized void setElements(List<Tuple> elements, Rectangle2D parentBounds) {
		marks.clear();
		
		marks.addAll(createGuideLabel());
		
		Collections.sort(elements, sorter);
		marks.addAll(createLabeledBoxes(elements));
		
		bounds = GuideUtils.fullBounds(marks);
		if (!bounds.isEmpty()) {
			if (autoPlace) {
				X = parentBounds.getMaxX() + bounds.getWidth();
				Y = parentBounds.getMinY();
			}
			this.bounds = new Rectangle2D.Double(bounds.getX()+X,bounds.getY()+Y, bounds.getWidth(), bounds.getHeight());
		}
	}
	
	public Rectangle2D getBoundsReference() {return bounds;}

	private Collection<Glyph2D> createGuideLabel() {
		List<Glyph2D> parts = new ArrayList(2);
		Font font = (Font) prototypeLabel.get("FONT");
		
		String[] labelFields = new String[]{"FONT", "X","Y","TEXT", "REGISTRATION"};
		Object[] labelValues = new Object[]{font, 0, 0, guideLabel, "LEFT"};
		Text label = prototypeLabel.update(new PrototypedTuple(labelFields, labelValues));
		parts.add(label);
		
		return parts;
	}

	
	private Collection<Glyph2D> createLabeledBoxes(List<Tuple> elements) {
		Collection<Glyph2D> marks = new ArrayList<Glyph2D>(elements.size() *2);
		for (int i=0; i< elements.size(); i++) {
			Tuple t = elements.get(i);
			marks.addAll(createLabeledBox(i+1, t));	//i+1 because of the guide label	
		}
		return marks;
	}
	
	// TODO: Move the layout/registration to guide rules		
	private Collection<Glyph2D> createLabeledBox(int idx, Tuple contents) {
		float indexOffset = (idx * exampleHeight) + (idx * vSpacing);  
		
		String[] labelFields = new String[]{"X","Y","TEXT", "REGISTRATION"};
		Object[] labelValues = new Object[]{hSpacing, indexOffset, contents.get(label_idx), "LEFT"};
		Text label = prototypeLabel.update(new PrototypedTuple(labelFields, labelValues));
		label = label.update(Tuples.sift(LABEL_PROPERTY_TAG, contents));
		
		String[] exampleFields = new String[]{"Y", "REGISTRATION"};
		Object[] exampleValues = new Object[]{indexOffset, "RIGHT"};
		Glyph2D example = prototypeExample.update(new PrototypedTuple(exampleFields, exampleValues));
		example = example.update(Tuples.sift(GUIDE_ELEMENT_TAG, contents));
		
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
