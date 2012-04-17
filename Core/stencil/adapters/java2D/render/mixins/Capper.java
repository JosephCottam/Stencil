package stencil.adapters.java2D.render.mixins;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Registrations.Registration;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public interface Capper {
	
	/**Setup the fill on the graphics device using the given tuple for
	 * fill information.
	 */
	public abstract GeneralPath getCap(Tuple t, Shape line);


	/**Return an empty path**/
	public final class None implements Capper {
		@Override
		public GeneralPath getCap(Tuple t, Shape line) {return new GeneralPath();}
	}
	
	
	public final class Const implements Capper {
		private final String type;
		private final boolean left;
		private final double weight; 
		public Const(String type, double weight, boolean left) {
			this.type = type; 
			this.left = left;
			this.weight = weight;
		}
		
		@Override
		public GeneralPath getCap(Tuple t, Shape line) {
			return Util.cap(type, line, weight, left);
		}
	}

	
	public final class Variable implements Capper {
		final int typeIdx, weightIdx;
		final boolean left;
		
		public Variable(int typeIdx, int  weightIdx, boolean left) {
			this.typeIdx = typeIdx;
			this.weightIdx = weightIdx;
			this.left = left;
		}
		
		@Override
		public GeneralPath getCap(Tuple t, Shape line) {
			Stroke s = (Stroke) t.get(weightIdx);
			double weight = ((BasicStroke) s).getLineWidth();
			String type = (String) t.get(typeIdx);
			
			return Util.cap(type, line, weight, left);
			
		}
	}


	public final class Util {
		private  Util() {}

		public static Capper instance(TuplePrototype<SchemaFieldDef> schema, int typeIdx, int strokeIdx, boolean left) {
			assert typeIdx >=0 && strokeIdx >= 0;
			
			SchemaFieldDef<String> typeDef = schema.get(typeIdx);
			SchemaFieldDef<Stroke> strokeDef = schema.get(strokeIdx);
			
			if (typeDef.isConstant() && typeDef.defaultValue().equals("NONE")) {
				return new None();
			} else if (typeDef.isConstant() && strokeDef.isConstant()) {
				Stroke s = strokeDef.defaultValue();
				double weight = ((BasicStroke) s).getLineWidth();
				return new Const(typeDef.defaultValue(), weight, left);
			} else {
				return new Variable(typeIdx, strokeIdx, left);
			}
		}
		
		
		
		static GeneralPath cap(String type, Shape l, double weight, boolean left) {
			if (l instanceof Line2D) {
				Line2D line = (Line2D) l;
				return cap (type, line.getX1(), line.getY1(), line.getX2(), line.getY2(), weight, left);
			} else if (l instanceof QuadCurve2D) {
				QuadCurve2D curve = (QuadCurve2D) l;
				return cap (type, curve.getX1(), curve.getY1(), curve.getX2(), curve.getY2(), weight, left);
			} else {
				throw new IllegalArgumentException("Cannot put caps on shape of type " + l.getClass().getSimpleName());}
		}
		
		private static final GeneralPath EMPTY_PATH = new GeneralPath(); 
		//TODO: Investigate caps as part of strokes...
		private static GeneralPath cap(String type, double x1, double y1, double x2, double y2, double weight, boolean left) {
			double scale = weight*6;
			double offset = scale/2.0;
			double slope = (y2-y1)/(x2-x1);
			
			
			double xe = left ? x1 : x2;
			double ye = left ? y1 : y2;
			double rotation;
			
			if (slope ==0) {rotation =0;}
			else if (slope > 0) {rotation = Math.atan(slope);}
			else {rotation = Math.atan(slope) + Math.PI;}

			if ((x1 >= x2 && y1 > y2)
					|| (x1 < x2 && y1 > y2)) {rotation = rotation + Math.PI;}

			if (left) {
				if (slope ==0 && x1 > x2) {rotation =Math.PI;}
			} else {
				rotation = rotation + Math.PI;
			}
			
			
			if (type.equals("ARROW")) {
				double x = xe-offset;
				double y = ye-offset;
				
				Point2D pt = Registrations.topLeftToRegistration(Registration.CENTER, x,y,scale,scale);
				GeneralPath p = new GeneralPath(Shapes.getShape(StandardShape.TRIANGLE_LEFT, x, y, scale, scale));
				p.transform(AffineTransform.getRotateInstance(rotation, pt.getX(), pt.getY()));
				return p;
			} else if (type.equals("NONE")){
				return EMPTY_PATH;
			} else {
				throw new RuntimeException("Only NONE and ARROW caps are known.  Requested: " + type);
			}
		}
	}
}
