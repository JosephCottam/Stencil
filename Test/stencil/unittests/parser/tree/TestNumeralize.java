package stencil.unittests.parser.tree;

import static stencil.adapters.java2D.Adapter.ADAPTER;
import stencil.parser.ParseStencil;

public class TestNumeralize extends stencil.unittests.StencilTestCase {

	private final String POINT_LABEL = 
			  "stream Data(value) from Text\n"
			+ "layer Display\n"
			+ "  guide pointLabels from ID\n"
			+ "from Data " +
			"      ID: value"
			+ "    X: value\n";
	    

	public void testPointLabels() throws Exception {
		ParseStencil.programTree(POINT_LABEL, ADAPTER);
	}
}
