package stencil.adapters.java2D.data.guides;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.adapters.java2D.data.glyphs.*;

import stencil.parser.tree.Guide;
import stencil.tuple.Tuple;

public class PointLabel extends Guide2D {
	protected Glyph2D prototypeText = new Text(null, "prototype");
	
	protected final Collection<Glyph2D> marks = new ArrayList();

	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	public PointLabel(Guide guideDef) {super(guideDef);}
		
	public synchronized void setElements(List<Tuple> elements) {
		marks.clear();
		
		for (Tuple t: elements) {
			Glyph2D label = prototypeText.update(t);
			marks.add(label);
		}
		
		bounds = GuideUtils.fullBounds(marks);
	}

	public Rectangle2D getBoundsReference() {return bounds;}
	
	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		for (Glyph2D glyph: marks) {glyph.render(g, viewTransform);}
	}
}
