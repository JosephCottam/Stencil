package stencil.adapters.java2D.render.mixins;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.color.ColorCache;

public interface Stroker {
	/**What stroke will be used.  No stroke is returned as null;**/
	public abstract Stroke getStroke(Tuple t);
	
	public static final class Util {
		private Util() {}
		public static Stroker instance(TuplePrototype<SchemaFieldDef> schema, int penIdx, int paintIdx) {
			assert penIdx >= 0 && paintIdx >=0;
			SchemaFieldDef<Stroke> penDef = schema.get(penIdx);
			SchemaFieldDef<Paint> paintDef = schema.get(paintIdx);
			
			if (paintDef.isConstant() && ColorCache.isTransparent(paintDef.defaultValue())) {return new None();}
			if (!penDef.isConstant()) {return new Variable(penIdx);}
			else {return new Const(penDef.defaultValue());}
		}
	}
	
	
	/**No stroke; indicates the paint was constant transparent, so stroke was not needed**/
	public final class None implements Stroker {
		public None() {}
		@Override
		public Stroke getStroke(Tuple t) {return null;}
		public Shape stroke(Shape s, Tuple t) {return null;}
	}
	
	/**Always uses the same stroke, regardless of tuple supplied.**/
	public final class Const implements Stroker {
		final Stroke stroke;
		public Const(Stroke stroke) {this.stroke = stroke;}
		@Override
		public Stroke getStroke(Tuple t) {return stroke;}
	}
	
	/**Stroke from the data set**/
	public final class Variable implements Stroker {
		private final int strokePen;
		public Variable(int strokePen) {this.strokePen = strokePen;}
		@Override
		public Stroke getStroke(Tuple t)  {return (Stroke) t.get(strokePen);}
	}
}
