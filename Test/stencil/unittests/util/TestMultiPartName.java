package stencil.unittests.util;

import junit.framework.TestCase;
import stencil.util.MultiPartName;

public class TestMultiPartName extends TestCase {
	public void testByParts() throws Exception {
		MultiPartName n;
		
		n = new MultiPartName("Pre", "Name", "Suf");
		assertEquals("Pre", n.getPrefix());
		assertEquals("Name", n.getName());
		assertEquals("Suf", n.getSuffix());
		assertEquals("Suf", n.getFacet());
		assertEquals("Pre::Name.Suf", n.toString());
		
		n = new MultiPartName("", "Name", "Suf");
		assertEquals("", n.getPrefix());
		assertEquals("Name", n.getName());
		assertEquals("Suf", n.getSuffix());
		assertEquals("Suf", n.getFacet());
		assertEquals("Name.Suf", n.toString());
	}

	public void testSingleString() throws Exception {
		MultiPartName n;
		
		n = new MultiPartName("Pre::Name.Suf");
		assertEquals("Pre", n.getPrefix());
		assertEquals("Name", n.getName());
		assertEquals("Suf", n.getSuffix());
		assertEquals("Suf", n.getFacet());
		assertEquals("Pre::Name.Suf", n.toString());
		
		n = new MultiPartName("Name.Suf");
		assertEquals("", n.getPrefix());
		assertEquals("Name", n.getName());
		assertEquals("Suf", n.getSuffix());
		assertEquals("Suf", n.getFacet());
		assertEquals("Name.Suf", n.toString());
	}


}
