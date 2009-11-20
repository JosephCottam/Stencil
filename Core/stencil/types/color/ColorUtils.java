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

import stencil.tuple.BasicTuple;
import stencil.tuple.Tuple;
import stencil.types.Converter;
import stencil.operator.module.ModuleData;
import stencil.operator.module.util.BasicModule;
import stencil.parser.tree.Value;

public final class ColorUtils extends BasicModule {
	//TODO: Somehow tie these utilities to the color type instantiated.
	private static final stencil.types.color.Color colorType = new stencil.types.color.Color();
	
	private enum DIR {up, down, full, none};
	private enum COMP {Red, Blue, Green, Alpha}

	static Integer rangeValue(Value value) {
		Integer v;
		if (value.isNumber()) {
			Number n = (Number) value.getValue();
			if (n instanceof Integer) {
				v = (Integer) n;
			} else {
				v = rangeValue(((Double) n).floatValue());
			}
		} else {
			throw new RuntimeException("Recived non-number to 'rangeValue'");
		}
		return v;
	}
		
	/**Convert a float to be in the proper integer range.*/
	static final Integer rangeValue(float f) {
		if (f > 1) {f=1;}
		if (f < 0) {f=0;}
		return Math.round(f*255);
	}
	
	private static ColorTuple validate(Object o) {
		if (o instanceof ColorTuple) {return (ColorTuple) o;}
		return (ColorTuple) colorType.convert(o, ColorTuple.class);
	}

	private static Tuple mod(Object source, ColorUtils.COMP comp, Object v, String name) {
		ColorTuple color = validate(source);
		
		Number value = Converter.toNumber(v);
		if (value instanceof Float) {value = rangeValue((Float) value);}
		else if (value instanceof Double) {value = rangeValue(((Double) value).floatValue());}
		
		return BasicTuple.singleton(color.modify(comp.name(), value)); //TODO: Change so just the color tuple is returned
	}

	private static Tuple mod(Object source, ColorUtils.COMP comp, ColorUtils.DIR dir, String name) {
		ColorTuple color = validate(source);
		float value;
		float[] values =color.getComponents(null);
		value = values[comp.ordinal()];

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

	public static Tuple darker(Object o) {return BasicTuple.singleton(validate(o).darker());}
	public static Tuple brighter(Object o) {return BasicTuple.singleton(validate(o).brighter());}
	
	public static Tuple getBlue(Object v) {return BasicTuple.singleton(validate(v).getBlue());}
	public static Tuple getIntBlue(Object v) {return BasicTuple.singleton(validate(v).getBlue());}
	public static Tuple setBlue(Object v, Object o) {return mod(o, COMP.Blue, v, "SetBlue");}

	public static Tuple getRed(Object v) {return BasicTuple.singleton(validate(v).getRed());}
	public static Tuple getIntRed(Object v) {return BasicTuple.singleton(validate(v).getRed());}
	public static Tuple setRed(Object v, Object o) {return mod(o, COMP.Red, v, "SetRed");}

	public static Tuple getGreen(Object v) {return BasicTuple.singleton(validate(v).getGreen());}
	public static Tuple getIntGreen(Object v) {return BasicTuple.singleton(validate(v).getGreen());}
	public static Tuple setGreen(Object v, Object o) {return mod(o, COMP.Green, v, "setGreen");}

	public static Tuple getAlpha(Object v) {return BasicTuple.singleton(validate(v).getAlpha());}
	public static Tuple getIntAlpha(Object v) {return BasicTuple.singleton(validate(v).getAlpha());}
	public static Tuple setAlpha(Object v, Object o) {return mod(o, COMP.Alpha, v, "setAlpha");}
	public static Tuple opaque(Object o) {return mod(o, COMP.Alpha, DIR.full, "Opque");}

	
	public ColorUtils(ModuleData md) {super(md);}
}