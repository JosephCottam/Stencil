package stencil.unittests.util;

import junit.framework.TestCase;
import stencil.interpreter.tree.MultiPartName;

public class TestMultiPartName extends TestCase {
	public void testThreeParts() throws Exception {
		MultiPartName n = new MultiPartName("Pre", "Name", "Suf");
		assertEquals("Pre", n.prefix());
		assertEquals("Name", n.name());
		assertEquals("Suf", n.facet());
		assertEquals("Pre::Name.Suf", n.toString());
	}
	
	public void testNameSuff() throws Exception {
		MultiPartName n = new MultiPartName("", "Name", "Suf");
		assertEquals("", n.prefix());
		assertEquals("Name", n.name());
		assertEquals("Suf", n.facet());
		assertEquals("Name.Suf", n.toString());
	}
	
	public void testName() throws Exception {
		MultiPartName n = new MultiPartName("", "Name");
		assertEquals("", n.prefix());
		assertEquals("Name", n.name());
		assertEquals("Name", n.toString());
	}
	
	public void testPreName() throws Exception {
		MultiPartName n = new MultiPartName("pre", "Name");
		assertEquals("pre", n.prefix());
		assertEquals("Name", n.name());
		assertEquals("pre::Name", n.toString());
	}
}
