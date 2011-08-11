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
import stencil.interpreter.Interpreter;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Rule;
import stencil.interpreter.tree.Specializer;
import stencil.parser.ParseStencil;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.instances.Singleton;
import stencil.types.Converter;
import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;
import static stencil.parser.ParserConstants.GUIDE_LABEL;
import static stencil.parser.ParserConstants.INPUT_FIELD;

//TODO: Pull more of this out to actual stencil code (like the calculation of tick/label positions and setting default values)
public class Axis extends Guide2D  {
	public static enum AXIS {X,Y}
	
	/**Different ways positioning can be handled:
	 * 		LOW: Lowest edge of the bounds (bottom or left)
	 *  	HIGH: Highest edge of the bounds (top or right)
	 *  	LEAST: Least of lowest  bound or Zero-point on alternate axis
	 *      MOST:  Greater of highest edge or zero-point on alternate axis
	 *      ZERO: Zero point on the alternate axis
	 *      AUTO: Determine which above
	 *      MANUAL: An exact value was directly specified
	 * Except for MANUAL, these are selected by name in the specializer.  
	 * MANUAL is inferred when the value in the specializer is a number. 
	 */
	public static enum POSITION {LOW, HIGH, LEAST, MOST, ZERO, AUTO, MANUAL}
	

	private static final String TICK_SIZE_KEY = "tickSize";	//TODO: Move into being just another part of the default rules 
	private static final String TEXT_OFFSET_KEY = "textOffset";	
	private static final String GUIDE_LABEL_GAP_KEY = "guideLabel.Gap";
	private static final String GUIDE_LABEL_SIZE_KEY = "guideLabel.Size";
	private static final String BASELINE_KEY = "baseline";
	private static final String IMPLANT_KEY = "implant";
	
	private static final String DEFAULT_SPECIALIZER_SOURCE = "[label.FONT: 4, label.COLOR: \"BLACK\", tick.PEN: .4, tick.PEN_COLOR: \"GRAY60\", textOffset: 1, tickSize: .75, guideLabel.Gap:2, guideLabel.Size:1.25, baseline: \"AUTO\"]";
	private static final String[] DEFAULTS_KNOCKOUT = new String[]{GUIDE_LABEL_GAP_KEY, GUIDE_LABEL_SIZE_KEY,"tickSize","textOffset", BASELINE_KEY};
	
	public static final Specializer DEFAULT_SPECIALIZER;
	static {
		try {
			DEFAULT_SPECIALIZER = ParseStencil.specializer(DEFAULT_SPECIALIZER_SOURCE);
		}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}	
	
	private CompoundTable data;
	private final CompoundRenderer renderer;
	private final PrototypedTuple updateMask;
	
	protected final AXIS axis;
	
	/**True -- Ensure that the axis touches the origin.
	 * False - Only display for the range of values provided.*/
	protected boolean connect = false;
	
	/**How is baseline positioning handled?*/
	private final POSITION position;

	/**Where should the axis be rendered?*/
	private Double baseline = null;
	
	private final PrototypedTuple axisLabel;
	private final Rule alterZero;	//Call chain to get the zero-position on the alter axis
	
	public Axis(Guide guideDef, Rule alterZero) {
		super(guideDef);
		Specializer spec = guideDef.specializer();
		this.alterZero = alterZero;
		
		data = makeTables(guideDef);
		PrototypedTuple updateMask = Tuples.merge(data.updateMaskTuple(), Tuples.delete(DEFAULT_SPECIALIZER, DEFAULTS_KNOCKOUT));
		if (spec.containsKey(IMPLANT_KEY)) {
			updateMask = GuideUtil.fullImplant(updateMask, Converter.toString(spec.get(IMPLANT_KEY)));
		}
		this.updateMask = updateMask;
		
		renderer = new CompoundRenderer(data.prototype());
		
		//Which axis is this?
		final String axisTag = guideDef.identifier().substring(guideDef.identifier().indexOf(":")+2); 
		axis = AXIS.valueOf(axisTag);
		
		//Update the schema per the guide def...
		//Get position info based on axis orientation
		Object pos = spec.get(BASELINE_KEY);
		if (pos instanceof Number) {
			position = POSITION.MANUAL;
			baseline = ((Number) pos).doubleValue();
		} else {
			position = (POSITION) Converter.convert(pos, POSITION.AUTO.getClass(), POSITION.AUTO);
			//Baseline placement deferred until render time
		}
		
		
		String label = (String) spec.get(GUIDE_LABEL);
		double rotation = axis==AXIS.X ? 0 : Math.toRadians(270);		
		String registration = axis==AXIS.X ? "TOP" : "BOTTOM";
		
		float guideLabelSize = Converter.toFloat(spec.get(GUIDE_LABEL_SIZE_KEY));
		Font font = ((Font) Converter.convert(spec.get("label.FONT"), Font.class));
		font = font.deriveFont(font.getSize2D() * guideLabelSize);
		
		String[] axisLabelFields = new String[]{"label.TEXT", "label.REGISTRATION", "label.FONT", "label.ROTATION", "label.ID"};
		PrototypedTuple axisLabel = new PrototypedArrayTuple(axisLabelFields, new Object[]{label, registration, font, rotation, -1d});
		axisLabel = Tuples.merge(updateMask, axisLabel);
		axisLabel = Tuples.delete(axisLabel, "tick");
		this.axisLabel = axisLabel;
	}

	private static CompoundTable makeTables(Guide guideDef) {
		String identifier = guideDef.identifier();

		Table labels = LayerTypeRegistry.makeTable("label", "TEXT");
		Table ticks = LayerTypeRegistry.makeTable("tick", "LINE");
				
		return new CompoundTable(identifier, labels, ticks);
	}
	
	
	public void render(Graphics2D g, AffineTransform viewTransform) {renderer.render(data.tenured(), g, viewTransform);}
	
	@Override
	public void setElements(List<PrototypedTuple> elements, Rectangle2D parentBounds, AffineTransform viewTransform) {
		data = makeTables(guideDef);		//Clear the old stuff
		if (elements.size() ==0) {return;}
		
		baseline = baseline(position, axis, parentBounds, baseline);
		
		//Reconstruct and store the updates
		List<PrototypedTuple> tickUpdates = labeledTicks(elements); 
		for (PrototypedTuple t: tickUpdates) {addToData(t);}
		
		//Commit the updates
		Table.Util.genChange(data, renderer, viewTransform);

		Rectangle2D b = ShapeUtils.union(data.getBoundsReference(), parentBounds);
		PrototypedTuple axisLine = axisLine(b);
		addToData(axisLine);
		Table.Util.genChange(data, renderer, viewTransform);
	}

	private void addToData(PrototypedTuple t) {
		t = Tuples.merge(updateMask, t);
		t = Tuples.restructure(t,"tick", "label");
		data.update(t);
	}

	
	/**Where should the baseline be placed, returns value in y-up/positive convention**/
	private static final Tuple ZERO = Singleton.from(0d);
	private static final Tuple ONE = Singleton.from(1d);
	private double baseline(POSITION position, AXIS axis, Rectangle2D parentBounds, Double baseline) {
		
		Double zeroPoint;
		try {
			zeroPoint = Converter.toDouble(Interpreter.processTuple(ZERO, alterZero).get(0));	//TOOD: may not be .get(0), what is it in general?
			if (Double.isInfinite(zeroPoint)) {
				zeroPoint = Converter.toDouble(Interpreter.processTuple(ONE, alterZero).get(0));	//TOOD: may not be .get(0), what is it in general?
			}
		} catch (Exception e) {zeroPoint = null;}

		
		if (position == POSITION.AUTO) {
			if(zeroPoint == null) {position = POSITION.LEAST;}
			else {position = POSITION.ZERO;}
		}
		
		if (position == POSITION.LEAST && zeroPoint == null) {position = POSITION.LOW;}
		if (position == POSITION.MOST && zeroPoint == null) {position = POSITION.HIGH;}
		
		switch(position) {
		case MANUAL: return baseline;
		case ZERO:	
			if (zeroPoint == null) {return 0;}
			else {return zeroPoint;}
		case LOW:
			if (axis == AXIS.X) {return -parentBounds.getMaxY();}
			else {return parentBounds.getMinX();}
		case HIGH:
			if (axis == AXIS.X) {return -parentBounds.getMinY();}
			else {return parentBounds.getMaxX();}
		case LEAST:
			if (axis == AXIS.X) {return -Math.max(parentBounds.getMinY(), -zeroPoint);}
			else {return Math.min(parentBounds.getMaxX(), zeroPoint);}
		case MOST: 
			if (axis == AXIS.X) {return -Math.min(parentBounds.getMinY(), -zeroPoint);}
			else {return Math.max(parentBounds.getMaxX(), zeroPoint);}
		
		}
	
		throw new Error("Valid position passed, but not handled in baseline calculation:" + position);
	}
	

	private List<PrototypedTuple> labeledTicks(List<PrototypedTuple> elements) {
		List<PrototypedTuple> updates = new ArrayList(elements.size());
		String axisTag = guideDef.selectors().keySet().iterator().next();
		int label_idx = elements.get(0).prototype().indexOf(INPUT_FIELD);
		int offset_idx = elements.get(0).prototype().indexOf(GUIDE_ELEMENT_TAG + NAME_SEPARATOR + axisTag);

		double tickSize = Converter.toDouble(guideDef.specializer().get(TICK_SIZE_KEY));
		double textOffset = Converter.toDouble(guideDef.specializer().get(TEXT_OFFSET_KEY));

		double idCounter=1;	//Start at 1 so the axis line gets 0
		for (PrototypedTuple t: elements) {
			String labelText = Converter.toString(((Tuple) t.get(label_idx)).get(0));
			double location = Converter.toDouble(t.get(offset_idx));
			
			PrototypedTuple tickParts = makeTick(location, tickSize, idCounter++);
			PrototypedTuple labelParts = makeLabel(labelText, location, textOffset, idCounter++);

			PrototypedTuple merged = Tuples.delete(t, label_idx, offset_idx);
			merged = Tuples.mergeAll(tickParts, labelParts, merged);			
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
