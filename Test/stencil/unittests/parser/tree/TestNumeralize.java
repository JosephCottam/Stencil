package stencil.unittests.parser.tree;

import static stencil.adapters.java2D.Adapter.ADAPTER;
import stencil.parser.ParseStencil;

public class TestNumeralize extends stencil.unittests.StencilTestCase {

	private final String POINT_LABEL = 
			  "stream Data(value)\n"
			+ "canvas Main\n"
			+ "  guide pointLabels from Display ID\n"
			+ "    TEXT: ID\n"
			+ "layer Display from Data ID: value\n";
	    

	public void testPointLabels() throws Exception {
		ParseStencil.programTree(POINT_LABEL, ADAPTER);
	}
}
