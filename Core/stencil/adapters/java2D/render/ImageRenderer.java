package stencil.adapters.java2D.render;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.adapters.java2D.render.mixins.*;
import stencil.display.Glyph;
import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.TuplePrototype;

public class ImageRenderer implements Renderer<TableView> {
	
	public static final TuplePrototype<SchemaFieldDef> SCHEMA = new TuplePrototype(
			ID,
			new SchemaFieldDef("FILE", null, String.class),
			ROTATION,
			new SchemaFieldDef(WIDTH.name(), Imager.AUTO_SCALE),
			new SchemaFieldDef(HEIGHT.name(), Imager.AUTO_SCALE),
			X,
			Y,
			IMPLANT,
			VISIBLE,
			REGISTRATION,
			Z,
			BOUNDS);


	private final Imager imager;
	private final Implanter implanter;
    private final Placer placer;
    private final Registerer reg;
    private final Rotater rotater;
    
    private final int boundsIdx, xIdx, yIdx;
    
    public ImageRenderer(TuplePrototype<SchemaFieldDef> schema) {
	    xIdx = schema.indexOf(X);
	    yIdx = schema.indexOf(Y);
	    boundsIdx = schema.indexOf(BOUNDS);
    	
    	imager = Imager.Util.instance(schema, schema.indexOf("FILE"), schema.indexOf(WIDTH), schema.indexOf(HEIGHT));
    	implanter  = Implanter.Util.instance(schema, schema.indexOf(IMPLANT));
	    placer  = Placer.Util.instance(schema, xIdx, yIdx);
	    reg = Registerer.Util.instance(schema, schema.indexOf(REGISTRATION));
	    rotater  = Rotater.Util.instance(schema, schema.indexOf(ROTATION));
    }
    
	@Override
	public void render(TableView layer, Graphics2D g, AffineTransform viewTransform) {
		for (Glyph glyph: new TupleIterator(layer, layer.renderOrder(), true)) {
		   if (!glyph.isVisible()) {continue;}		   
		   
		   BufferedImage img  = imager.image(glyph, viewTransform);
		   AffineTransform trans = trans(img, glyph, viewTransform);
		   g.drawRenderedImage(img, trans);
		}
		g.setTransform(viewTransform);
		Renderer.Util.debugRender(layer, g);
	}
	

	private static final AffineTransform IDENTITY = new AffineTransform();
	@Override
	public void calcFields(TableShare share, AffineTransform viewTransform) {
		Rectangle2D[] bounds = new Rectangle2D[share.size()];
		Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);

		for (StoreTuple glyph: new TupleIterator(share, true)) {			
		   BufferedImage img  = imager.image(glyph, IDENTITY);
		   Rectangle2D bound = new Rectangle2D.Double(0, 0, img.getWidth(), img.getHeight());
		   
		   AffineTransform trans = trans(img, glyph, viewTransform);		   
		   Rectangle2D b = trans.createTransformedShape(bound).getBounds2D(); 
		   bounds[glyph.row()] = b;
		   ShapeUtils.add(fullBounds, b);
		}
		Column newCol = share.columns()[boundsIdx].replaceAll(bounds);
		share.setColumn(boundsIdx, newCol);
		share.setBounds(fullBounds);
	}
	
    private AffineTransform trans(BufferedImage img, Glyph glyph, AffineTransform viewTransform) {
		   AffineTransform trans = new AffineTransform();
		   trans = placer.place(trans, glyph);

		   Rectangle2D actualBounds = new Rectangle2D.Double(0,0, img.getWidth(), img.getHeight());
		   Rectangle2D logicalBounds = (Rectangle2D) glyph.get(boundsIdx);
		   if (logicalBounds == null) {logicalBounds = actualBounds;}
		   trans.scale(logicalBounds.getWidth()/actualBounds.getWidth(), logicalBounds.getHeight()/actualBounds.getHeight());

		   if (!(rotater instanceof Rotater.None 
				   && implanter instanceof Implanter.Area
				   && reg instanceof Registerer.None)) {
			   trans = reg.register(trans, glyph, logicalBounds);
			   trans = rotater.rotate(trans, glyph);
			   trans = implanter.implant(trans, viewTransform, glyph);
		   }
		   
		   return trans;
 }
 
	
}
