package stencil.adapters.java2D.render.guides;

import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;
import static stencil.parser.ParserConstants.GUIDE_LABEL;
import static stencil.parser.ParserConstants.INPUT_FIELD;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.adapters.java2D.columnStore.CompoundTable;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.CompoundRenderer;
import stencil.adapters.java2D.render.Renderer;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.instances.Singleton;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.types.geometry.PointTuple;
import stencil.util.collections.ListSet;

import static stencil.adapters.java2D.render.guides.Legend.*;


//TODO:  Move this into a stencil file
public class CrossLegend extends Guide2D {
	private static final PrototypedTuple NO_ELEMENT = Singleton.from("ele", new ListSet());
	private static final PrototypedTuple NO_LABEL = Singleton.from("label", new ListSet());
	
	private CompoundTable data;
	private final CompoundRenderer renderer;
	private final PrototypedTuple updateMask;

	private final boolean autoPlace;
	private final String guideLabel;
	private final int[] label_idx;

	private double legendX, legendY;
	private int idCounter =0;


	public CrossLegend(Guide def, int legendCount) {
		super(def);
		
		Specializer spec = guideDef.specializer();
		autoPlace = !(spec.containsKey(Renderer.X.name()) || spec.containsKey(Renderer.Y.name()));
		guideLabel = Converter.toString(spec.get(GUIDE_LABEL));

		if (!autoPlace) {
			legendX = spec.containsKey("X") ? Converter.toDouble(spec.get("X")) : 0;
			legendY = spec.containsKey("Y") ? Converter.toDouble(spec.get("Y")) : 0;
		}

		
		data = makeTable(guideDef);
		updateMask = Tuples.merge(data.updateMaskTuple(), Tuples.delete(DEFAULT_SPECIALIZER, SPEC_NON_VALUES));
		renderer = new CompoundRenderer(data.prototype());
		
		TuplePrototype p = guideDef.rule().prototype();
		ArrayList<Integer> idxs = new ArrayList();
		for (int i=0; i< p.size(); i++) {if (p.get(i).name().startsWith(INPUT_FIELD)) {idxs.add(i);}}
		label_idx = new int[idxs.size()];
		for (int i=0; i<idxs.size(); i++) {label_idx[i] =idxs.get(i);}
		
		assert label_idx.length == 2 : "Can only cross on two elements";
	}
	
	private static CompoundTable makeTable(Guide guideDef) {
		String identifier = guideDef.identifier();
		String exampleType = guideDef.specializer().containsKey(GEOM_TAG) ? Converter.toString(guideDef.specializer().get(GEOM_TAG)) : "SHAPE";

		Table labels = LayerTypeRegistry.makeTable(LABEL_PROPERTY_TAG, "TEXT");
		Table elements = LayerTypeRegistry.makeTable(GUIDE_ELEMENT_TAG , exampleType);
		return new CompoundTable(identifier, labels, elements);
	}

	@Override
	public void setElements(List<PrototypedTuple> elements, Rectangle2D parentBounds, AffineTransform viewTransform) {
		data = makeTable(guideDef);
		List<SortedSet> labels = labels(elements);
		List<List<PrototypedTuple>> splitElements = split(elements, labels);
	
		Collection<PrototypedTuple> exampleTuples = createExamples(labels, splitElements);
		Collection<PrototypedTuple> labelTuples = createLabels(labels);
		for (PrototypedTuple item:exampleTuples) {data.update(item);}	
		for (PrototypedTuple item:labelTuples) {data.update(item);}
		
		Table.Util.genChange(data, renderer, viewTransform);
		
		data.update(createGuideLabel(data.getBoundsReference()));
		Table.Util.genChange(data, renderer, viewTransform);
	}

		
	private Collection<PrototypedTuple> createLabels(List<? extends Collection> labels) {
		ArrayList<PrototypedTuple> marks = new ArrayList();
		int col=2, row=2;	//start at 1 so the overall label can be applied
		for (Object label: labels.get(0)) {marks.add(createLabel(row++, 1, "RIGHT", 0, Converter.toString(label)));}
		for (Object label: labels.get(1)) {marks.add(createLabel(1, col++, "LEFT", Math.toRadians(-90), Converter.toString(label)));}
		return marks;
	}
	
	private static final String[] LABEL_FIELDS = new String[]{"label.X","label.Y","label.TEXT", "label.REGISTRATION", "label.ROTATION", "label.ID", "ID"};
	private PrototypedTuple createLabel(int row, int col, String reg, double rotation, String text) {
		PointTuple place = layout(row, col);
		
		Object[] labelValues = new Object[]{place.x(), place.y(), text, reg, rotation, idCounter++, idCounter++};
		PrototypedTuple label = new PrototypedArrayTuple(LABEL_FIELDS, labelValues);
		label = Tuples.merge(updateMask, label);
		label = Tuples.restructure(label, "label", "ele");
		label = Tuples.merge(label, NO_ELEMENT);
		
		return label;
	}
	
		
	private Collection<PrototypedTuple> createExamples(List<SortedSet> labels, List<List<PrototypedTuple>> elements) {
		Collection<PrototypedTuple> marks = new ArrayList();
		for (int col=0; col< elements.size(); col++) {
			for (int row=0; row< elements.get(col).size(); row++) {
				PrototypedTuple t = elements.get(col).get(row);
				marks.add(createExample(labels, t));
			}
		}
		return marks;
	}
	
	private static final float exampleHeight =4f;
	private static final float vSpacing = 3f;
	private static final float hSpacing = 2f;
	private static final String[] EXAMPLE_FIELDS = new String[]{"ele.X", "ele.Y", "ele.REGISTRATION", "ele.ID", "ID"};
	private PrototypedTuple createExample(List<SortedSet> labels, PrototypedTuple contents) {
		int row = labels.get(0).headSet(contents.get(INPUT_FIELD)).size() +2;	//+2 accounts for labels
		int col = labels.get(1).headSet(contents.get(INPUT_FIELD + "1")).size() +2;
		PointTuple place = layout(row, col);
		
		Object[] values = new Object[]{place.x(), place.y(), "CENTER", idCounter++, idCounter++};
		PrototypedTuple example = new PrototypedArrayTuple(EXAMPLE_FIELDS , values); 
		example = Tuples.mergeAll(updateMask, example, contents);
		example = Tuples.restructure(example, "ele", "label");
		example = Tuples.delete(example, INPUT_FIELD, INPUT_FIELD + "1");
		example = Tuples.merge(example, NO_LABEL);
		
		return example;
	}	

	private PointTuple layout(int row, int col) {
		double xp = legendX + (col * exampleHeight + (col * vSpacing));
		double yp = legendY - (row * exampleHeight + (row - hSpacing));
		
		return new PointTuple(xp,yp);
	}
	
	
	/**Determine the unique row and column labels for the matrix.*/
	private List<SortedSet> labels(List<? extends Tuple> elements) {
		List<SortedSet> labelss = new ArrayList();
		for (int i=0; i< label_idx.length; i++) {labelss.add(new TreeSet());}
		for (Tuple t: elements) {
			for (int i=0; i< label_idx.length; i++) {
				SortedSet labels = labelss.get(i);
				labels.add(t.get(label_idx[i]));
			}
		}		
		return labelss;
	}
	
	/**Divide the elements into groups according to the first labeling field.*/
	private List<List<PrototypedTuple>> split(List<PrototypedTuple> elements, List<SortedSet> labels) {
		List<List<PrototypedTuple>> split = new ArrayList();
		for (int i=0; i<labels.get(0).size(); i++) {split.add(new ArrayList());}
		for (PrototypedTuple t: elements) {
			String key = Converter.toString(t.get(label_idx[0]));
			int set = labels.get(0).tailSet(key).size()-1;
			split.get(set).add(t);
		}
		return split;
	}
	
	private PrototypedTuple createGuideLabel(Rectangle2D bounds) {
		Object[] labelValues = new Object[]{bounds.getCenterX(), -bounds.getMinY(), guideLabel, "BOTTOM", 0, -1, -1};
		PrototypedTuple t = new PrototypedArrayTuple(LABEL_FIELDS, labelValues);
		t = Tuples.merge(updateMask, t);
		t = Tuples.restructure(t, GUIDE_ELEMENT_TAG, LABEL_PROPERTY_TAG);
		t = Tuples.merge(t, NO_ELEMENT);
		return t;
	}

	
	@Override
	public Rectangle2D getBoundsReference() {return data.getBoundsReference();}

	@Override
	public void render(Graphics2D g, AffineTransform viewTransform) {
		renderer.render(data.tenured(), g, viewTransform);
	}

}
