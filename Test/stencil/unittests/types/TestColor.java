package stencil.unittests.types;

import junit.framework.TestCase;
import java.awt.Color;
import java.lang.reflect.*;
import stencil.types.color.ColorCache;


public class TestColor extends TestCase {
	private static final int OTHER = 234;
	private static final int OPAQUE = 255;
	public void testRed() throws Exception {
		Color s;
		Color j;
		
		for (int i=0; i< 256; i++) {
			s = getsColor(i,OTHER,OTHER, OPAQUE);
			j = getjColor(i,OTHER,OTHER, OPAQUE);
			
			assertEquals("Color did not match at red=" + i, j, s);
			assertEquals("Color component did not match", i, s.getRed());
		}
	}

	public void testGreen() throws Exception {
		Color s;
		Color j;
		
		for (int i=0; i< 256; i++) {
			s = getsColor(OTHER,i,OTHER, OPAQUE);
			j = getjColor(OTHER,i,OTHER, OPAQUE);
			
			assertEquals("Color did not match at green=" + i, j, s);
			assertEquals("Color component did not match", i, s.getGreen());
		}
	}

	public void testBlue() throws Exception {
		Color s;
		Color j;
		
		for (int i=0; i< 256; i++) {
			s = getsColor(OTHER, OTHER, i, OPAQUE);
			j = getjColor(OTHER, OTHER, i, OPAQUE);
			
			assertEquals("Color did not match at blue=" + i, j, s);
			assertEquals("Color component did not match", i, s.getBlue());
		}
	}

	public void testAlpha() throws Exception {
		Color s;
		Color j;
		
		for (int i=0; i< 256; i++) {
			s = getsColor(OTHER, OTHER, OTHER, i);
			j = getjColor(OTHER, OTHER, OTHER, i);
			
			assertEquals("Color did not match at alpha=" + i, j, s);
			assertEquals("Color component did not match", i, s.getAlpha());
		}
	}

	public void testjNamed() throws Exception {
		Class colorClass = Color.class;
		Field[] fields = colorClass.getFields();
		
		for (Field field: fields) {
			try {
				Color j = (Color) field.get(null);
				Color s = getsColor(j.getRed(), j.getGreen(), j.getBlue(), j.getAlpha());
				assertEquals("Color match for " + field.getName(), j,s);
			} catch (Exception e) {/*Ignored.*/}
		}
	}
	
	public void testMiscColors() throws Exception {
		Color j;
		Color s;
		
		s = getColor("LIME,150");
		j = new Color(0,255,0, 150);
		
		assertEquals("Lime test failed.", ColorCache.get(j),  s);
	}
	
	public void testTagged() throws Exception {
		Color j;
		Color s;
		
		s = getColor("RGB:10,10,10");
		j = new Color(10,10,10);
		assertEquals(ColorCache.get(j),  s);

		s = getColor("RGB:10,10,10, 255");
		j = new Color(10,10,10,255);
		assertEquals(ColorCache.get(j),  s);

		s = getColor("HSV: 0, .5, .34");
		j = Color.getHSBColor(0f, .5f, .34f);
		assertEquals(ColorCache.get(j),  s);
	}
	
	
	private Color getjColor(int r, int g, int b, int a) {return new Color(r,g,b,a);}
	private Color getColor(String source) throws Exception {
		return ColorCache.get(source);
	}
	private Color getsColor(int r, int g, int b, int a) throws Exception {
		return getColor(colorString(r,g,b,a));
	}
	
	private String colorString(int r, int g, int b, int a) {
		return String.format("%1$s, %2$d, %3$d, %4$d", r, g,b,a);
	}
}
