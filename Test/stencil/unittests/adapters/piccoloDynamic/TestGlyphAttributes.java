package stencil.unittests.adapters.piccoloDynamic;

import junit.framework.TestCase;
import java.awt.Color;
import java.awt.Paint;
import java.util.EnumSet;

import stencil.adapters.piccoloDynamic.glyphs.*;

//TODO: Add tests for failure cases
public class TestGlyphAttributes extends TestCase{
	public static Double[] doubles = new Double[] {11.0, 3.0, 1.5, 10.25};
	public static Integer[] integers = new Integer[] {11, 3, 33, 10};
	public static String[] strings = new String[] {"one", "two", "three", "four"};
	public static Paint[] colors = new Color[]{Color.RED, Color.CYAN, Color.DARK_GRAY, Color.WHITE, new Color(.33f, .81f, .90f)};


	public static Object[] testSet(Class type) {
		if (type.isEnum()) {return EnumSet.allOf(type).toArray();}

		if (Double.class.isAssignableFrom(type)) {return doubles;}
		if (Integer.class.isAssignableFrom(type)) {return integers;}
		if (String.class.isAssignableFrom(type)) {return strings;}
		if (Paint.class.isAssignableFrom(type)) {return colors;}


		throw new RuntimeException("No value set known for " + type.getName());
	}

	private void testAttributes(Node node) throws Exception {
		String[] attributes =node.getAttributes().toArray(new String[0]);
		testAttributes(node, attributes);
	}

	private void testAttributes(Node node, String[] attributes) throws Exception {
		for(String att : attributes) {
			Object[] testSet = testSet(node.getType(att));
			testAttribute(node, att, testSet);
		}

		checkCustomAttribute(node);
	}

	private void testAttribute(Node node, String attribute, Object[] testSet) {
		if (node.getProvidedAttributes().get(attribute).set.passName) {return;} //Do not test if there is an implicit argument
		node.reset();

		if (node.getDefault(attribute) != null) {
			assertEquals("Error testing default " + attribute, node.getDefault(attribute), node.getAttribute(attribute));
		}

		for (Object value: testSet) {
			node.setAttribute(attribute, value);
			assertEquals("Error testing " + attribute, value, node.getAttribute(attribute));
		}
	}

	public void checkCustomAttribute(Node node) throws Exception {
		String key = "CUSTOM_Property_NAME";
		String value = "Property value";
		assertNull(node.getAttribute(key));


		node.setAttribute(key, value);
		assertTrue("Custom attribute not found after add.", node.getAttributes().contains(key));
		assertEquals("Custom attribute value did not match expected.", value, node.getAttribute(key));
	}

	public void testText() throws Exception {testAttributes(new Text("MyID"));}
	public void testShape() throws Exception {testAttributes(new Shape("MyID"));}
	public void testImage() throws Exception {testAttributes(new Image("MyID"));}
	public void testLine() throws Exception {testAttributes(new Line("MyID"));}
	public void testPolyLine() throws Exception {testAttributes(new AbstractPoly.PolyLine("MyID"));}
	public void testPoly() throws Exception {testAttributes(new AbstractPoly.Poly("MyID"));}
	public void testPie() throws Exception {testAttributes(new Pie("MyID"));}

}
