package stencil.types.color;

import stencil.tuple.Tuple;
import stencil.tuple.instances.Singleton;
import stencil.types.Converter;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;

//TODO: Reword due to ColorTuple (no more 'get' operators required)
@Description("Create and modify colors.")
@Module
public final class ColorUtils extends BasicModule {
	private enum DIR {up, down, full, none}

	
	/**Ensure integer is in the proper range**/
	static final int rangeValue(int i) {
		if (i<0) {i=0;}
		if (i>255) {i=255;}
		return i;
	}
	
	private static ColorTuple validate(Object o) {
		if (o instanceof ColorTuple) {return (ColorTuple) o;}
		if (o instanceof java.awt.Color) {return ColorCache.toTuple(((java.awt.Color) o).getRGB());}
		else throw new RuntimeException(String.format("Not a known color format: %1$s (object type %2$s). ", o.toString(), o.getClass().getName()));
	}

	private static Tuple mod(Object source, int comp, Object v, String name) {
		ColorTuple color = validate(source);
		
		Integer value = rangeValue(Converter.toInteger(v));
		
		return color.modify(comp, value);
	}

	private static Tuple mod(Object source, int comp, ColorUtils.DIR dir, String name) {
		ColorTuple color = validate(source);
		float value = ((Integer) color.get(comp))/255f;

		switch (dir){
			case up: value = value  + ((1-value) /2); break; //Up by half the distance to full
			case down: value = value  - (value /2); break;  //Down by half the distance to none
			case full: value = 1; break;
			case none: value =0;
		}

		if (value > 1) {value = 1;}
		if (value <0) {value = 0;}

		return mod(source, comp, value, name);
	}

	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
	public static Tuple darker(Object o) {return Singleton.from(validate(o).darker());}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
	public static Tuple brighter(Object o) {return Singleton.from(validate(o).brighter());}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
	@Description("Move the color towards white by the passed factor (e.g. .5 will move it half-way to white from where it is now).")
	public static ColorTuple lighten(Object o, double factor) {
		ColorTuple c = validate(o);
		int r = (int) Math.min(255, ((255-c.getRed())*factor) + c.getRed());
		int g = (int) Math.min(255, ((255-c.getGreen())*factor) + c.getGreen());
		int b = (int) Math.min(255, ((255-c.getBlue())*factor) + c.getBlue());
		return ColorCache.toTuple(new java.awt.Color(r,g,b).getRGB());
	}

	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(int blue)", alias={"map","query"})
	public static int getBlue(Object v) {return validate(v).getBlue();}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
	public static Tuple setBlue(Object v, Object o) {return mod(o, ColorTuple.BLUE, v, "SetBlue");}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(int red)", alias={"map","query"})
	public static Tuple getRed(Object v) {return Singleton.from(validate(v).getRed());}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
	public static Tuple setRed(Object v, Object o) {return mod(o, ColorTuple.RED, v, "SetRed");}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(int green)", alias={"map","query"})
	public static Tuple getGreen(Object v) {return Singleton.from(validate(v).getGreen());}


	@Operator()
	@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
	public static Tuple setGreen(Object v, Object o) {return mod(o, ColorTuple.GREEN, v, "setGreen");}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(int alpha)", alias={"map","query"})
	public static Tuple getAlpha(Object v) {return Singleton.from(validate(v).getAlpha());}
	

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
	public static Tuple setAlpha(Object v, Object o) {return mod(o, ColorTuple.ALPHA, v, "setAlpha");}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
	public static Tuple opaque(Object o) {return mod(o, ColorTuple.ALPHA, DIR.full, "Opque");}

	@Operator()
	public static class Color extends AbstractOperator {
		public Color(OperatorData opData) {super(opData);}

		@Facet(memUse="FUNCTION", prototype="(Color color)")
		public Tuple argumentParser(String arg) {
			return ColorCache.get(arg);
		}

		@Facet(memUse="FUNCTION", prototype="(Color color)", alias={"map","query"})
		public Tuple query(String arg) {return ColorCache.get(arg);}
	}
}