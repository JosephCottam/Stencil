package stencil.adapters.java2D.data.guides;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.adapters.java2D.data.glyphs.*;

import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;

import static stencil.parser.ParserConstants.GUIDE_LABEL;

public class Axis extends Guide2D {
	/**Fields used in creating marks.*/
	private final String[] LABEL_FIELDS =  new String[]{"TEXT", "X","Y", "ROTATION"};
	private final String[] LINE_FIELDS = new String[]{"X.1", "Y.1","X.2","Y.2"};

	public static final String AXIS_LABEL_PROPERTY= GUIDE_LABEL;
	
	/**In the axial specializer, map values that start with
	 * this tag will be applied to the labels.
	 */
	public static final String LABEL_PROPERTY_TAG = "label";
	
	/**In the axial specializer, map values that start with
	 * this tag will be applied to the labels.
	 */
	public static final String LINE_PROPERTY_TAG = "line";

	private static final String defaultArguments = "[sample: \"CATEGORICAL\", position: NULL, label.FONT: 4, label.COLOR: \"BLACK\", line.PEN: .4, line.PEN_COLOR: \"GRAY60\", textOffset: 1, tickSize: .75, connect: \"FALSE\"]";
	public static final Specializer DEFAULT_ARGUMENTS;
	static {
		try {DEFAULT_ARGUMENTS = ParseStencil.specializer(defaultArguments);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}

	public static final String IMPLANTATION_NAME  = "AXIS";
	public static enum AXIS {X,Y}

	/**How far from the axis should text be placed?*/
	public float textOffset;
	
	/**How tall should the ticks be?*/
	public float tickSize;

	/**True -- Ensure that the axis touches the origin.
	 * False - Only display for the range of values provided.*/
	public boolean connect;
	
	/**Where should the axis be rendered?*/
	private Double baseline = null;
	
	/**What is the specified position (influence the baseline).
	 * null means auto position
	 * Any other value is the literal location of the axis line.*/
	private Double position = null;
	
	protected final AXIS axis;
	
	protected Glyph2D prototypeText = new Text(null, "prototype");
	protected Glyph2D prototypeLine = new Line(null, "prototype");

	protected final ArrayList<Glyph2D> marks  = new ArrayList();

	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	protected final int label_idx;
	protected final int offset_idx;
	protected final TupleSorter sorter;
	
	protected Text guideLabel;
	
	/**@param Which axis should this go on (valid values are X and Y)*/
	public Axis(Guide guideDef) {
		super(guideDef);
		axis = AXIS.valueOf(guideDef.selector().attribute().substring(0,1));//Only the first character matters because of XS,YS, etc
		Specializer spec = guideDef.specializer();
		
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(spec, this);

		//Apply default registration based on axis orientation
		String registration = axis==AXIS.X ? "LEFT" : "RIGHT";
		Tuple update = new PrototypedTuple(new String[]{"REGISTRATION"}, new Object[]{registration});
		prototypeText = prototypeText.update(update);
		
		
		//Get position info based on axis orientation
		if (axis == AXIS.X) {position = (spec.containsKey("Y") ? Converter.toDouble(spec.get("Y")) : null);}
		if (axis == AXIS.Y) {position = (spec.containsKey("X") ? Converter.toDouble(spec.get("X")) : null);}
		
		prototypeText = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeText);
		prototypeText = GuideUtils.applyDefaults(spec, LABEL_PROPERTY_TAG, prototypeText);

		prototypeLine = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LINE_PROPERTY_TAG, prototypeLine);
		prototypeLine = GuideUtils.applyDefaults(spec, LINE_PROPERTY_TAG, prototypeLine);
		
		TuplePrototype p = guideDef.resultsPrototype();
		label_idx = ArrayUtil.indexOf("Input", TuplePrototypes.getNames(p));
		offset_idx = ArrayUtil.indexOf("Output", TuplePrototypes.getNames(p));
		
		assert label_idx >=0 : "Input field not found for labeling in results prototype";
		assert offset_idx >=0 : "Output field not found for labeling in results prototype";
		
		sorter = new TupleSorter(offset_idx);

		guideLabel = (Text) prototypeText.updateID("label");
		if (spec.containsKey(AXIS_LABEL_PROPERTY)) {
			String label = (String) spec.get(AXIS_LABEL_PROPERTY);
			int rotation = axis==AXIS.X ? 0 : 270;
			registration = axis==AXIS.X ? "TOP" : "BOTTOM";
			Font font = (Font) guideLabel.get("FONT");
			font = font.deriveFont(font.getSize2D()*1.2f);

			update = new PrototypedTuple(new String[]{"TEXT", "REGISTRATION", "FONT", "ROTATION"}, new Object[]{label, registration, font, rotation}); 
			
			guideLabel = guideLabel.update(update);
		}

		if (position != null) {baseline = position;}
	}
	
	public void setConnect(boolean connect) {this.connect = connect;}

	public AXIS getAxis() {return axis;} 
	
	/**
	 * @param elements The values to be represented on the guide.   
	 * 		           Each tuple must be at least a pair with input and output.
	 *                 Tuples MAY contain other formatting information 
	 * @param targetBounds the bounds of the layer the guide is applied to
	 */
	public synchronized void setElements(List<Tuple> elements, Rectangle2D targetBounds) {
		marks.clear();
		
		if (position == null) {
			if (axis == AXIS.X) {baseline = targetBounds.getMaxY();}
			if (axis == AXIS.Y) {baseline = targetBounds.getMinX();}
		} 
		
		if (elements.size() > 0) {
			createLabeledTics(elements);
			Glyph2D line = createLine(elements);
			if (line !=null) {marks.add(line);}	
		}
		
		bounds = GuideUtils.fullBounds(marks);
		
		Tuple update;
		double x,y;
		if (axis == AXIS.X) {
			x = bounds.getCenterX();
			y = bounds.getMaxY() + 2;
		} else {
			x = bounds.getMinX() - 2;
			y = bounds.getCenterY();
		}

		update = new PrototypedTuple(new String[]{"X", "Y"},  new Object[]{x,y});
		guideLabel = guideLabel.update(update);
		marks.add(guideLabel);
		bounds = GuideUtils.fullBounds(marks);		
	}

	public Rectangle2D getBoundsReference() {return bounds;}
	
	/**Create the major axial line for the axis.*/
	private Glyph2D createLine(List<Tuple> elements) {
		if (stencil.types.color.ColorCache.isTransparent((java.awt.Color) prototypeLine.get("PEN_COLOR"))) {return null;}

		Collections.sort(elements, sorter);
		
		Double[] values = new Double[LINE_FIELDS.length];
				
		double min = Converter.toDouble(elements.get(0).get(offset_idx));
		double max = Converter.toDouble(elements.get(elements.size()-1).get(offset_idx)); 
				
		if (axis == AXIS.X) {
			values[0]  = min;
			values[1] = baseline;
			values[2] = max;
			values[3] = baseline;
		} else {
			values[0] = baseline;
			values[1] = min;
			values[2] = baseline;
			values[3] = max;
		}
		return prototypeLine.update(new PrototypedTuple(LINE_FIELDS, values));
	}

	/**Create the labeled tick marks around the major axis line.*/
	private Collection<Glyph2D> createLabeledTics(List<Tuple> elements) {
		marks.ensureCapacity(elements.size() *2);
		
		for (Tuple t: elements) {
			String labelText = Converter.toString(t.get(label_idx));
			double location = Converter.toDouble(t.get(offset_idx));

			Glyph2D tick = makeTick(location, Tuples.sift("line.", t));
			if (tick != null) {marks.add(tick);}
			Glyph2D label = makeLabel(labelText, location, Tuples.sift("label.", t));
			if (label !=null) {marks.add(label);}
		}
		return marks;
	}
	
	/**Make a tick-mark label.*/
	private Glyph2D makeLabel(String text, double value, Tuple formatting) {
		if (stencil.types.color.ColorCache.isTransparent((java.awt.Color) prototypeText.get("COLOR"))) {return null;}

		Object[] values = new Object[LABEL_FIELDS.length];

		values[0] = text;
		
		if (axis == AXIS.X) {
			values[1] = value;
			values[2] = baseline + textOffset;
			values[3] =	90;
		} else {
			values[1] = baseline - textOffset;
			values[2] = value;
			values[3] =0;
		}
		
		Glyph2D result = prototypeText.update(new PrototypedTuple(LABEL_FIELDS, values));
		result = result.update(formatting);
		return result;
	}
	
	/**Make an actual tick mark.*/
	private Glyph2D makeTick(double offset, Tuple formatting) {
		if (stencil.types.color.ColorCache.isTransparent((java.awt.Color) prototypeLine.get("PEN_COLOR"))) {return null;}
	
		String[] fields = new String[]{"X.1", "Y.1","X.2","Y.2"};
		Double[] values = new Double[fields.length];
		
		if (axis == AXIS.X) {
			values[0] = offset;
			values[1] = baseline - tickSize;
			values[2] = offset;
			values[3] = baseline + tickSize;
		} else {
			values[0] = baseline - tickSize;
			values[1] = offset;
			values[2] = baseline + tickSize;
			values[3] = offset;
		}

		Glyph2D result = prototypeLine.update(new PrototypedTuple(fields, values));
		result = result.update(formatting);
		return result;
	}
	
	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		for (Glyph2D glyph: marks) {glyph.render(g, viewTransform);}
	}
}
