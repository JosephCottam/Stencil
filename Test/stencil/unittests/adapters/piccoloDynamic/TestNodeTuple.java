package stencil.unittests.adapters.piccoloDynamic;

import stencil.adapters.piccoloDynamic.*;
import stencil.adapters.piccoloDynamic.glyphs.Text;
import junit.framework.TestCase;

public class TestNodeTuple extends TestCase {

	public void testSetAttribute() {
		Text node = new Text("MyID");
		node.setText("Test Text");
		NodeTuple t = new NodeTuple(node);

		for (String name:node.getAttributes()) {
			Object original = t.get(name);

			Object def = t.getNode().getDefault(name);
			if (def == null) {continue;}

			Object newValue = TestGlyphAttributes.testSet(def.getClass())[0];
			if (newValue.equals(original)) {newValue = TestGlyphAttributes.testSet(t.getNode().getDefault(name).getClass())[1];}
			t.set(name, newValue);

			assertEquals("Value mismatch for attribute " + name, newValue, t.get(name));
		}
	}

	public void testEmptyBoundsPosition() {
		Text node = new Text("MyID");
		node.setXPosition(3);
		node.setYPosition(4);

		NodeTuple t = new NodeTuple(node);
		assertEquals("Did not find expected X value", 3.0, t.get("X"));
		assertEquals("Did not find expected Y value", 4.0, t.get("Y"));
	}

	public void testGetAttribute() {
		Text node = new Text("MyID");
		node.setText("test");
		node.addAttribute("Value", "v");
		node.setXPosition(3);
		node.setYPosition(4);

		NodeTuple t = new NodeTuple(node);
		assertEquals("Could not find custom value 'v'", "v", t.get("Value"));
		assertEquals("Did not find expected ID value", "MyID", t.get("ID"));
		assertEquals("Did not find expected X value", 3.0, t.get("X"));
		assertEquals("Did not find expected Y value", 4.0, t.get("Y"));
	}
}
