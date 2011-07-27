package stencil.adapters.java2D.render.guides;

import  static stencil.parser.ParserConstants.GUIDE_LABEL;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.adapters.java2D.columnStore.CompoundTable;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.CompoundRenderer;
import stencil.adapters.java2D.render.Renderer;
import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.types.Converter;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.instances.Singleton;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.util.collections.ArrayUtil;
import stencil.util.collections.ListSet;

import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;

public class Legend extends Guide2D {
	public static final String GEOM_TAG = "#geom";
	protected static final String LABEL_PROPERTY_TAG = "label";
	protected static final String SPACING_TAG = "spacing";
	protected static final String EXAMPLE_WIDTH_TAG = "exampleWidth";

	public static final Specializer DEFAULT_SPECIALIZER;
	protected static final String defaultValues = "[label.FONT: 4, label.COLOR: \"BLACK\", spacing: .25, exampleWidth: 10]";
	protected static final String[] SPEC_NON_VALUES = new String[]{SPACING_TAG, EXAMPLE_WIDTH_TAG};	//Things that will be deleted from the specializer when constructing the default values
	static {
		try {DEFAULT_SPECIALIZER = ParseStencil.specializer(defaultValues);}
		 catch (Exception e) {throw new Error("Error parsing Lend configuration.", e);}
	}
	
	private final float spacing;	
	private final boolean autoPlace;
	private boolean lineGuide = false;
	
	private final TupleSorter sorter;
	private final int label_idx;
	private final String guideLabel;
	
	private CompoundTable data;
	private final CompoundRenderer renderer;
	private final PrototypedTuple updateMask;
	private final double exampleWidth;
	
	private double x,y;
	
	public Legend(Guide guideDef, int idx) {
		super(guideDef);

		Specializer spec = guideDef.specializer();
		autoPlace = !(spec.containsKey(Renderer.X.name()) || spec.containsKey(Renderer.Y.name()));
		guideLabel = Converter.toString(spec.get(GUIDE_LABEL));
		spacing = Converter.toFloat(spec.get(SPACING_TAG));
		exampleWidth = Converter.toFloat(spec.get(EXAMPLE_WIDTH_TAG));
		
		if (!autoPlace) {
			x = spec.containsKey("X") ? Converter.toDouble(spec.get("X")) : 0;
			y = spec.containsKey("Y") ? Converter.toDouble(spec.get("Y")) : 0;
		}
		
		TuplePrototype p = guideDef.rule().prototype();
		label_idx = ArrayUtil.indexOf("Input", TuplePrototypes.getNames(p));
		assert label_idx >=0 : "Input field not found for labeling in results prototype";
		sorter = new TupleSorter(label_idx);

		data = makeTable();
		PrototypedTuple defaultValues = Tuples.delete(DEFAULT_SPECIALIZER, SPEC_NON_VALUES); 
		updateMask = Tuples.merge(data.updateMaskTuple(), defaultValues);		
		renderer = new CompoundRenderer(data.prototype());
		
	}
	
	/**Creates the data tables for the guide.
	 * These will be empty and conform to schema elements defined in the guide def.
	 * **/
	private CompoundTable makeTable() {
		String identifier = guideDef.identifier();
		String exampleType = Converter.toString(guideDef.specializer().get(GEOM_TAG));
		
		if (exampleType.equals("ARC") || exampleType.startsWith("POLY")) {exampleType = "LINE"; lineGuide = true;}
		else {lineGuide = false;}

		Table labels = LayerTypeRegistry.makeTable(LABEL_PROPERTY_TAG, "TEXT");
		Table elements = LayerTypeRegistry.makeTable(GUIDE_ELEMENT_TAG , exampleType);
		return new CompoundTable(identifier, labels, elements);
	}

	public synchronized void setElements(List<PrototypedTuple> elements, Rectangle2D parentBounds) {
		if (autoPlace) {
			x = parentBounds.getMaxX();
			y = -parentBounds.getMinY();
		}
				
		data = makeTable();

		Collections.sort(elements, sorter);
		
		Collection<PrototypedTuple> items = createLabeledItems(elements,x,y); 
		for (PrototypedTuple item:items) {data.update(item);}
		Table.Util.genChange(data, renderer);
		
		data.update(createGuideLabel(data.getBoundsReference()));
		Table.Util.genChange(data, renderer);
	}
	


	private static final String[] LABEL_FIELDS= new String[]{"label.X","label.Y","label.TEXT", "label.REGISTRATION", "label.ID", "ID"};
	private static final PrototypedTuple NO_ELEMENT = Singleton.from("ele", new ListSet());
	private PrototypedTuple createGuideLabel(Rectangle2D bounds) {
		Object[] labelValues = new Object[]{bounds.getCenterX(), -bounds.getMinY(), guideLabel, "BOTTOM", -1, -1};
		PrototypedTuple t = new PrototypedArrayTuple(LABEL_FIELDS, labelValues);
		t = Tuples.merge(updateMask, t);
		t = Tuples.restructure(t, GUIDE_ELEMENT_TAG, LABEL_PROPERTY_TAG);
		t = Tuples.merge(t, NO_ELEMENT);
		return t;
	}

	
	private Collection<PrototypedTuple> createLabeledItems(List<PrototypedTuple> elements, double x, double y) {
		Collection<PrototypedTuple> marks = new ArrayList(elements.size());
		for (int i=0; i< elements.size(); i++) {
			PrototypedTuple t = elements.get(i);
			t = createLabeledItem(i+1, t,x ,y);	//i+1 because of the guide label	
			t = Tuples.merge(updateMask, t);
			t = Tuples.restructure(t, GUIDE_ELEMENT_TAG, LABEL_PROPERTY_TAG);
			marks.add(t);
		}
		
		return marks;
	}
	
	
	// TODO: Move the layout/registration to guide rules		
	private static final float exampleHeight =4f;
	private static final float vSpacing = 3f;
	private static final float hSpacing = 2f;
	private PrototypedTuple createLabeledItem(int idx, PrototypedTuple contents, double x, double y) {
		double indexOffset = y-(idx * exampleHeight) - (idx * vSpacing);  

		
		String[] exampleFields;
		Object[] exampleValues;
		if (!lineGuide) {
			exampleFields = new String[]{"ele.X", "ele.Y", "ele.REGISTRATION", "ele.ID"};
			exampleValues = new Object[]{x+hSpacing, indexOffset, "LEFT", idx};
		} else {
			exampleFields = new String[]{"ele.X1", "ele.Y1", "ele.X2", "ele.Y2", "ele.ID"};
			exampleValues = new Object[]{x+hSpacing, indexOffset, x+exampleWidth, indexOffset, idx};			
		}
		PrototypedTuple example = new PrototypedArrayTuple(exampleFields, exampleValues);

		String[] labelFields = new String[]{"label.X","label.Y","label.TEXT", "label.REGISTRATION", "label.ID", "ID"};
		Object[] labelValues = new Object[]{x+exampleWidth+hSpacing, indexOffset, contents.get(label_idx), "LEFT", idx, idx};
		PrototypedTuple label = new PrototypedArrayTuple(labelFields, labelValues);
		

		
		return Tuples.merge(label,example, Tuples.delete(contents, label_idx));
	}	

	public Rectangle2D getBoundsReference() {return data.getBoundsReference();}
	
	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		renderer.render(data.tenured(), g, viewTransform);
	}
}
