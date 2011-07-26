package stencil.adapters.java2D.render.mixins;

import java.awt.Shape;

import stencil.adapters.general.Shapes;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public interface Shaper {
	/**Return a shape based on the passed tuple.**/
	public abstract Shape shape(Tuple t);
	
	/**Return a statically determined shape. 
	 * This class ignores the argument to the shape method and just returns the statically determined shape.
	 */
	public static class Const implements Shaper {
		private final Shape shape;
		public Const (StandardShape shape, double width, double height) {this.shape = Shapes.getShape(shape, 0, 0, width, height);}
		public Shape shape(Tuple t) {return shape;}
	}
	
	/**Return a shape based on the data in a tuple.
	 * The shape field index of the tuple is specified in the constructor.  
	 */
	public static class Variable implements Shaper {
		private final int shapeIdx;		//Index to look in tuple to get the shape
		private final int heightIdx;
		private final int widthIdx;
		private final int sizeIdx;
		
		public Variable(int shapeIdx,  int widthIdx, int heightIdx, int sizeIdx) {
			this.shapeIdx = shapeIdx;
			this.heightIdx = heightIdx;
			this.widthIdx = widthIdx;
			this.sizeIdx = sizeIdx;
		}
		
		public Shape shape(Tuple t) {
			double height = (Double) t.get(heightIdx);
			double width = (Double) t.get(widthIdx);
			double size = (Double) t.get(sizeIdx);
			StandardShape shape = (StandardShape) t.get(shapeIdx);
			
			if  (size <0) {
				return Shapes.getShape(shape, 0, 0, width,height);
			} else {
				return Shapes.getShape(shape, 0,0, size,size);
			}
		}
	}
	
	public static class Util {
		private Util() {}
		public static Shaper instance(TuplePrototype<SchemaFieldDef> schema, int shapeIdx, int widthIdx, int heightIdx, int sizeIdx) {
			assert shapeIdx >=0 && widthIdx >= 0 && heightIdx >= 0 && sizeIdx >=0;
			
			SchemaFieldDef<StandardShape> shapeDef = schema.get(shapeIdx);
			SchemaFieldDef<Double> widthDef = schema.get(widthIdx);
			SchemaFieldDef<Double> heightDef = schema.get(heightIdx);
			SchemaFieldDef<Double> sizeDef = schema.get(sizeIdx);
			
			if (shapeDef.isConstant() && widthDef.isConstant() && heightDef.isConstant() && sizeDef.isConstant()) {
				if (sizeDef.defaultValue() <0) {
					return new Const(shapeDef.defaultValue(), widthDef.defaultValue(), heightDef.defaultValue());
				} else {
					return new Const(shapeDef.defaultValue(), sizeDef.defaultValue(), sizeDef.defaultValue());
				}
			} else {
				return new Variable(shapeIdx, widthIdx, heightIdx, sizeIdx);
			}
		}
	}
}
