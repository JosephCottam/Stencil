package stencil.unittests.module;

import stencil.modules.Projection;
import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;
import stencil.types.color.ColorTuple;

import junit.framework.*;


public class TestProjectionOperators extends TestCase {

	public void testHeatScale() throws Exception {
		String red = "@Color{255,0,0}";
		String white = "@Color{255,255,255}";

		java.awt.Color c;

		Specializer s = ParseStencil.specializer("[cold: \"RED\", hot: \"WHITE\", throw: \"FALSE\"]");
		
		
		Projection.HeatScale l = new Projection.HeatScale(null, s);	//OperatorData is not required...

		//Test floating ceiling (white)
		for (int i=0; i< 100; i++) {
			c = l.map(i);
			assertEquals("Testing " + i, white, ColorTuple.toString(c));
		}

		//Test floating floor (red)
		l =  new Projection.HeatScale(null, s);
		c = l.map(100);		
		assertEquals(white,ColorTuple.toString(c));
		
		for (int i=99; i>= 0; i--) {
			c = l.map(i);
			assertEquals("Testing " + i, red, ColorTuple.toString(c));
		}

		//Test mid-ranges
		assertEquals("@Color{255,127,127}", ColorTuple.toString(l.map(50)));
		assertEquals("@Color{255,63,63}", ColorTuple.toString(l.map(25)));
		assertEquals("@Color{255,191,191}", ColorTuple.toString(l.map(75)));


		//Test misc changes
		assertEquals(white, ColorTuple.toString(l.map(200)));
		assertEquals("@Color{255,127,127}", ColorTuple.toString(l.map(100)));
		assertEquals("@Color{255,63,63}", ColorTuple.toString(l.map(50)));
		assertEquals("@Color{255,191,191}", ColorTuple.toString(l.map(150)));
	}


	public void testIndexScale() {
		Projection.Index l = new Projection.Index(null);
		String[] items = new String[] {"Hello", "Why", "What", "@Color{12,39,2}"};
		for (int i=0; i< items.length;i++) {
			assertEquals(new Integer(i), Converter.toInteger(l.map(items[i])));
		}
		for (int i=0; i< items.length;i++) {assertEquals(new Integer(i), Converter.toInteger(l.map(items[i])));}
		for (int i=items.length-1; i>=0;i--) {assertEquals(new Integer(i), Converter.toInteger(l.map(items[i])));}
	}
}
