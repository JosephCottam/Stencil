package stencil.unittests.operator.module;

import stencil.operator.*;
import stencil.operator.module.provided.Projection;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.types.Converter;
import stencil.types.color.ColorTuple;

import junit.framework.*;


public class TestProjectionOperators extends TestCase {

	public void testHeatScale() throws Exception {
		String red = "@color(255,0,0)";
		String white = "@color(255,255,255)";

		java.awt.Color c;

		Specializer s = ParseStencil.parseSpecializer("[1 .. n, cold=@color(RED), hot=@color(WHITE)]");
		
		
		StencilOperator l = new Projection.HeatScale(s);

		//Test floating ceiling (white)
		for (int i=0; i< 100; i++) {
			c = (java.awt.Color) l.map(Integer.toString(i)).get(Tuple.DEFAULT_KEY);
			assertEquals("Testing " + i, white, ColorTuple.toString(c));
		}

		//Test floating floor (red)
		l =  new Projection.HeatScale(s);
		c = (java.awt.Color) l.map("100").get(Tuple.DEFAULT_KEY);		
		assertEquals(white,ColorTuple.toString(c));
		
		for (int i=99; i>= 0; i--) {
			c = (java.awt.Color) l.map(Integer.toString(i)).get(Tuple.DEFAULT_KEY);
			assertEquals("Testing " + i, red, ColorTuple.toString(c));
		}

		//Test mid-ranges
		assertEquals("@color(255,127,127)", ColorTuple.toString((java.awt.Color) l.map("50").get(Tuple.DEFAULT_KEY)));
		assertEquals("@color(255,63,63)", ColorTuple.toString((java.awt.Color) l.map("25").get(Tuple.DEFAULT_KEY)));
		assertEquals("@color(255,191,191)", ColorTuple.toString((java.awt.Color) l.map("75").get(Tuple.DEFAULT_KEY)));


		//Test misc changes
		assertEquals(white, ColorTuple.toString((java.awt.Color) l.map("200").get(Tuple.DEFAULT_KEY)));
		assertEquals("@color(255,127,127)", ColorTuple.toString((java.awt.Color) l.map("100").get(Tuple.DEFAULT_KEY)));
		assertEquals("@color(255,63,63)", ColorTuple.toString((java.awt.Color) l.map("50").get(Tuple.DEFAULT_KEY)));
		assertEquals("@color(255,191,191)", ColorTuple.toString((java.awt.Color) l.map("150").get(Tuple.DEFAULT_KEY)));
	}


	public void testIndexScale() {
		StencilOperator l = new Projection.Index();
		String[] items = new String[] {"Hello", "Why", "What", "@color(12,39,2)"};
		for (int i=0; i< items.length;i++) {
			assertEquals(new Integer(i), Converter.toInteger(l.map(items[i]).get(Tuple.DEFAULT_KEY)));
		}
		for (int i=0; i< items.length;i++) {assertEquals(new Integer(i), Converter.toInteger(l.map(items[i]).get(Tuple.DEFAULT_KEY)));}
		for (int i=items.length-1; i>=0;i--) {assertEquals(new Integer(i), Converter.toInteger(l.map(items[i]).get(Tuple.DEFAULT_KEY)));}

		assertEquals(Projection.Index.NAME,l.getName());
	}
}
