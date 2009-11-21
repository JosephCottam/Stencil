package stencil.adapters.java2D.data.guides;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.adapters.java2D.data.glyphs.*;

import stencil.interpreter.Interpreter;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Guide;
import stencil.parser.tree.Rule;
import stencil.parser.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.util.AutoguidePair;

public class Axis implements Guide2D {
	/**In the axial specializer, map values that start with
	 * this tag will be applied to the labels.
	 */
	public static final String LABEL_PROPERTY_TAG = "label";
	
	/**In the axial specializer, map values that start with
	 * this tag will be applied to the labels.
	 */
	public static final String LINE_PROPERTY_TAG = "line";

	private static final String defaultArguments = "[sample=\"CATEGORICAL\", label.FONT_SIZE=1, label.FONT_COLOR=@color(BLACK), line.STROKE_WEIGHT=.1, line.STROKE_COLOR=@color(GRAY), textOffset=1, tickSize=.75, tickCount=10, axisOffset=0, connect=\"FALSE\"]";
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

	/**Valid samples are CATEGORICAL, LINEAR, LOG and EXP.
	 * 
	 * Sample is actually used by the guide operator, not by the guide itself.
	 * Using categorical ensures a categorical guide op.  All other values
	 * yield a continuous guide op and dictate the sample strategy.
	 **/
	public String sample;
	
	protected List<Rule> rules;
	
	protected AXIS axis;
	
	protected Glyph2D prototypeText = new Text(null, "prototype");
	protected Glyph2D prototypeLine = new Line(null, "prototype");

	protected Collection<Glyph2D> marks;

	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	/**@param Which axis should this go on (valid values are X and Y)*/
	public Axis(String id, Guide guideDef) {
		this.rules = guideDef.getRules();
		
		axis = AXIS.valueOf(id);
		
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(guideDef.getSpecializer(), this);
		
		prototypeText = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeText);
		prototypeText = GuideUtils.applyDefaults(guideDef.getSpecializer(), LABEL_PROPERTY_TAG, prototypeText);

		prototypeLine = GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LINE_PROPERTY_TAG, prototypeLine);
		prototypeLine = GuideUtils.applyDefaults(guideDef.getSpecializer(), LINE_PROPERTY_TAG, prototypeLine);
	}
	
	public void setConnect(boolean connect) {this.connect = connect;}

	public AXIS getAxis() {return axis;} 
	
	public synchronized void setElements(List<AutoguidePair> elements) {
		List<GuidePair<Float>> listing = validate(elements);		
		
		if (listing.size() > 0) {
			marks = createLabeledTics(listing);
			Glyph2D line = createLine(listing);
			if (line !=null) {marks.add(line);}	
			bounds = GuideUtils.fullBounds(marks);
		} else {
			marks = new ArrayList<Glyph2D>();
			bounds = null;
		}
	}

	public Rectangle2D getBoundsReference() {return bounds;}
	
	/**Create the major axial line for the axis.*/
	private Glyph2D createLine(List<GuidePair<Float>> elements) {
		if (stencil.types.color.Color.isTransparent((java.awt.Color) prototypeLine.get("STROKE_COLOR"))) {return null;}

		String[] fields = new String[]{"X.1", "Y.1","X.2","Y.2"};
		Double[] values = new Double[fields.length];
				
		double min = elements.get(0).output.doubleValue();
		double max = elements.get(elements.size()-1).output.doubleValue(); 
		
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
		return prototypeLine.update(new PrototypedTuple(fields, values));
	}

	/**Create the labeled tick marks around the major axis line.*/
	private Collection<Glyph2D> createLabeledTics(List<GuidePair<Float>> elements) {
		Collection<Glyph2D> marks = new ArrayList<Glyph2D>(elements.size() *2);//One for tick, one for label
		
		for (GuidePair<Float> p: elements) {
			double location = p.output.doubleValue();
			Tuple result;

			try {result = Interpreter.process(rules, p);}
			catch (Exception e) {throw new RuntimeException("Error updating guide with descriptor pair: " + p.toString(), e);}

			Glyph2D tick = makeTick(location, Tuples.sift("line.", result));
			if (tick != null) {marks.add(tick);}
			Glyph2D label = makeLabel(p.input, location, Tuples.sift("label.", result));
			if (label !=null) {marks.add(label);}
		}
		return marks;
	}
	
	/**Make a tick-mark label.*/
	private Glyph2D makeLabel(String text, double value, Tuple formatting) {
		if (stencil.types.color.Color.isTransparent((java.awt.Color) prototypeText.get("FONT_COLOR"))) {return null;}

		String[] fields =  new String[]{"TEXT", "X","Y", "ROTATION"};
		Object[] values = new Object[fields.length];

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
		
		Glyph2D result = prototypeText.update(new PrototypedTuple(fields, values));
		result = result.update(formatting);
		return result;
	}
	
	/**Make an actual tick mark.*/
	private Glyph2D makeTick(double offset, Tuple formatting) {
		if (stencil.types.color.Color.isTransparent((java.awt.Color) prototypeLine.get("STROKE_COLOR"))) {return null;}
	
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
	
	/**Verify that all Autoguide pairs have exactly one result, and that result
	 * is some type of Number.
	 * 
	 * @param elements
	 * @return
	 */
	private List<GuidePair<Float>> validate(Collection<AutoguidePair> elements) {
		List<GuidePair<Float>> pairs = new ArrayList<GuidePair<Float>>(elements.size());
		Comparator c = new Comparator<GuidePair<Float>>() {
			public int compare(GuidePair<Float> p1, GuidePair<Float> p2) { 
				Float n1 = p1.output;
				Float n2 = p2.output;
				
				if (n1> n2) {return 1;}
				if (n1 < n2) {return -1;}
				return 0;
			}
			
		};
				
		for (AutoguidePair p: elements) {
			if (p.getResult().length != 1 || !(p.getResult()[0] instanceof Number))  {
				throw new IllegalArgumentException(String.format("Attempting to us a non-numeric result in an %1$s-axis legend: %2$s", axis.toString(), Arrays.deepToString(p.getResult())));
			} 
			GuidePair<Float> pair = new GuidePair<Float>(p.getInput()[0].toString(), ((Number) p.getResult()[0]).floatValue());
			pairs.add(pair);
		}
		
		Collections.sort(pairs, c);
		return pairs;
	}

	
	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		if (marks == null) {return;}
		
		for (Glyph2D glyph: marks) {glyph.render(g, viewTransform);}
	}
}
