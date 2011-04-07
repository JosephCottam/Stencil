package stencil.adapters.java2D.data.guides;

import  static stencil.parser.ParserConstants.GUIDE_LABEL;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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

import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;

/**Legend that supports cross products of categorical attributes.
 */
public class CrossLegend extends Guide2D {
	
	public static final String IMPLANTATION_NAME = "SIDE_BAR";
	public static final String LABEL_PROPERTY_TAG = "label";
	
	private static final String defaultArguments = "[label.FONT: 4, label.FCOLOR: \"BLACK\", element.SIZE: 4, spacing: .25]";
	public static final Specializer DEFAULT_ARGUMENTS;
	static {
		try {DEFAULT_ARGUMENTS = ParseStencil.specializer(defaultArguments);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}
	
	private Text prototypeLabel = new Text("prototype");
	private Shape prototypeExample = new Shape("prototype");
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
	
	private final TupleSorter[] sorters;
	private final int[] label_idx;
	private final String guideLabel;
	
	
	public CrossLegend(Guide guideDef, int idx) {
		super(guideDef);
		
		Specializer specializer = guideDef.specializer();
		
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(specializer, this);
		
		prototypeLabel = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeLabel);
		prototypeLabel = GuideUtils.applyDefaults(specializer, LABEL_PROPERTY_TAG, prototypeLabel);

		prototypeExample = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, GUIDE_ELEMENT_TAG, prototypeExample);
		prototypeExample = GuideUtils.applyDefaults(specializer, GUIDE_ELEMENT_TAG, prototypeExample);
		
		guideLabel = (String) specializer.get(GUIDE_LABEL);
		
		exampleHeight = Math.max(Converter.toFloat(prototypeLabel.get(StandardAttribute.HEIGHT.name())), Converter.toFloat(prototypeExample.get("SIZE")));
		exampleWidth = Converter.toFloat(prototypeExample.get("SIZE"));
		vSpacing = spacing * exampleHeight;
		hSpacing = spacing * exampleWidth;
		
		
		if (specializer.containsKey(StandardAttribute.X.name()) || specializer.containsKey(StandardAttribute.Y.name())) {autoPlace = false;}
		
		TuplePrototype p = guideDef.resultsPrototype();
		ArrayList<Integer> idxs = new ArrayList();
		for (int i=0; i< p.size(); i++) {if (p.get(i).getFieldName().startsWith("Input")) {idxs.add(i);}}
		sorters = new TupleSorter[idxs.size()];
		label_idx = new int[idxs.size()];
		for (int i=0; i<idxs.size(); i++) {
			label_idx[i] =idxs.get(i);
			sorters[i] = new TupleSorter(label_idx[i]);
		}
		
		assert label_idx.length == 2 && sorters.length ==2 : "Can only cross on two elements";
	}

	public synchronized void setElements(List<Tuple> elements, Rectangle2D parentBounds) {
		marks.clear();
		
		List<SortedSet<String>> labels = labels(elements);
		List<List<Tuple>> splitElements = split(elements, labels);
		marks.addAll(createExamples(splitElements));
		marks.addAll(createLabels(labels));
		
		bounds = GuideUtils.fullBounds(marks);
		if (!bounds.isEmpty()) {
			if (autoPlace) {
				X = parentBounds.getMaxX() + bounds.getWidth();
				Y = parentBounds.getMinY();
			}
			this.bounds = new Rectangle2D.Double(bounds.getX()+X,bounds.getY()+Y, bounds.getWidth(), bounds.getHeight());
		}
		Glyph2D gl = createGuideLabel();
		marks.add(gl);
		bounds.add(gl.getBoundsReference());
	}
	
	/**Determine the unique row and column labels for the matrix.*/
	private List<SortedSet<String>> labels(List<Tuple> elements) {
		List<SortedSet<String>> labelss = new ArrayList();
		for (int i=0; i< label_idx.length; i++) {labelss.add(new TreeSet());}
		for (Tuple t: elements) {
			for (int i=0; i< label_idx.length; i++) {
				SortedSet<String> labels = labelss.get(i);
				labels.add(Converter.toString(t.get(label_idx[i])));
			}
		}
		return labelss;
	}
	
	/**Divide the elements into groups according to the first labling field.*/
	private List<List<Tuple>> split(List<Tuple> elements, List<SortedSet<String>> labels) {
		for (TupleSorter sorter: sorters) {Collections.sort(elements, sorter);}
		
		List<List<Tuple>> split = new ArrayList();
		for (@SuppressWarnings("unused") String s: labels.get(0)) {split.add(new ArrayList());}
		for (Tuple t: elements) {
			String key = Converter.toString(t.get(label_idx[0]));
			int set = labels.get(0).tailSet(key).size()-1;
			split.get(set).add(t);
		}
		return split;
	}
	
	public Rectangle2D getBoundsReference() {return bounds;}

	private Glyph2D createGuideLabel() {
		Font font = (Font) prototypeLabel.get("FONT");
		String[] labelFields = new String[]{"FONT", "X","Y","TEXT", "REGISTRATION"};
		Object[] labelValues = new Object[]{font, 0, 0, guideLabel, "BOTTOM_LEFT"};
		Text label = prototypeLabel.update(new PrototypedTuple(labelFields, labelValues));
		return label;
	}

	
	private Collection<Glyph2D> createExamples(List<List<Tuple>> elements) {
		Collection<Glyph2D> marks = new ArrayList<Glyph2D>();
		for (int col=0; col< elements.size(); col++) {
			for (int row=0; row< elements.get(col).size(); row++) {
				Tuple t = elements.get(col).get(row);
				marks.add(createExample(col+2, row+2, t));	//i+2 because of the labels	
			}
		}
		return marks;
	}
	
	private List<Glyph2D> createLabels(List<? extends Collection<String>> labels) {
		ArrayList<Glyph2D> marks = new ArrayList<Glyph2D>();
		int col=2, row=2;	//start at 1 so the overall label can be applied
		for (String label: labels.get(0)) {marks.add(createLabel(row++, 1, "RIGHT", 0, label));}
		for (String label: labels.get(1)) {marks.add(createLabel(1, col++, "LEFT", -90, label));}
		return marks;
	}
	
	private Glyph2D createLabel(int row, int col, String reg, int rotation, String text) {
		float x = col * exampleHeight + (col * vSpacing);
		float y = row * exampleHeight + (row * hSpacing);
		
		String[] labelFields = new String[]{"X","Y","TEXT", "REGISTRATION", "ROTATION"};
		Object[] labelValues = new Object[]{x,y, text, reg, rotation};
		Text label = prototypeLabel.update(new PrototypedTuple(labelFields, labelValues));
		return label;
	}
	
	// TODO: Move the layout/registration to guide rules		
	private Glyph2D createExample(int row, int col, Tuple contents) {
		float x = col * exampleHeight + (col * vSpacing);
		float y = row * exampleHeight + (row * hSpacing);
		
		String[] exampleFields = new String[]{"X", "Y", "REGISTRATION"};
		Object[] exampleValues = new Object[]{x,y, "CENTER"};
		Shape example = prototypeExample.update(new PrototypedTuple(exampleFields, exampleValues));
		example = example.update(Tuples.sift(GUIDE_ELEMENT_TAG, contents));
		return example;
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
