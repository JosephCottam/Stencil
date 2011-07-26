package stencil.adapters.java2D.render.mixins;

import java.awt.Graphics2D;
import java.awt.Paint;

import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.color.ColorCache;

public interface Colorer {
	
	/**Setup the fill on the graphics device using the given tuple for
	 * fill information.
	 */
	public abstract void setColor(Graphics2D g, Tuple t);


	/**Set fill to background.**/
	public final class None implements Colorer {
		public void setColor(Graphics2D g, Tuple t) {g.setPaint(g.getBackground());}
	}
	
	
	/**The same fill used for all renderings**/
	public final class Const implements Colorer {
		private final Paint paint;
		public Const(Paint paint) {this.paint = paint;}
		public void setColor(Graphics2D g, Tuple t) {g.setPaint(paint);}
	}

	
	/**A potentially unique fill for each value passed.*/
	public final class Variable implements Colorer {
		final int fillIdx;
		public Variable(int fillIdx) {this.fillIdx = fillIdx;}
		
		public void setColor(Graphics2D g, Tuple t) {
			Paint p = (Paint) t.get(fillIdx);
			g.setPaint(p);
		}
	}


	public final class Util {
		private  Util() {}
		public static Colorer instance(TuplePrototype<SchemaFieldDef> schema, int idx) {
			assert idx >= 0;
			SchemaFieldDef<Paint> def = schema.get(idx);
			
			if (!schema.get(idx).isConstant()) {
				return new Variable(idx);
			} else if (ColorCache.isTransparent(def.defaultValue())) {
				return new None();
			} else {
				return new Const(def.defaultValue());
			}
		}
	}
}
