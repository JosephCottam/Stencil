package stencil.unittests.types.color;

import junit.framework.TestCase;
import java.awt.Color;
import java.lang.reflect.*;
import stencil.parser.string.ParseStencil;
import static stencil.parser.ParserConstants.SIGIL;
import stencil.parser.tree.Sigil;


public class TestColor extends TestCase {
	private static final int OTHER = 234;
	private static final int OPAQUE = 255;
	public void testRed() throws Exception {
		Color sigil;
		Color java;
		
		for (int i=0; i< 256; i++) {
			sigil = getSigilColor(i,OTHER,OTHER, OPAQUE);
			java = getJavaColor(i,OTHER,OTHER, OPAQUE);
			
			assertEquals("Color did not match at red=" + i, java, sigil);
			assertEquals("Color component did not match", i, sigil.getRed());
		}
	}

	public void testGreen() throws Exception {
		Color sigil;
		Color java;
		
		for (int i=0; i< 256; i++) {
			sigil = getSigilColor(OTHER,i,OTHER, OPAQUE);
			java = getJavaColor(OTHER,i,OTHER, OPAQUE);
			
			assertEquals("Color did not match at green=" + i, java, sigil);
			assertEquals("Color component did not match", i, sigil.getGreen());
		}
	}

	public void testBlue() throws Exception {
		Color sigil;
		Color java;
		
		for (int i=0; i< 256; i++) {
			sigil = getSigilColor(OTHER, OTHER, i, OPAQUE);
			java = getJavaColor(OTHER, OTHER, i, OPAQUE);
			
			assertEquals("Color did not match at blue=" + i, java, sigil);
			assertEquals("Color component did not match", i, sigil.getBlue());
		}
	}

	public void testAlpha() throws Exception {
		Color sigil;
		Color java;
		
		for (int i=0; i< 256; i++) {
			sigil = getSigilColor(OTHER, OTHER, OTHER, i);
			java = getJavaColor(OTHER, OTHER, OTHER, i);
			
			assertEquals("Color did not match at alpha=" + i, java, sigil);
			assertEquals("Color component did not match", i, sigil.getAlpha());
		}
	}

	public void testJavaNamed() throws Exception {
		Class colorClass = java.awt.Color.class;
		Field[] fields = colorClass.getFields();
		
		for (Field field: fields) {
			try {
				Color j = (java.awt.Color) field.get(null);
				Color s = getSigilColor(j.getRed(), j.getGreen(), j.getBlue(), j.getAlpha());
				assertEquals("Color match for " + field.getName(), j,s);
			} catch (Exception e) {/*Ignored.*/}
		}
	}
	
	public void testMiscColors() throws Exception {
		java.awt.Color java;
		java.awt.Color sigil;
		
		sigil = getSigilColor("@color(LIME,150)");
		java = new java.awt.Color(0,255,0, 150);
		
		assertEquals("Lime test failed.", sigil, java);
	}
	
	private java.awt.Color getJavaColor(int r, int g, int b, int a) {return new Color(r,g,b,a);}
	private java.awt.Color getSigilColor(String source) throws Exception {
		Sigil s = ParseStencil.parseSigil(source);
		assertTrue(s.getValue() instanceof java.awt.Color);
		return (java.awt.Color) s.getValue();		
	}
	private java.awt.Color getSigilColor(int r, int g, int b, int a) throws Exception {
		return getSigilColor(colorString(r,g,b,a));
	}
	
	private String colorString(int r, int g, int b, int a) {
		return String.format("%1$scolor(%2$s, %3$d, %4$d, %5$d)", SIGIL, r, g,b,a);
	}
}
