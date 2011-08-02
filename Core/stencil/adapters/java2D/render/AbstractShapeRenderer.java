package stencil.adapters.java2D.render;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.awt.Shape;

import stencil.display.Glyph;
import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.adapters.java2D.render.mixins.*;

public abstract class AbstractShapeRenderer implements Renderer<TableView> {
	protected final Colorer filler;
    protected final Colorer outliner;
    protected final Stroker stroker;
    protected final Implanter implanter;
    protected final Shaper shaper;
    protected final Placer placer;
    protected final Registerer reg;
    protected final Rotater rotater;

    protected final int boundsIdx;
    
    protected AbstractShapeRenderer(Colorer filler, Colorer outliner, Stroker stroker, Implanter implanter, Shaper shaper, Placer placer, Registerer reg, Rotater rotater, int boundsIdx) {
    	this.filler = filler;
    	this.outliner = outliner;
    	this.stroker = stroker;
    	this.implanter = implanter;
    	this.shaper = shaper;
    	this.placer = placer;
    	this.rotater = rotater;
    	this.reg = reg;
    	
    	this.boundsIdx = boundsIdx;
    	
    	assert boundsIdx > 1;
    }
    

	@Override
	public void calcFields(TableShare share, AffineTransform viewTransform) {
		Rectangle2D[] bounds = new Rectangle2D[share.size()];
		Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);
		for (StoreTuple g: new TupleIterator(share, true)) {
			Shape shape = shaper.shape(g);	//Used as a reference for the basic shape to (potentially) optimize rendering
			AffineTransform trans = makeTrans(shape, g, viewTransform);
			Rectangle2D b = trans.createTransformedShape(shape.getBounds2D()).getBounds2D();
			bounds[g.row()] = b;
			ShapeUtils.add(fullBounds, b);
		}
		Column newCol = share.columns()[boundsIdx].replaceAll(bounds);
		share.setColumn(boundsIdx, newCol);
		share.setBounds(fullBounds);
	}

   private AffineTransform makeTrans(Shape shape, Glyph glyph, AffineTransform viewTransform) {
	   AffineTransform trans = new AffineTransform();
	   if (!(rotater instanceof Rotater.None && 
			   reg instanceof Registerer.None &&
			   implanter instanceof Implanter.Area)) {
		   trans = implanter.implant(trans, viewTransform, glyph);
		   trans = rotater.rotate(trans, glyph);
		   trans = reg.register(trans, glyph, shape.getBounds2D());
	   }
	   
	   trans = placer.place(trans, glyph);
	   return trans;
   }

   public void render(TableView layer, Graphics2D g, AffineTransform viewTransform) {
	   for (Glyph glyph: new TupleIterator(layer, layer.renderOrder(), true)) {
		   if (!glyph.isVisible()) {continue;}
		   
		   Rectangle2D bounds = glyph.getBoundsReference();
		   if (!(g.hitClip((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight()))) {continue;}
		   
		   Shape shape = shaper.shape(glyph);	//Used as a reference for the basic shape to (potentially) optimize rendering
		   AffineTransform trans = makeTrans(shape, glyph, viewTransform);
		   
		   if (!(filler instanceof Colorer.None)) {
			   filler.setColor(g, glyph);
			   complexDrawShape(g, null, shape, trans, true);
		   }
		   
		   if (!(stroker instanceof Stroker.None)) {
			   Stroke stroke = stroker.getStroke(glyph);
			   outliner.setColor(g, glyph);
			   complexDrawShape(g, stroke, shape, trans, false);
		   }
	   }
	   Renderer.Util.debugRender(layer, g);
	   g.setTransform(viewTransform);
   }
   
   /**Based on Prefuse's GraphicsLib....**/
   private void complexDrawShape(Graphics2D g, Stroke stroke, Shape shape, AffineTransform trans, boolean fill) {
       int x, y, w, h;
       double xx, yy, ww, hh;

       //Record current state
       Stroke origStroke = null;
       AffineTransform origTrans = g.getTransform();
       if (!fill) {
    	   origStroke = g.getStroke();
    	   g.setStroke(stroke);
       }
       
       //Modify graphics transform for this particular shape
       g.transform(trans);
       
	   // see if an optimized (non-shape) rendering call is available for us
       // these can speed things up significantly when zoomed out
       AffineTransform at = g.getTransform();
       double scale = Math.max(at.getScaleX(), at.getScaleY());

       if ( scale > 1.5 || (!(shape instanceof RectangularShape))) {
           if (fill) {g.fill(shape); }
           else {g.draw(shape);} 
       }
       else 
       {
           RectangularShape r = shape.getBounds2D();
           xx = r.getX(); ww = r.getWidth(); 
           yy = r.getY(); hh = r.getHeight();
           
           x = (int)xx;
           y = (int)yy;
           w = (int)(ww+xx-x);
           h = (int)(hh+yy-y);
           
           if ( shape instanceof Rectangle2D || shape instanceof RoundRectangle2D) {
               if (fill) {g.fillRect(x, y, w, h);}
               else  {g.drawRect(x, y, w, h);}
           } else if ( shape instanceof Ellipse2D ) {
               if (fill) {g.fillOval(x, y, w, h);}
               else {g.drawOval(x, y, w, h);}
           } else {
               if (fill) {g.fill(shape); }
               else {g.draw(shape); }
           }
       }
       
       if(!fill) {g.setStroke(origStroke);}
       g.setTransform(origTrans);
   }
}
