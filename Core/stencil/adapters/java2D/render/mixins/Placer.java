package stencil.adapters.java2D.render.mixins;

import java.awt.geom.AffineTransform;

import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public interface Placer {
	/**Create an affine transform that can be used to place a glyph at a position.
	 * Will take into account registration (which is why the bounds need to be known).
	 */
	public AffineTransform place(AffineTransform base, Tuple t);
	
	/**Read out the positioning and place it there (assumes fine-tuning based on registration will be handled separately).
	 */
	public static final class Variable implements Placer {
		public final int xIdx, yIdx;
		public Variable(int xIdx, int yIdx) {this.xIdx = xIdx; this.yIdx = yIdx;}
		@Override
		public AffineTransform place(AffineTransform base, Tuple t) {
			double tx = (Double) t.get(xIdx);
			double ty = (Double) t.get(yIdx);
			base.translate(tx,-ty);
			return base;
		}
	}
	
	public static class Util {
		private Util() {}
		public static Placer instance(TuplePrototype<SchemaFieldDef> schema, int xIdx, int yIdx) {
			assert xIdx >=0 && yIdx >= 0;
			return new Variable(xIdx, yIdx);			
		}
	}
}
