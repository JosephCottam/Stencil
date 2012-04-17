package stencil.types.stroke;

import java.awt.BasicStroke;
import java.awt.Stroke;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.util.collections.ArrayUtil;

/**Pen is the pattern information about a stroke or
 * a fill, but not its color elements (those are in "Paint").*/
public class StrokeTuple implements PrototypedTuple {
	static final String SELF    = "self";
	static final String WIDTH   = "width";
	static final String JOIN    = "join";
	static final String CAP     = "cap";
	static final String PATTERN = "pattern";
	static final String PHASE   = "phase";
	static final String LIMIT   = "limit";
	
	static final float DEFAULT_WIDTH = 1f;
	static final Join DEFAULT_JOIN = Join.ROUND;
	static final Cap DEFAULT_CAP  = Cap.ROUND;
	static final float[] DEFAULT_PATTERN = Pattern.SOLID.mask;
	static final float DEFAULT_LIMIT = 15f;
	static final float DEFAULT_PHASE  = 0f;
	
	public static final Stroke DEFAULT_STROKE = new BasicStroke(DEFAULT_WIDTH, DEFAULT_CAP.v, DEFAULT_JOIN.v, DEFAULT_LIMIT, DEFAULT_PATTERN, DEFAULT_PHASE);
	private static final String[] FIELDS = new String[] {SELF, WIDTH, JOIN, CAP, PATTERN, PHASE, LIMIT};
	private static final Class[] TYPES = new Class[] {BasicStroke.class, Double.class, Join.class, Cap.class, float[].class, float.class, float.class};
	private static TuplePrototype PROTOTYPE = new TuplePrototype(FIELDS, TYPES);
	public static final int SELF_IDX = ArrayUtil.indexOf(SELF, FIELDS);
	public static final int WIDTH_IDX = ArrayUtil.indexOf(WIDTH, FIELDS);
	public static final int JOIN_IDX = ArrayUtil.indexOf(JOIN, FIELDS);
	public static final int CAP_IDX = ArrayUtil.indexOf(CAP, FIELDS);
	public static final int PATTERN_IDX = ArrayUtil.indexOf(PATTERN, FIELDS);
	public static final int PHASE_IDX = ArrayUtil.indexOf(PHASE, FIELDS);
	public static final int LIMIT_IDX = ArrayUtil.indexOf(LIMIT, FIELDS);
	
	public enum Join {
		MITER (BasicStroke.JOIN_MITER), 
		BEVEL (BasicStroke.JOIN_BEVEL), 
		ROUND (BasicStroke.JOIN_ROUND);
		
		int v;
		private Join(int v) {this.v = v;}
	}
	
	public enum Cap {
		BUTT (BasicStroke.CAP_BUTT), 
		ROUND (BasicStroke.CAP_ROUND), 
		SQUARE (BasicStroke.CAP_SQUARE);
		
		int v;
		private Cap(int v) {this.v = v;}
	}
	
	public enum Pattern {	
		SOLID     (null),
		DOT      (1f, 2f),
		DASH5    (5f),
		DASH10   (10f),
		DOT_DASH (10f, 4f, 2f, 4f);
		
		float[] mask;
		private Pattern(float... mask) {this.mask = mask;}
	}
		
	private final BasicStroke stroke;
	
	public StrokeTuple(BasicStroke stroke) {this.stroke = stroke;}
	
	public java.awt.Stroke getStroke() {return stroke;}

	@Override
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}

	@Override
	public Object get(int idx) throws TupleBoundsException {
		if (SELF_IDX == idx) {return this;}
		if (WIDTH_IDX == idx) {return stroke.getLineWidth();}
		if (JOIN_IDX == idx) {return stroke.getLineJoin();}
		if (CAP_IDX == idx) {return stroke.getEndCap();}
		if (PATTERN_IDX == idx) {return stroke.getDashArray();}
		if (PHASE_IDX == idx) {return stroke.getDashPhase();}
		if (LIMIT_IDX == idx) {return stroke.getMiterLimit();}
		throw new TupleBoundsException(idx, this);
	}

	@Override
	public TuplePrototype prototype() {return PROTOTYPE;}

	public boolean isDefault(String name, Object value) {
		if (name.equals(WIDTH)) {return value.equals(DEFAULT_WIDTH);}
		if (name.equals(JOIN)) {return value.equals(DEFAULT_JOIN);}
		if (name.equals(CAP)) {return value.equals(DEFAULT_CAP);}
		if (name.equals(PATTERN)) {return value == null;}
		if (name.equals(PHASE)) {return value.equals(DEFAULT_PHASE);}
		if (name.equals(LIMIT)) {return value.equals(DEFAULT_LIMIT);}
		return false;
	}

	@Override
	public int size() {return FIELDS.length;}

	@Override
	public String toString() {return Tuples.toString("Stroke", this, 1);}

	@Override
	public boolean equals(Object other) {
		return other instanceof StrokeTuple 
				&& ((StrokeTuple) other).stroke.equals(stroke);
	}
	
	@Override
	public int hashCode() {return Tuples.hashCode(this);}
}
