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


import java.awt.Color;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import stencil.parser.tree.Value;
import stencil.types.Converter;
import static stencil.types.color.IntColor.*;
import static stencil.types.color.Color.CLEAR_INT;

public final class NamedColor {
	public static final String SEPARATOR = ",";
	public static final String INITIATOR = "(";
	public static final String TERMINATOR = ")";
	public static final String GRAY_PREFIX = "GRAY";
	public static final Color CLEAR = new Color(CLEAR_INT,CLEAR_INT,CLEAR_INT,CLEAR_INT);

	private static final Map<String, Integer> COLOR_CONSTANTS = new TreeMap<String, Integer>();
	
	static {
		COLOR_CONSTANTS.put("ALICEBLUE",IntColor.colorInt(240,248,255));
		COLOR_CONSTANTS.put("ANTIQUEWHITE",IntColor.colorInt(250,235,215));
		COLOR_CONSTANTS.put("AQUA",IntColor.colorInt(0,255,255));
		COLOR_CONSTANTS.put("AQUAMARINE",IntColor.colorInt(127,255,212));
		COLOR_CONSTANTS.put("AZURE",IntColor.colorInt(240,255,255));
		COLOR_CONSTANTS.put("BEIGE",IntColor.colorInt(245,245,220));
		COLOR_CONSTANTS.put("BISQUE",IntColor.colorInt(255,228,196));
		COLOR_CONSTANTS.put("BLACK",IntColor.colorInt(0,0,0));
		COLOR_CONSTANTS.put("BLANCHEDALMOND",IntColor.colorInt(255,235,205));
		COLOR_CONSTANTS.put("BLUE",IntColor.colorInt(0,0,255));
		COLOR_CONSTANTS.put("BLUEVIOLET",IntColor.colorInt(138,43,226));
		COLOR_CONSTANTS.put("BROWN",IntColor.colorInt(165,42,42));
		COLOR_CONSTANTS.put("BURGESSWOOD",IntColor.colorInt(222,184,135));
		COLOR_CONSTANTS.put("CADETBLUE",IntColor.colorInt(95,158,160));
		COLOR_CONSTANTS.put("CHARTREUSE",IntColor.colorInt(127,255,0));
		COLOR_CONSTANTS.put("CHOCOLATE",IntColor.colorInt(210,105,30));
		COLOR_CONSTANTS.put("CORAL",IntColor.colorInt(255,127,80));
		COLOR_CONSTANTS.put("CORNFLOWERBLUE",IntColor.colorInt(100,149,237));
		COLOR_CONSTANTS.put("CORNSILK",IntColor.colorInt(255,248,220));
		COLOR_CONSTANTS.put("CRIMSON",IntColor.colorInt(220,20,60));
		COLOR_CONSTANTS.put("CYAN",IntColor.colorInt(0,255,255));
		COLOR_CONSTANTS.put("DARKBLUE",IntColor.colorInt(0,0,139));
		COLOR_CONSTANTS.put("DARKCYAN",IntColor.colorInt(0,139,139));
		COLOR_CONSTANTS.put("DARKGOLDENROD",IntColor.colorInt(184,134,11));
		COLOR_CONSTANTS.put("DARKGRAY",IntColor.colorInt(169,169,169));
		COLOR_CONSTANTS.put("DARKGREEN",IntColor.colorInt(0,100,0));
		COLOR_CONSTANTS.put("DARKKHAKI",IntColor.colorInt(189,183,107));
		COLOR_CONSTANTS.put("DARKMAGENTA",IntColor.colorInt(139,0,139));
		COLOR_CONSTANTS.put("DARKOLIVEGREEN",IntColor.colorInt(85,107,47));
		COLOR_CONSTANTS.put("DARKORANGE",IntColor.colorInt(255,140,0));
		COLOR_CONSTANTS.put("DARKORCHID",IntColor.colorInt(153,50,204));
		COLOR_CONSTANTS.put("DARKRED",IntColor.colorInt(139,0,0));
		COLOR_CONSTANTS.put("DARKSALMON",IntColor.colorInt(233,150,122));
		COLOR_CONSTANTS.put("DARKSEAGREEN",IntColor.colorInt(143,188,143));
		COLOR_CONSTANTS.put("DARKSLATEBLUE",IntColor.colorInt(72,61,139));
		COLOR_CONSTANTS.put("DARKSLATEGRAY",IntColor.colorInt(47,79,79));
		COLOR_CONSTANTS.put("DARKTURQUOISE",IntColor.colorInt(0,206,209));
		COLOR_CONSTANTS.put("DARKVIOLET",IntColor.colorInt(148,0,211));
		COLOR_CONSTANTS.put("DEEPPINK",IntColor.colorInt(255,20,147));
		COLOR_CONSTANTS.put("DEEPSKYBLUE",IntColor.colorInt(0,191,255));
		COLOR_CONSTANTS.put("DIMGRAY",IntColor.colorInt(105,105,105));
		COLOR_CONSTANTS.put("DODGERBLUE",IntColor.colorInt(30,144,255));
		COLOR_CONSTANTS.put("FIREBRICK",IntColor.colorInt(178,34,34));
		COLOR_CONSTANTS.put("FLORALWHITE",IntColor.colorInt(255,250,240));
		COLOR_CONSTANTS.put("FORESTGREEN",IntColor.colorInt(34,139,34));
		COLOR_CONSTANTS.put("FUCHSIA",IntColor.colorInt(255,0,255));
		COLOR_CONSTANTS.put("GAINSBORO",IntColor.colorInt(220,220,220));
		COLOR_CONSTANTS.put("GHOSTWHITE",IntColor.colorInt(248,248,255));
		COLOR_CONSTANTS.put("GOLD",IntColor.colorInt(255,215,0));
		COLOR_CONSTANTS.put("GOLDENROD",IntColor.colorInt(218,165,32));
		COLOR_CONSTANTS.put("GRAY",IntColor.colorInt(128,128,128));
		COLOR_CONSTANTS.put("GREEN",IntColor.colorInt(0,128,0));
		COLOR_CONSTANTS.put("GREENYELLOW",IntColor.colorInt(173,255,47));
		COLOR_CONSTANTS.put("HONEYDEW",IntColor.colorInt(240,255,240));
		COLOR_CONSTANTS.put("HOTPINK",IntColor.colorInt(255,105,180));
		COLOR_CONSTANTS.put("INDIANRED",IntColor.colorInt(205,92,92));
		COLOR_CONSTANTS.put("INDIGO",IntColor.colorInt(75,0,130));
		COLOR_CONSTANTS.put("IVORY",IntColor.colorInt(255,255,240));
		COLOR_CONSTANTS.put("KHAKI" ,IntColor.colorInt(240,230,140));
		COLOR_CONSTANTS.put("LAVENDER",IntColor.colorInt(230,230,250));
		COLOR_CONSTANTS.put("LAVENDERBLUSH",IntColor.colorInt(255,240,245));
		COLOR_CONSTANTS.put("LAWNGREEN",IntColor.colorInt(124,252,0));
		COLOR_CONSTANTS.put("LEMONCHIFFON",IntColor.colorInt(255,250,205));
		COLOR_CONSTANTS.put("LIGHTBLUE",IntColor.colorInt(173,216,230));
		COLOR_CONSTANTS.put("LIGHTCORAL",IntColor.colorInt(240,128,128));
		COLOR_CONSTANTS.put("LIGHTCYAN",IntColor.colorInt(224,255,255));
		COLOR_CONSTANTS.put("LIGHTGOLDENRODYELLOW",IntColor.colorInt(250,250,210));
		COLOR_CONSTANTS.put("LIGHTGREEN",IntColor.colorInt(144,238,144));
		COLOR_CONSTANTS.put("LIGHTGREY",IntColor.colorInt(211,211,211));
		COLOR_CONSTANTS.put("LIGHTPINK",IntColor.colorInt(255,182,193));
		COLOR_CONSTANTS.put("LIGHTSALMON",IntColor.colorInt(255,160,122));
		COLOR_CONSTANTS.put("LIGHTSEAGREEN",IntColor.colorInt(32,178,170));
		COLOR_CONSTANTS.put("LIGHTSKYBLUE",IntColor.colorInt(135,206,250));
		COLOR_CONSTANTS.put("LIGHTSLATEGRAY",IntColor.colorInt(119,136,153));
		COLOR_CONSTANTS.put("LIGHTSTEELBLUE",IntColor.colorInt(176,196,222));
		COLOR_CONSTANTS.put("LIGHTYELLOW",IntColor.colorInt(255,255,224));
		COLOR_CONSTANTS.put("LIME",IntColor.colorInt(0,255,0));
		COLOR_CONSTANTS.put("LIMEGREEN",IntColor.colorInt(50,205,50));
		COLOR_CONSTANTS.put("LINEN",IntColor.colorInt(250,240,230));
		COLOR_CONSTANTS.put("MAGENTA",IntColor.colorInt(255,0,255));
		COLOR_CONSTANTS.put("MAROON",IntColor.colorInt(128,0,0));
		COLOR_CONSTANTS.put("MEDIUMAQUAMARINE",IntColor.colorInt(102,205,170));
		COLOR_CONSTANTS.put("MEDIUMBLUE",IntColor.colorInt(0,0,205));
		COLOR_CONSTANTS.put("MEDIUMORCHID",IntColor.colorInt(186,85,211));
		COLOR_CONSTANTS.put("MEDIUMPURPLE",IntColor.colorInt(147,112,219));
		COLOR_CONSTANTS.put("MEDIUMSEAGREEN",IntColor.colorInt(60,179,113));
		COLOR_CONSTANTS.put("MEDIUMSLATEBLUE",IntColor.colorInt(123,104,238));
		COLOR_CONSTANTS.put("MEDIUMSPRINGGREEN",IntColor.colorInt(0,250,154));
		COLOR_CONSTANTS.put("MEDIUMTURQUOISE",IntColor.colorInt(72,209,204));
		COLOR_CONSTANTS.put("MEDIUMVIOLETRED",IntColor.colorInt(199,21,133));
		COLOR_CONSTANTS.put("MIDNIGHTBLUE",IntColor.colorInt(25,25,112));
		COLOR_CONSTANTS.put("MINTCREAM",IntColor.colorInt(245,255,250));
		COLOR_CONSTANTS.put("MISTYROSE",IntColor.colorInt(255,228,225));
		COLOR_CONSTANTS.put("MOCCASIN",IntColor.colorInt(255,228,181));
		COLOR_CONSTANTS.put("NAVAJOWHITE",IntColor.colorInt(255,222,173));
		COLOR_CONSTANTS.put("NAVY",IntColor.colorInt(0,0,128));
		COLOR_CONSTANTS.put("OLDLACE",IntColor.colorInt(253,245,230));
		COLOR_CONSTANTS.put("OLIVE",IntColor.colorInt(128,128,0));
		COLOR_CONSTANTS.put("OLIVEDRAB",IntColor.colorInt(107,142,35));
		COLOR_CONSTANTS.put("ORANGE",IntColor.colorInt(255,165,0));
		COLOR_CONSTANTS.put("ORANGERED",IntColor.colorInt(255,69,0));
		COLOR_CONSTANTS.put("ORCHID",IntColor.colorInt(218,112,214));
		COLOR_CONSTANTS.put("PALEGOLDENROD",IntColor.colorInt(238,232,170));
		COLOR_CONSTANTS.put("PALEGREEN",IntColor.colorInt(152,251,152));
		COLOR_CONSTANTS.put("PALETURQUOISE",IntColor.colorInt(175,238,238));
		COLOR_CONSTANTS.put("PALEVIOLETRED",IntColor.colorInt(219,112,147));
		COLOR_CONSTANTS.put("PAPAYAWHIP",IntColor.colorInt(255,239,213));
		COLOR_CONSTANTS.put("PEACHPUFF",IntColor.colorInt(255,218,185));
		COLOR_CONSTANTS.put("PERU",IntColor.colorInt(205,133,63));
		COLOR_CONSTANTS.put("PINK",IntColor.colorInt(255,192,203));
		COLOR_CONSTANTS.put("PLUM",IntColor.colorInt(221,160,221));
		COLOR_CONSTANTS.put("POWDERBLUE",IntColor.colorInt(176,224,230));
		COLOR_CONSTANTS.put("PURPLE",IntColor.colorInt(128,0,128));
		COLOR_CONSTANTS.put("RED",IntColor.colorInt(255,0,0));
		COLOR_CONSTANTS.put("ROSYBROWN",IntColor.colorInt(188,143,143));
		COLOR_CONSTANTS.put("ROYALBLUE",IntColor.colorInt(65,105,225));
		COLOR_CONSTANTS.put("SADDLEBROWN",IntColor.colorInt(139,69,19));
		COLOR_CONSTANTS.put("SALMON",IntColor.colorInt(250,128,114));
		COLOR_CONSTANTS.put("SANDYBROWN",IntColor.colorInt(244,164,96));
		COLOR_CONSTANTS.put("SEAGREEN",IntColor.colorInt(46,139,87));
		COLOR_CONSTANTS.put("SEASHELL",IntColor.colorInt(255,245,238));
		COLOR_CONSTANTS.put("SIENNA",IntColor.colorInt(160,82,45));
		COLOR_CONSTANTS.put("SILVER",IntColor.colorInt(192,192,192));
		COLOR_CONSTANTS.put("SKYBLUE",IntColor.colorInt(135,206,235));
		COLOR_CONSTANTS.put("SLATEBLUE",IntColor.colorInt(106,90,205));
		COLOR_CONSTANTS.put("SLATEGRAY",IntColor.colorInt(112,128,144));
		COLOR_CONSTANTS.put("SNOW",IntColor.colorInt(255,250,250));
		COLOR_CONSTANTS.put("SPRINGGREEN",IntColor.colorInt(0,255,127));
		COLOR_CONSTANTS.put("STEELBLUE",IntColor.colorInt(70,130,180));
		COLOR_CONSTANTS.put("TAN",IntColor.colorInt(210,180,140));
		COLOR_CONSTANTS.put("TEAL",IntColor.colorInt(0,128,128));
		COLOR_CONSTANTS.put("THISTLE",IntColor.colorInt(216,191,216));
		COLOR_CONSTANTS.put("TOMATO",IntColor.colorInt(255,99,71));
		COLOR_CONSTANTS.put("TURQUOISE",IntColor.colorInt(64,224,208));
		COLOR_CONSTANTS.put("VIOLET",IntColor.colorInt(238,130,238));
		COLOR_CONSTANTS.put("WHEAT",IntColor.colorInt(245,222,179));
		COLOR_CONSTANTS.put("WHITE",IntColor.colorInt(255,255,255));
		COLOR_CONSTANTS.put("WHITESMOKE",IntColor.colorInt(245,245,245));
		COLOR_CONSTANTS.put("YELLOW",IntColor.colorInt(255,255,0));
		COLOR_CONSTANTS.put("YELLOWGREEN",IntColor.colorInt(154,205,50));

		COLOR_CONSTANTS.put("CLEAR", CLEAR.getRGB());
	}

	private NamedColor() {/*Utility class. Not instantiable.*/}
	
	static int create(List<Value> args) {
		String name = Converter.toString(args.get(0)).toUpperCase();
		
		int c;
		//Named color
		if (COLOR_CONSTANTS.containsKey(name)) {c=COLOR_CONSTANTS.get(name);}
		else if (name.startsWith(GRAY_PREFIX)) {
			int value=0;
			String part = name.substring(GRAY_PREFIX.length());

			try {value = Integer.parseInt(part);}
			catch (Exception e) {throw new IllegalArgumentException("Improperly formed gray value (" + name + ").  Format is " + GRAY_PREFIX + "XX where XX is a numer between 0 and 100 (inclusive).");}
			
			if (value > 100 | value <0) {throw new IllegalArgumentException("Gray values must between 0 and 100 (found " + value + " in " + name + ").");}
			float percent = value/100.0f;
			
			int component = ColorUtils.rangeValue(percent);
			
			c= IntColor.colorInt(component, component, component, stencil.types.color.Color.OPAQUE_INT);
		} else {
			throw new IllegalArgumentException("Cannot form a color from " + Arrays.deepToString(args.toArray()));
		}
		
		//Change alpha, if supplied
		if (args.size() !=1) {
			int n = ColorUtils.rangeValue(args.get(1));
			c = modifyAlpha(c, n);
		} 
		return c;
	}
}
