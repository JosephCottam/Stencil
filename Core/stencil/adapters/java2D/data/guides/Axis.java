package stencil.adapters.java2D.data.guides;

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

import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Guide;
import stencil.parser.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleSorter;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;

public class Axis implements Guide2D {
	/**Fields used in creating marks.*/
	private final String[] LABEL_FIELDS =  new String[]{"TEXT", "X","Y", "ROTATION"};
	private final String[] LINE_FIELDS = new String[]{"X.1", "Y.1","X.2","Y.2"};

	/**In the axial specializer, map values that start with
	 * this tag will be applied to the labels.
	 */
	public static final String LABEL_PROPERTY_TAG = "label";
	
	/**In the axial specializer, map values that start with
	 * this tag will be applied to the labels.
	 */
	public static final String LINE_PROPERTY_TAG = "line";

	private static final String defaultArguments = "[sample=\"CATEGORICAL\", label.FONT_SIZE=4, label.FONT_COLOR=\"BLACK\", line.STROKE_WEIGHT=.1, line.STROKE_COLOR=\"GRAY\", textOffset=1, tickSize=.75, tickCount=10, axisOffset=0, connect=\"FALSE\"]";
	public static final Specializer DEFAULT_ARGUMENTS;
	static {
		try {DEFAULT_ARGUMENTS = ParseStencil.parseSpecializer(defaultArguments);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}

	public static final String IMPLANTATION_NAME  = "AXIS";
	public static enum AXIS {X,Y}
	
	/**How far from the axis specified should the actual axis line be placed.*/
	public double axisOffset;
	
	/**How far from the axis should text be placed?*/
	public float textOffset;
	
	/**How tall should the ticks be?*/
	public float tickSize;

	/**True -- Ensure that the axis touches the origin.
	 * False - Only display for the range of values provided.*/
	public boolean connect;
	
	/**Indicates how many ticks are desired on the axis.
	 * This property is not used by the axis itself, but is 
	 * rather used by continuous guide ops to influence the sample points.
	 * 
	 * This value is only a suggestion.  It will be approximately the number
	 * of tick marks used, but the exact number is determined by the continuous guide
	 * operator and may take into account the strategy and the range.
	 * 
	 * */
	public float tickCount;
	
	protected AXIS axis;
	
	protected Glyph2D prototypeText = new Text(null, "prototype");
	protected Glyph2D prototypeLine = new Line(null, "prototype");

	protected final ArrayList<Glyph2D> marks  = new ArrayList();

	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	protected final int label_idx;
	protected final int offset_idx;
	protected final TupleSorter sorter;
	
	/**@param Which axis should this go on (valid values are X and Y)*/
	public Axis(Guide guideDef) {		
		axis = AXIS.valueOf(guideDef.getAttribute());
		
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(guideDef.getSpecializer(), this);
		
		prototypeText = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeText);
		prototypeText = GuideUtils.applyDefaults(guideDef.getSpecializer(), LABEL_PROPERTY_TAG, prototypeText);

		prototypeLine = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LINE_PROPERTY_TAG, prototypeLine);
		prototypeLine = GuideUtils.applyDefaults(guideDef.getSpecializer(), LINE_PROPERTY_TAG, prototypeLine);
		
		TuplePrototype p = guideDef.getPrototype();		//Get input prototype
		label_idx = ArrayUtil.indexOf("Input", TuplePrototypes.getNames(p));
		offset_idx = ArrayUtil.indexOf("Output", TuplePrototypes.getNames(p));
		
		assert label_idx >=0 : "Input field not found for labeling in results prototype";
		assert offset_idx >=0 : "Output field not found for labeling in results prototype";
		
		sorter = new TupleSorter(offset_idx);
	}
	
	public void setConnect(boolean connect) {this.connect = connect;}

	public AXIS getAxis() {return axis;} 
	
	public synchronized void setElements(List<Tuple> elements) {
		marks.clear();
		
		if (elements.size() > 0) {
			createLabeledTics(elements);
			Glyph2D line = createLine(elements);
			if (line !=null) {marks.add(line);}	
		} 
		bounds = GuideUtils.fullBounds(marks);		
	}

	public Rectangle2D getBoundsReference() {return bounds;}
	
	/**Create the major axial line for the axis.*/
	private Glyph2D createLine(List<Tuple> elements) {
		if (stencil.types.color.ColorCache.isTransparent((java.awt.Color) prototypeLine.get("STROKE_COLOR"))) {return null;}

		Collections.sort(elements, sorter);
		
		Double[] values = new Double[LINE_FIELDS.length];
				
		double min = Converter.toDouble(elements.get(0).get(offset_idx));
		double max = Converter.toDouble(elements.get(elements.size()-1).get(offset_idx)); 
		
		if (max >0 && min <0) {/*do nothing, doesn't matter what connect is set to if the origin is bridged.*/}
		
		if (connect && (min >0)) {min =0;}
		else if (connect && (max <0)) {max =0;}
		
		if (axis == AXIS.X) {
			values[0]  = min;
			values[1] = axisOffset;
			values[2] = max;
			values[3] = axisOffset;
		} else {
			values[0] = axisOffset;
			values[1] = min;
			values[2] = axisOffset;
			values[3] = max;
		}
		return prototypeLine.update(new PrototypedTuple(LINE_FIELDS, values));
	}

	/**Create the labeled tick marks around the major axis line.*/
	private Collection<Glyph2D> createLabeledTics(List<Tuple> elements) {
		marks.ensureCapacity(elements.size() *2);
		
		for (Tuple t: elements) {
			if (t == null) {continue;}	//TODO: HACK!!!! This is because the guide creation is not properly scheduled right now

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
		if (stencil.types.color.ColorCache.isTransparent((java.awt.Color) prototypeText.get("FONT_COLOR"))) {return null;}

		Object[] values = new Object[LABEL_FIELDS.length];

		values[0] = text;
		
		if (axis == AXIS.X) {
			values[1] = value;
			values[2] = axisOffset + textOffset;
			values[3] =	90;
		} else {
			values[1] = axisOffset - textOffset;
			values[2] = value;
			values[3] =0;
		}
		
		Glyph2D result = prototypeText.update(new PrototypedTuple(LABEL_FIELDS, values));
		result = result.update(formatting);
		return result;
	}
	
	/**Make an actual tick mark.*/
	private Glyph2D makeTick(double offset, Tuple formatting) {
		if (stencil.types.color.ColorCache.isTransparent((java.awt.Color) prototypeLine.get("STROKE_COLOR"))) {return null;}
	
		String[] fields = new String[]{"X.1", "Y.1","X.2","Y.2"};
		Double[] values = new Double[fields.length];
		
		if (axis == AXIS.X) {
			values[0] = offset;
			values[1] = axisOffset - tickSize;
			values[2] = offset;
			values[3] = axisOffset+tickSize;
		} else {
			values[0] = axisOffset - tickSize;
			values[1] = offset;
			values[2] = axisOffset+tickSize;
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
