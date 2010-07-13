/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.types.color;

import stencil.tuple.Tuple;
import stencil.tuple.instances.PrototypedTuple;
import stencil.types.Converter;
import stencil.module.operator.util.BasicProject;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;

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

	public static Tuple darker(Object o) {return PrototypedTuple.singleton(validate(o).darker());}
	public static Tuple brighter(Object o) {return PrototypedTuple.singleton(validate(o).brighter());}
	
	public static Tuple getBlue(Object v) {return PrototypedTuple.singleton(validate(v).getBlue());}
	public static Tuple getIntBlue(Object v) {return PrototypedTuple.singleton(validate(v).getBlue());}
	public static Tuple setBlue(Object v, Object o) {return mod(o, ColorTuple.BLUE, v, "SetBlue");}

	public static Tuple getRed(Object v) {return PrototypedTuple.singleton(validate(v).getRed());}
	public static Tuple getIntRed(Object v) {return PrototypedTuple.singleton(validate(v).getRed());}
	public static Tuple setRed(Object v, Object o) {return mod(o, ColorTuple.RED, v, "SetRed");}

	public static Tuple getGreen(Object v) {return PrototypedTuple.singleton(validate(v).getGreen());}
	public static Tuple getIntGreen(Object v) {return PrototypedTuple.singleton(validate(v).getGreen());}
	public static Tuple setGreen(Object v, Object o) {return mod(o, ColorTuple.GREEN, v, "setGreen");}

	public static Tuple getAlpha(Object v) {return PrototypedTuple.singleton(validate(v).getAlpha());}
	public static Tuple getIntAlpha(Object v) {return PrototypedTuple.singleton(validate(v).getAlpha());}
	public static Tuple setAlpha(Object v, Object o) {return mod(o, ColorTuple.ALPHA, v, "setAlpha");}
	public static Tuple opaque(Object o) {return mod(o, ColorTuple.ALPHA, DIR.full, "Opque");}

	public static class Color extends BasicProject {
		public Color(OperatorData opData) {super(opData);}
		public Tuple argumentParser(String arg) {
			return ColorCache.get(arg);
		}
		public Tuple query(String arg) {
			return ColorCache.get(arg);
		}

	}
	
	public ColorUtils(ModuleData md) {super(md);}
}