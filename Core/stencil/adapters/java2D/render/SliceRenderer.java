package stencil.adapters.java2D.render;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.geometry.PointTuple;
import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.render.mixins.*;

/** A slice is a fraction of a circle.**/
public class SliceRenderer extends AbstractShapeRenderer {
	/**Basic expected table schema.**/
	public static final TuplePrototype<SchemaFieldDef> SCHEMA = new TuplePrototype(
				ID,
				new SchemaFieldDef(SIZE.name(), 10d),
				X,
				Y,
				new SchemaFieldDef("START", 0d),		//Measured in degrees
				new SchemaFieldDef("END", 45d),			//Measured in degrees
				IMPLANT,
				FILL_COLOR,
				PEN_COLOR,
				PEN,
				VISIBLE,
				Z,
				BOUNDS,
				new SchemaFieldDef("OUTER", null, PointTuple.class));

	private final int outerIdx;
	
    public SliceRenderer(TuplePrototype<SchemaFieldDef> schema) {
    	super( Colorer.Util.instance(schema, schema.indexOf(FILL_COLOR)),
    		   Colorer.Util.instance(schema, schema.indexOf(PEN_COLOR)),
	           Stroker.Util.instance(schema, schema.indexOf(PEN), schema.indexOf(PEN_COLOR)),
	           Implanter.Util.instance(schema, schema.indexOf(IMPLANT)),
	           new Slicer(schema.indexOf("START"), schema.indexOf("END"), schema.indexOf(SIZE)),
	           Placer.Util.instance(schema, schema.indexOf(X), schema.indexOf(Y)),
	           new Registerer.None(),
	           new Rotater.None(),
	           schema.indexOf(BOUNDS));
    	
    	outerIdx = schema.indexOf("OUTER");
    }
    
    //TODO: properly handle viewTransform in bounds calculation
	@Override
	public void calcFields(TableShare share, AffineTransform viewTransform) {
		PointTuple[] points = new PointTuple[share.size()];
		Rectangle2D[] bounds = new Rectangle2D[share.size()];
		Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);

		AffineTransform trans = new AffineTransform();
		for (StoreTuple glyph: share) {
			trans.setToIdentity();
			Arc2D arc = (Arc2D) shaper.shape(glyph);
			
			Point2D start = arc.getStartPoint();
			Point2D end = arc.getEndPoint();
			double x = (start.getX() + end.getX())/2;
			double y = (start.getY() + end.getY())/2;
			Point2D p = new Point2D.Double(x,y);
			

			placer.place(trans, glyph);
			trans.transform(p,p);
			points[glyph.row()] = new PointTuple(p); 			
			Rectangle2D b =trans.createTransformedShape(arc).getBounds2D();
			bounds[glyph.row()] = b;
			ShapeUtils.add(fullBounds, b);
		}
		
		Column outerCol = share.columns()[outerIdx].replaceAll(points);
		Column boundsCol = share.columns()[boundsIdx].replaceAll(bounds);
		
		share.setColumn(outerIdx, outerCol);
		share.setColumn(boundsIdx, boundsCol);
		share.setBounds(fullBounds);
	}


	/**Generate a slice**/
    private static final class Slicer implements Shaper {
    	final int startIdx;
    	final int endIdx;
    	final int sizeIdx;
    	
    	public Slicer(int startIdx, int endIdx, int sizeIdx) {
    		assert startIdx >= 0 && endIdx >=0 && sizeIdx >= 0;
    		
    		this.startIdx = startIdx;
    		this.endIdx = endIdx;
    		this.sizeIdx = sizeIdx;
    	}
    	
		@Override
		public Shape shape(Tuple t) {
			double start = (Double) t.get(startIdx);
			double end = (Double) t.get(endIdx);
			double size = (Double) t.get(sizeIdx);
			
			
			return new Arc2D.Double(-size/2,-size/2, size, size, start, end-start, Arc2D.PIE);
		}
    }


}
