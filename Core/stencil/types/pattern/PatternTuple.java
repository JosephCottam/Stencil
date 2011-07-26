package stencil.types.pattern;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;

public class PatternTuple implements Paint, PrototypedTuple {
	public static final String PROTOTYPE_STRING ="(PatternTuple self, String pattern, Color fore, Color back, double weight, int scale)";
	public static final TuplePrototype PROTOTYPE;
	static {
		TuplePrototype proto = null;
		try{proto = ParseStencil.prototype(PROTOTYPE_STRING, false);} 
		catch (ProgramParseException e) {throw new Error("Error preparing PatternTuple class.", e);}
		finally {PROTOTYPE = proto;}
	}
	
	
	protected final String pattern;
	protected final int scale;
	protected final double weight;
	protected final Color fore;
	protected final Color back;

	protected Paint paint;

	
	public PatternTuple(String pattern, Color fore, Color back, int scale, double weight) {
		this.pattern = pattern;
		this.fore = fore == null ? Color.BLACK : fore;
		this.back = back == null ? Color.WHITE : fore;
		this.scale = scale;
		this.weight = weight;
		
		if (pattern.toUpperCase().equals("HATCH")) {paint = hatch(fore, back, scale, weight);}
		else if (pattern.toUpperCase().equals("SOLID")) {paint = fore;}
		else {throw new IllegalArgumentException("Unknown pattern: " + pattern);}
	}

	
	//------------------  Paint Methods ------------------------------------
	@Override
	public int getTransparency() {return paint.getTransparency();}

	@Override
	public PaintContext createContext(ColorModel cm,
			Rectangle deviceBounds, Rectangle2D userBounds,
			AffineTransform xform, RenderingHints hints) {
		if (hints ==null) {hints = new RenderingHints(null);} //TODO: Not sure why this is required to do a textured paint, but it is!
		return paint.createContext(cm, deviceBounds, userBounds, xform, hints);
	}


	//------------------  Tuple Methods ------------------------------------
	@Override
	public Object get(String name) throws InvalidNameException {
		if (name.equals("self")) {return this;}
		if (name.equals("fore")) {return fore;}
		if (name.equals("back")) {return back;}
		if (name.equals("pattern")) {return pattern;}
		if (name.equals("scale")) {return scale;}
		if (name.equals("weight")) {return weight;}
		throw new InvalidNameException(name, PROTOTYPE);
	}
		

	@Override
	public Object get(int idx) throws TupleBoundsException {return get(PROTOTYPE.get(idx).name());}

	@Override
	public TuplePrototype prototype() {return PROTOTYPE;}

	@Override
	public int size() {return PROTOTYPE.size();}
	
	public String toString() {return Tuples.toString(this);}


	/**Method to actually produce a hatched pattern.**/
	private static Paint hatch(Color fore, Color back, int scale, double weight) {
		BufferedImage i = new BufferedImage(scale, scale, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) i.getGraphics();

		//Background
		g.setBackground(back);
		g.clearRect(0,0, scale, scale);

		//Foreground
		g.setColor(fore);
		g.setStroke(new BasicStroke((float) weight));
		g.draw(new Line2D.Double(0, 0, scale, scale));
		g.draw(new Line2D.Double(0, scale, scale,0));
		return new TexturePaint(i, new Rectangle2D.Double(0,0, i.getWidth(), i.getHeight()));
	}
}
