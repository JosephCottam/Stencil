package stencil.unittests.operator.module;

import stencil.operator.module.provided.Projection;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;
import stencil.types.color.ColorTuple;

import junit.framework.*;


public class TestProjectionOperators extends TestCase {

	public void testHeatScale() throws Exception {
		String red = "@Color{255,0,0}";
		String white = "@Color{255,255,255}";

		java.awt.Color c;

		Specializer s = ParseStencil.parseSpecializer("[1 .. n, cold=\"RED\", hot=\"WHITE\"]");
		
		
		Projection.HeatScale l = new Projection.HeatScale(null, s);	//OperatorData is not required...

		//Test floating ceiling (white)
		for (int i=0; i< 100; i++) {
			c = (java.awt.Color) l.map(Integer.toString(i)).get(0);
			assertEquals("Testing " + i, white, ColorTuple.toString(c));
		}

		//Test floating floor (red)
		l =  new Projection.HeatScale(null, s);
		c = (java.awt.Color) l.map("100").get(0);		
		assertEquals(white,ColorTuple.toString(c));
		
		for (int i=99; i>= 0; i--) {
			c = (java.awt.Color) l.map(Integer.toString(i)).get(0);
			assertEquals("Testing " + i, red, ColorTuple.toString(c));
		}

		//Test mid-ranges
		assertEquals("@Color{255,127,127}", ColorTuple.toString((java.awt.Color) l.map("50").get(0)));
		assertEquals("@Color{255,63,63}", ColorTuple.toString((java.awt.Color) l.map("25").get(0)));
		assertEquals("@Color{255,191,191}", ColorTuple.toString((java.awt.Color) l.map("75").get(0)));


		//Test misc changes
		assertEquals(white, ColorTuple.toString((java.awt.Color) l.map("200").get(0)));
		assertEquals("@Color{255,127,127}", ColorTuple.toString((java.awt.Color) l.map("100").get(0)));
		assertEquals("@Color{255,63,63}", ColorTuple.toString((java.awt.Color) l.map("50").get(0)));
		assertEquals("@Color{255,191,191}", ColorTuple.toString((java.awt.Color) l.map("150").get(0)));
	}


	public void testIndexScale() {
		Projection.Index l = new Projection.Index(null);
		String[] items = new String[] {"Hello", "Why", "What", "@Color{12,39,2}"};
		for (int i=0; i< items.length;i++) {
			assertEquals(new Integer(i), Converter.toInteger(l.map(items[i])));
		}
		for (int i=0; i< items.length;i++) {assertEquals(new Integer(i), Converter.toInteger(l.map(items[i])));}
		for (int i=items.length-1; i>=0;i--) {assertEquals(new Integer(i), Converter.toInteger(l.map(items[i])));}

		assertEquals(Projection.Index.NAME,l.getName());
	}
}
