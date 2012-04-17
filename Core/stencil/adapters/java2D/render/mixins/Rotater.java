package stencil.adapters.java2D.render.mixins;

import java.awt.geom.AffineTransform;

import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public interface Rotater {
	/**Return a new transform, derived from base but includes rotation.
	 * May return a modified version of the passed transform.
	 * Rotations are expected in radians.*/
	public abstract AffineTransform rotate(AffineTransform base, Tuple t);
	
	public static final class None implements Rotater {
		@Override
		public AffineTransform rotate(AffineTransform base, Tuple t) {return base;}
	}
	
	public static final class Const implements Rotater {
		private final double radians;
		public Const(double radians) {this.radians = radians;}

		@Override
		public AffineTransform rotate(AffineTransform base, Tuple t) {
			base.rotate(radians);
			return base;
		}
	}
	
	
	public static final class Variable implements Rotater {
		private final int idx;
		public Variable(int idx) {this.idx = idx;}

		@Override
		public AffineTransform rotate(AffineTransform base, Tuple t) {
			double radians = (Double) t.get(idx);
			base.rotate(radians);
			return base;
		}
	}
	


	public final class Util {
		private  Util() {}
		public static Rotater instance(TuplePrototype<SchemaFieldDef> schema, int idx) {
			assert idx >=0;
			
			SchemaFieldDef<Double> def = schema.get(idx);
			if (!def.isConstant()) {
				return new Variable(idx);
			} else if (def.defaultValue() == 0){
				return new None();
			} else {
				return new Const(def.defaultValue());
			}
		}
	}
}
