package stencil.adapters.java2D.render.mixins;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public interface Liner {
	/**Return a shape based on the passed tuple.**/
	public abstract Shape line(Tuple t);
	
	/**Return a statically determined shape. 
	 * This class ignores the argument to the shape method and just returns the statically determined shape.
	 */
	public static class Const implements Liner {
		private final Shape line;
		public Const (double x1, double y1, double x2, double y2) {line = new Line2D.Double(x1,-y1,x2,-y2);}
		public Const (double arcHeight, double x1, double y1, double x2, double y2) {line = Util.getArc(x1,-y1,x2,-y2,arcHeight);}
		public Shape line(Tuple t) {return line;}
	}
	
	public static class VariableLine implements Liner {
		protected final int x1Idx;		
		protected final int y1Idx;
		protected final int x2Idx;
		protected final int y2Idx;
		
		public VariableLine(int x1Idx, int y1Idx, int x2Idx, int y2Idx) {
			this.x1Idx = x1Idx;
			this.y1Idx = y1Idx;
			this.x2Idx = x2Idx;
			this.y2Idx = y2Idx;
		}
		
		public Shape line(Tuple t) {
			double x1 = (Double) t.get(x1Idx);
			double y1 = (Double) t.get(y1Idx);
			double x2 = (Double) t.get(x2Idx);
			double y2 = (Double) t.get(y2Idx);
			return new Line2D.Double(x1,-y1,x2,-y2);
		}
	}
	

	public static class VariableArc extends VariableLine {
		private final int arcHeightIdx;
		
		public VariableArc(int x1Idx, int y1Idx, int x2Idx, int y2Idx, int arcHeightIdx) {
			super(x1Idx, y1Idx, x2Idx, y2Idx);
			this.arcHeightIdx = arcHeightIdx;
		}
		
		public QuadCurve2D line(Tuple t) {
			double x1 = (Double) t.get(x1Idx);
			double y1 = (Double) t.get(y1Idx);
			double x2 = (Double) t.get(x2Idx);
			double y2 = (Double) t.get(y2Idx);
			double arcHeight = (Double) t.get(arcHeightIdx);
			return Util.getArc(x1,-y1,x2,-y2, arcHeight);
		}
	}

	public static class Util {
		private Util() {}
		
		private static final QuadCurve2D getArc( double x1, double y1, double x2, double y2, double arcHeight) {
			Point2D mid = new Point2D.Double((x1+x2)/2.0, (y1+y2)/2.0);
			
			Point2D control;
			
			//Vertical line
			if (x1 == x2) {
				control = new Point2D.Double(mid.getX()+arcHeight, mid.getY());

			//Horizontal line
			} else if (y1 == y2) {
				control = new Point2D.Double(mid.getX(), mid.getY() + arcHeight);

			//Other types of lines
			} else {
				double length = Point2D.distance(x1, y1, x2, y2);
				double scale = arcHeight/length;
				
				double rise = (y1 - y2) * scale;
				double run =  (x1 - x2) * scale;
				
				double x = mid.getX() - rise; 
				double y = mid.getY() + run;
					
				control = new Point2D.Double(x,-y);
			}
			
			QuadCurve2D arc = new QuadCurve2D.Double();
			arc.setCurve(x1,y1, control.getX(), control.getY(), x2,y2);
			return arc;
		}
		
		public static Liner instance(TuplePrototype<SchemaFieldDef> schema, int arcHeightIdx, int x1Idx, int y1Idx, int x2Idx, int y2Idx) {
			assert x1Idx >= 0 && y1Idx >=0 && x2Idx >= 0 && y2Idx >= 0;
			
			SchemaFieldDef<Double> x1Def = schema.get(x1Idx);
			SchemaFieldDef<Double> y1Def = schema.get(y1Idx);
			SchemaFieldDef<Double> x2Def = schema.get(x2Idx);
			SchemaFieldDef<Double> y2Def = schema.get(y2Idx);
			
			if (arcHeightIdx <0 ) {
				if (x1Def.isConstant() && y1Def.isConstant() && x2Def.isConstant() && y2Def.isConstant()) {
					return new Const(x1Def.defaultValue(), y1Def.defaultValue(), x2Def.defaultValue(), y2Def.defaultValue());
				} else {
					return new VariableLine(x1Idx, y1Idx, x2Idx, y2Idx);
				}
			} else {
				SchemaFieldDef<Double> arcHeight = schema.get(arcHeightIdx);
				if (x1Def.isConstant() && y1Def.isConstant() && x2Def.isConstant() && y2Def.isConstant() && arcHeight.isConstant()) {
					return new Const(x1Def.defaultValue(), y1Def.defaultValue(), x2Def.defaultValue(), y2Def.defaultValue(), arcHeight.defaultValue());
				} else {
					return new VariableArc(x1Idx, y1Idx, x2Idx, y2Idx, arcHeightIdx);
				}
			}
		}
	}
}
