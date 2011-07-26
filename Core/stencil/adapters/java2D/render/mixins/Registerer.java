package stencil.adapters.java2D.render.mixins;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Registrations.Registration;
import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public interface Registerer {
	/**Create an affine transform that can be used to place a glyph at a position.
	 * Will take into account registration (which is why the bounds need to be known).
	 */
	public AffineTransform register(AffineTransform base, Tuple t, Rectangle2D bounds);

	/**No changes made, just echo the base transform back.*/
	public static final class None implements Registerer {
		public AffineTransform register(AffineTransform base, Tuple t, Rectangle2D bounds) {return base;}
	}
	
	/**Move item to the same registration every time.**/
	public static final class Const implements Registerer {
		public final Registration reg;
		public Const(Registration reg) {this.reg =reg;}
		public AffineTransform register(AffineTransform base, Tuple t, Rectangle2D bounds) {
			Point2D p = Registrations.registrationToTopLeft(reg, 0,0, bounds.getWidth(), bounds.getHeight());
			base.translate(p.getX(), p.getY());
			return base;
		}
	}


	/**Move from 0,0 to the bounds-calculated registration point.
	 */
	public static final class Variable implements Registerer {
		public final int regIdx;
		public Variable(int regIdx) {
			this.regIdx = regIdx;
		}
		
		public AffineTransform register(AffineTransform base, Tuple t, Rectangle2D bounds) {
			Registration registration = (Registration) t.get(regIdx);
			Point2D p = Registrations.registrationToTopLeft(registration, 0,0, bounds.getWidth(), bounds.getHeight());
			base.translate(p.getX(), p.getY());
			return base;
		}
	}

	
	public static class Util {
		private Util() {}
				
		public static Registerer instance(TuplePrototype<SchemaFieldDef> schema, int regIdx) {
			assert regIdx >=0;
			SchemaFieldDef regDef = schema.get(regIdx);
			
			if (regDef.isConstant() && regDef.defaultValue().equals(Registration.TOP_LEFT)) {return new None();}
			if (regDef.isConstant()) {return new Const((Registration) regDef.defaultValue());}
			else {return new Variable(regIdx);}			
		}
	}
}
