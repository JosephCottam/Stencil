package stencil.adapters.java2D.render.guides;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.adapters.java2D.columnStore.CompoundTable;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.CompoundRenderer;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Specializer;
import stencil.parser.ParseStencil;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;
import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;
import static stencil.parser.ParserConstants.GUIDE_LABEL;

//TODO: Pull more of this out to actual stencil code (like the calculation of tick/label positions and setting default values)
public class Axis extends Guide2D  {
	public static enum AXIS {X,Y}

	private static final String TICK_SIZE_KEY = "tickSize";	//TODO: Move into being just another part of the default rules 
	private static final String TEXT_OFFSET_KEY = "textOffset";	
	private static final String GUIDE_LABEL_GAP_KEY = "guideLabel.Gap";
	private static final String GUIDE_LABEL_SIZE_KEY = "guideLabel.Size";
	
	private static final String DEFAULT_SPECIALIZER_SOURCE = "[label.FONT: 4, label.COLOR: \"BLACK\", tick.PEN: .4, tick.PEN_COLOR: \"GRAY60\", textOffset: 1, tickSize: .75, guideLabel.Gap:2, guideLabel.Size:1.25]";
	private static final String[] DEFAULTS_KNOCKOUT = new String[]{"guideLabel.Gap", "guideLabel.Size","tickSize","textOffset"};
	
	public static final Specializer DEFAULT_SPECIALIZER;
	static {
		try {
			DEFAULT_SPECIALIZER = ParseStencil.specializer(DEFAULT_SPECIALIZER_SOURCE);
		}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}	
	protected final int labelIdx;
	protected final int offsetIdx;
	
	private CompoundTable data;
	private final CompoundRenderer renderer;
	private final PrototypedTuple updateMask;
	
	protected final AXIS axis;
	
	/**True -- Ensure that the axis touches the origin.
	 * False - Only display for the range of values provided.*/
	protected boolean connect = false;
	
	/**Where should the axis be rendered?*/
	private Double baseline = null;
	
	/**What is the specified position (influence the baseline).
	 * null means auto position
	 * Any other value is the literal location of the axis line.*/
	private final Double position;

	private final PrototypedTuple axisLabel;
	
	public Axis(Guide guideDef) {
		super(guideDef);
		Specializer spec = guideDef.specializer();
		
		data = makeTables(guideDef);
		updateMask = Tuples.merge(data.updateMaskTuple(), Tuples.delete(DEFAULT_SPECIALIZER, DEFAULTS_KNOCKOUT));		
		renderer = new CompoundRenderer(data.prototype());
		
		//Which axis is this?
		final String axisTag = guideDef.identifier().substring(guideDef.identifier().indexOf(":")+2); 
		axis = AXIS.valueOf(axisTag);
		
		//How will basic info show up?
		TuplePrototype p = guideDef.rule().prototype();
		labelIdx = ArrayUtil.indexOf("Input", TuplePrototypes.getNames(p));
		offsetIdx = ArrayUtil.indexOf(GUIDE_ELEMENT_TAG + NAME_SEPARATOR + axisTag, TuplePrototypes.getNames(p));

		//Update the schema per the guide def...
		
		//Get position info based on axis orientation
		if (axis == AXIS.X) {position = (spec.containsKey("Y") ? Converter.toDouble(spec.get("Y")) : null);}
		else if (axis == AXIS.Y) {position = (spec.containsKey("X") ? Converter.toDouble(spec.get("X")) : null);}
		else {position = null;}
		baseline = position;
		
		
		String label = (String) spec.get(GUIDE_LABEL);
		double rotation = axis==AXIS.X ? 0 : Math.toRadians(270);		
		String registration = axis==AXIS.X ? "TOP" : "BOTTOM";
		
		float guideLabelSize = Converter.toFloat(spec.get(GUIDE_LABEL_SIZE_KEY));
		Font font = ((Font) Converter.convert(spec.get("label.FONT"), Font.class));
		font = font.deriveFont(font.getSize2D() * guideLabelSize);
		
		String[] axisLabelFields = new String[]{"label.TEXT", "label.REGISTRATION", "label.FONT", "label.ROTATION", "label.ID"};
		axisLabel = new PrototypedArrayTuple(axisLabelFields, new Object[]{label, registration, font, rotation, -1d});	
	}

	private static CompoundTable makeTables(Guide guideDef) {
		String identifier = guideDef.identifier();

		Table labels = LayerTypeRegistry.makeTable("label", "TEXT");
		Table ticks = LayerTypeRegistry.makeTable("tick", "LINE");
				
		return new CompoundTable(identifier, labels, ticks);
	}
	
	
	public void render(Graphics2D g, AffineTransform viewTransform) {renderer.render(data.tenured(), g, viewTransform);}
	
	@Override
	public void setElements(List<PrototypedTuple> elements, Rectangle2D parentBounds) {
		data = makeTables(guideDef);		//Clear the old stuff
		if (elements.size() ==0) {return;}
		
		if (position == null) {
			if (axis == AXIS.X) {baseline = -parentBounds.getMaxY();}
			if (axis == AXIS.Y) {baseline = parentBounds.getMinX();}
		} 
		
		//Reconstruct and store the updates
		List<PrototypedTuple> tickUpdates = labeledTicks(elements); 
		for (PrototypedTuple t: tickUpdates) {addToData(t);}
		
		//Commit the updates
		Table.Util.genChange(data, renderer);

		Rectangle2D b = ShapeUtils.union(data.getBoundsReference(), parentBounds);
		PrototypedTuple axisLine = axisLine(b);
		addToData(axisLine);
		Table.Util.genChange(data, renderer);
	}

	private void addToData(PrototypedTuple t) {
		t = Tuples.merge(updateMask, t);
		t = Tuples.restructure(t,"tick", "label");
		data.update(t);
	}
	

	private List<PrototypedTuple> labeledTicks(List<PrototypedTuple> elements) {
		List<PrototypedTuple> updates = new ArrayList(elements.size());
		String axisTag = guideDef.selectors().keySet().iterator().next();
		int label_idx = elements.get(0).prototype().indexOf("Input");
		int offset_idx = elements.get(0).prototype().indexOf(GUIDE_ELEMENT_TAG + NAME_SEPARATOR + axisTag);

		double tickSize = Converter.toDouble(guideDef.specializer().get(TICK_SIZE_KEY));
		double textOffset = Converter.toDouble(guideDef.specializer().get(TEXT_OFFSET_KEY));

		double idCounter=1;	//Start at 1 so the axis line gets 0
		for (PrototypedTuple t: elements) {
			String labelText = Converter.toString(t.get(label_idx));
			double location = Converter.toDouble(t.get(offset_idx));
			
			PrototypedTuple tickParts = makeTick(location, tickSize, idCounter++);
			PrototypedTuple labelParts = makeLabel(labelText, location, textOffset, idCounter++);

			PrototypedTuple merged = Tuples.delete(t, label_idx, offset_idx);
			merged = Tuples.merge(merged, tickParts);
			merged = Tuples.merge(merged, labelParts);
			
			updates.add(merged);
		}
		
		return updates;
	}
	
	private static final String[] TICK_FIELDS = new String[]{"tick.X1", "tick.Y1","tick.X2","tick.Y2", "tick.ID", "ID"};
	public PrototypedTuple makeTick(double offset, double tickSize, double id) {
		Object[] values = new Object[6];
		values[values.length-1] = id;	//Once for the tick, once for the whole update
		values[values.length-2] = id;
		
		if (axis == AXIS.X) {
			values[0] = offset;
			values[1] = (baseline - tickSize);
			values[2] = offset;
			values[3] = (baseline + tickSize);
		} else {
			values[0] = baseline - tickSize;
			values[1] = offset;
			values[2] = baseline + tickSize;
			values[3] = offset;
		}
		return new PrototypedArrayTuple(TICK_FIELDS, values);
	}
	
	/**Make a tick-mark label.*/
	private static final String[] LABEL_FIELDS = new String[]{"label.TEXT", "label.X","label.Y", "label.ROTATION", "label.REGISTRATION", "label.ID"};
	private PrototypedTuple makeLabel(String text, double offset, double textOffset, double id) {
		Object[] values = new Object[6];

		values[values.length-1] = id;
		values[0] = text;
		
		if (axis == AXIS.X) {
			values[1] = offset;
			values[2] = (baseline - textOffset);
			values[3] = Math.toRadians(90);
			values[4] = "LEFT";
		} else {
			values[1] = baseline - textOffset;
			values[2] = offset;
			values[3] = 0;
			values[4] = "RIGHT";
		}
		
		return new PrototypedArrayTuple(LABEL_FIELDS, values);
	}
	
	private static final String[] SPINE_FIELDS =  new String[]{"tick.X1", "tick.Y1", "tick.X2", "tick.Y2", "label.X", "label.Y", "ID", "tick.ID"};
	private PrototypedTuple axisLine(Rectangle2D bounds) {
		double guideLabelGap = Converter.toDouble(guideDef.specializer().get(GUIDE_LABEL_GAP_KEY));
		Object[] values = new Object[SPINE_FIELDS.length];
		values[SPINE_FIELDS.length-1] = -1d;
		values[SPINE_FIELDS.length-2] = -1d;

		if (axis == AXIS.X) {
			values[0] = bounds.getMinX();		//Line x1
			values[1] = baseline;
			values[2] = bounds.getMaxX();		//line x2
			values[3] = baseline;
			values[4] = bounds.getCenterX();	//Label X
			values[5] = -bounds.getMaxY() - guideLabelGap;	//Label Y
		} else {
			values[0] = baseline;
			values[1] = -bounds.getMinY();
			values[2] = baseline;
			values[3] = -bounds.getMaxY();
			values[4] = bounds.getMinX() - guideLabelGap;
			values[5] = -bounds.getCenterY();
		}
		
		PrototypedTuple t = new PrototypedArrayTuple(SPINE_FIELDS, values);
		t=Tuples.merge(axisLabel, t);
		return t;
	}

	@Override
	public Rectangle2D getBoundsReference() {return data.getBoundsReference();}	
}
