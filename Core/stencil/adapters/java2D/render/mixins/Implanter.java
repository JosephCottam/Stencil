package stencil.adapters.java2D.render.mixins;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;


/**Modify the scaling of a shape according to implantatiton rules.
 * Valid implantations are:
 * 
 *  
 *  X : Only change scale with the X axis
 *  Y : Only change scale with the Y axis
 *  Point: Do not change scales when zooming
 *  Area: Change scale along both X and Y
 * 
 * The most common use is "Area"
 * 
 * TODO: Add "largest" and "smallest"
 */
public interface Implanter {
	public static enum ImplantBy {AREA, POINT, LINE, X,Y, LARGEST, SMALLEST, SCREEN}

	/**What transform should be used to correct for implantation?  
	 * This returns a transform that should be concatenated with the view transform, not replace it.
	 * 
	 * If view transform is identity or null, the return value is equal to the base.
	 * 
	 * @param base The current transform to be applied to the shape, may be modified by this method
	 * @param view The view transform
	 * @param t    The tuple the glyph is based on
	 * @return     A transform to be applied to be the shape 
	 */
	public AffineTransform implant(AffineTransform base, AffineTransform view, Tuple t);
	
	/**Identity implanter, provided separately for optimization opportunities.**/
	public final class Area implements Implanter {
		public AffineTransform implant(AffineTransform base, AffineTransform view, Tuple t) {return base;}
	}
	
	public final class Const implements Implanter {
		private final ImplantBy implant;
		private Const(ImplantBy implant) {this.implant =implant;}
		public AffineTransform implant(AffineTransform base, AffineTransform view, Tuple t) {return Util.implant(implant, base, view);}
	}

	public static final class Variable implements Implanter {
		public final int implantIdx;
		public Variable(int idx) {this.implantIdx = idx;}
		public AffineTransform implant(AffineTransform base, AffineTransform view, Tuple t) {
			ImplantBy implant = (ImplantBy) Converter.convert(t.get(implantIdx), ImplantBy.class);
			return Util.implant(implant, base, view);
		}
	}
	
	public static final class Util {
		private Util() {}
		
		private static final AffineTransform implant(ImplantBy implant, AffineTransform base, AffineTransform view) {
			double factor;
			
			if (view == null || view.isIdentity()) {return base;}
			
			switch (implant) {
				case AREA: return base;
				case SCREEN: 
					try {return view.createInverse();}
					catch (NoninvertibleTransformException e) {return base;}
				case POINT: factor = 1d; break;
				case LINE: factor = 1d; break;
				case X: factor = view.getScaleX(); break;
				case Y: factor = view.getScaleY(); break;
				case LARGEST: factor = Math.max(view.getScaleX(), view.getScaleY()); break;
				case SMALLEST: factor = Math.min(view.getScaleX(), view.getScaleY()); break;
				default: throw new Error("Unahndled case in ScaleWith: " + implant.name());

			}
			
			double sx = view.getScaleX() == 0 ? 1 : factor/view.getScaleX();
			double sy = view.getScaleY() == 0 ? 1 : factor/view.getScaleY();

			base.scale(sx, sy);
			return base;
		}		
		

		public static final Implanter instance(TuplePrototype<SchemaFieldDef> schema, int implantIdx) {
			SchemaFieldDef<ImplantBy> implantDef = schema.get(implantIdx);
			if (implantDef.isConstant()) {
				if (implantDef.defaultValue().equals(ImplantBy.AREA)) {return new Area();}
				return new Const(implantDef.defaultValue());}
			else  {return new Variable(implantIdx);}
		}
	}

	
}
