package stencil.adapters.piccoloDynamic.guides;

import java.util.*;

import stencil.adapters.piccoloDynamic.NodeTuple;
import stencil.adapters.piccoloDynamic.glyphs.Node;
import stencil.adapters.piccoloDynamic.glyphs.Line;
import stencil.adapters.piccoloDynamic.glyphs.Text;
import stencil.display.DisplayGuide;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.util.AutoguidePair;
import stencil.util.Tuples;

import edu.umd.cs.piccolo.PNode;


public class Axis extends Node implements DisplayGuide {
	private static final class Pair {
		String label;
		float value;
		boolean category;
		
		public Pair(Object label, float value) {
			category = !(label instanceof Number);
			this.label = label.toString();
			this.value = value;
		}
		
		public boolean category() {return category;}
		public String toString() {return "(" + label + ", " + value + ")";}
	}
	
	/**In the axial specializer, map values that start with
	 * this tag will be applied to the labels.
	 */
	public static final String LABEL_PROPERTY_TAG = "label";
	
	/**In the axial specializer, map values that start with
	 * this tag will be applied to the labels.
	 */
	public static final String LINE_PROPERTY_TAG = "line";

	private static final String defaultArguments = "[label.FONT_SIZE=1, label.FONT_COLOR=@color(BLACK), line.STROKE_WEIGHT=.1, line.STROKE_COLOR=@color(GRAY), textOffset=1, tickSize=.75, tickCount=10, axisOffset=0, connect=\"FALSE\"]";
	public static final Specializer DEFAULT_ARGUMENTS;
	static {
		try {DEFAULT_ARGUMENTS = ParseStencil.parseSpecializer(defaultArguments);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}

	public static final String IMPLANTATION_NAME  = "AXIS";
	public static enum AXIS {X,Y}
	
	/**How far from the axis specified should the actual axis line be placed.*/
	public float axisOffset;
	
	/**How far from the axis should text be placed?*/
	public float textOffset;
	
	/**How tall should the ticks be?*/
	public float tickSize;
	
	/**TODO: Auto-determine tick count...*/
	public float tickCount;
	
	protected boolean categorical;
	
	protected AXIS axis;
	protected boolean connect;
	
	protected NodeTuple<Text> prototypeText = new NodeTuple<Text>(new Text("prototype"));
	protected NodeTuple<Line> prototypeLine = new NodeTuple<Line>(new Line("prototype"));

	/**@param Which axis should this go on (valid values are X and Y)*/
	public Axis(String id, Specializer specializer) {
		super(id, IMPLANTATION_NAME, Node.PROVIDED_ATTRIBUTES);
		axis = AXIS.valueOf(id);
		
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(specializer, this);
		
		GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeText);
		GuideUtils.applyDefaults(specializer, LABEL_PROPERTY_TAG, prototypeText);

		GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LINE_PROPERTY_TAG, prototypeLine);
		GuideUtils.applyDefaults(specializer, LINE_PROPERTY_TAG, prototypeLine);
	}
	
	public void setConnect(boolean connect) {this.connect = connect;}

	public AXIS getAxis() {return axis;} 
	
	public void setElements(List<AutoguidePair> elements) {
		List<Pair> listing = validate(elements);		
		categorical = !allNumbers(listing);		
		
		Collection<PNode> marks = createLabeledTics(listing);
		PNode line = createLine(listing);
		if (line !=null) {marks.add(line);}
		
		this.removeAllChildren();
		this.addChildren(marks);
	}

	
	/**Create the major axial line for the axis.*/
	private PNode createLine(List<Pair> elements) {
		if (stencil.types.color.Color.isTransparent((java.awt.Color) prototypeLine.get("STROKE_COLOR"))) {return null;}

		NodeTuple<Text> newLine = new NodeTuple(new Line(axis.name()));
		Tuples.transfer(prototypeLine, newLine, false);
		
		float min = ((Number) elements.get(0).value).floatValue();
		float max = ((Number) elements.get(elements.size()-1).value).floatValue(); 
		
		if (max >0 && min <0) {/*do nothing, doesn't matter what connect is set to if the origin is bridged.*/}
		
		if (connect && (min >0)) {min =0;}
		else if (connect && (max <0)) {max =0;}
		
		if (axis == AXIS.X) {
			newLine.set("X.1", min);
			newLine.set("Y.1", axisOffset);
			newLine.set("X.2", max);
			newLine.set("Y.2", axisOffset);
		} else {
			newLine.set("X.1", axisOffset);
			newLine.set("Y.1", min);
			newLine.set("X.2", axisOffset);
			newLine.set("Y.2", max);
		}
		
		return newLine.getNode();
	}

	/**Create the labeled tick marks around the major axis line.*/
	private Collection<PNode> createLabeledTics(List<Pair> elements) {
		Collection<PNode> marks = new ArrayList<PNode>(elements.size() *2);
		
		if (categorical) {
			for (Pair p: elements) {
				double location = p.value;
				PNode tick = makeTick(location);
				if (tick != null) {marks.add(tick);}
				PNode label = makeLabel(p.label, location);
				if (label !=null) {marks.add(label);}
			}
		} else {
			double max = elements.get(elements.size()-1).value;
			double min = elements.get(0).value;
			
						
			double range = niceNum(max-min, false);							//'Nice' range
			double spacing = niceNum(range/(tickCount-1), true);			//'Nice' spacing;
			double graphMin = Math.floor(min/spacing) * spacing;			//Smallest value on the graph
			double graphMax = Math.ceil(max/spacing) * spacing;				//Largest value on the graph
			double nfrac = -Math.max(Math.floor(Math.log10(spacing)), 0); 	//Number of decimal places in the fraction
			String format = String.format("%%1.%1$df", (int) nfrac);		//Format template for labels
			
			
			for (double v=graphMin; v<(graphMax+.5*spacing); v+=spacing) {
				PNode label = makeLabel(String.format(format, v),v); 
				if (label != null) {marks.add(label);}
				PNode tick =makeTick(v);
				if (tick != null) {marks.add(tick);}
			}
		}
		
		return marks;
	}
	
	/**Make a tick-mark label.*/
	private PNode makeLabel(String text, double value) {
		if (stencil.types.color.Color.isTransparent((java.awt.Color) prototypeText.get("FONT_COLOR"))) {return null;}

		NodeTuple<Text> newLabel = new NodeTuple(new Text(text));
		Tuples.transfer(prototypeText, newLabel, false);
		
		newLabel.set("TEXT", text);

		Node label = newLabel.getNode();
		
		if (axis == AXIS.X) {
			label.setOffset(value + (label.getHeight()/2.0), axisOffset + textOffset);
			label.setRotation(Math.toRadians(90));
		} else {
			label.setOffset(axisOffset - label.getWidth() - textOffset, value - (label.getHeight()/2.0));
		}
		if ((Double) newLabel.get("WIDTH") < 5) {newLabel.set("WIDTH", 5);}
		return newLabel.getNode();
	}
	
	/**Make an actual tick mark.*/
	private PNode makeTick(double offset) {
		if (stencil.types.color.Color.isTransparent((java.awt.Color) prototypeLine.get("STROKE_COLOR"))) {return null;}
	
		NodeTuple<Line> newLine = new NodeTuple(new Line(axis.name()));
		Tuples.transfer(prototypeLine, newLine, false);
		
		if (axis == AXIS.X) {
			newLine.set("X.1", offset);
			newLine.set("Y.1", axisOffset - tickSize);
			newLine.set("X.2", offset);
			newLine.set("Y.2", axisOffset+tickSize);
		} else {
			newLine.set("X.1", axisOffset - tickSize);
			newLine.set("Y.1", offset);
			newLine.set("X.2", axisOffset+tickSize);
			newLine.set("Y.2", offset);
		}
		
		return newLine.getNode();
	}
	
	/**Finds a multiple of 1,2 or 5 or a power of 10 near the passed number.
	 * 
	 * From: Graphic Gems, "Nice Numbers for Graph Labels," by Paul Heckbert*/
	private static double niceNum(double num, boolean round) {
		int exp;
		double f;
		double nf;
		
		exp = (int) Math.floor(Math.log10(num));
		f = num/Math.pow(10, exp);
		if (round) {
			if (f< 1.5) {nf=1;}
			else if (f<3) {nf=2;}
			else if (f<7) {nf=5;}
			else {nf=10;}
		} else {
			if (f<=1) {nf=1;}
			else if (f<=2) {nf=2;}
			else if (f<=5) {nf=5;}
			else {nf=10;}
		}		
		return (float) (nf*Math.pow(10, exp));
	}
	
	
	/**Check if all input fields are numerical in nature.*/
	private static boolean allNumbers(Collection<Pair> elements) {
		for (Pair p: elements) {
			if (p.category()) {return false;}
		}
		return true;
	}
	
	
	/**Verify that all Autoguide pairs have exactly one result, and that result
	 * is some type of Number.
	 * 
	 * @param elements
	 * @return
	 */
	private List<Pair> validate(Collection<AutoguidePair> elements) {
		List<Pair> pairs = new ArrayList<Pair>(elements.size());
		Comparator c = new Comparator<Pair>() {
			public int compare(Pair p1, Pair p2) { 
				double n1 = p1.value;
				double n2 = p2.value;
				
				if (n1> n2) {return 1;}
				if (n1 < n2) {return -1;}
				return 0;
			}
			
		};
				
		for (AutoguidePair p: elements) {
			if (p.getResult().length != 1 || !(p.getResult()[0] instanceof Number))  {
				throw new IllegalArgumentException(String.format("Attempting to us a non-numeric result in an %1$s-axis legend: %2$s", axis.toString(), Arrays.deepToString(p.getResult())));
			} 
			Pair pair = new Pair(p.getInput()[0].toString(), ((Number) p.getResult()[0]).floatValue());
			pairs.add(pair);
		}
		
		Collections.sort(pairs, c);
		return pairs;
	}
}
