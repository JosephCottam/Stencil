package stencil.unittests.interpreter;

import java.util.Arrays;

import stencil.interpreter.tree.Specializer;
import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.parser.tree.StencilTree;
import stencil.util.collections.ArrayUtil;
import static stencil.adapters.java2D.Adapter.ADAPTER;

public class TestTree extends junit.framework.TestCase{
	public void testSpecializerBlend() throws ProgramParseException {
		StencilTree spec1 = ParseStencil.specializerTree("[one:1, two:2, three:3]");
		StencilTree spec2 = ParseStencil.specializerTree("[one:11, four:4, five:5]");
		Specializer blended = Specializer.blend(spec1, spec2);
		
		String[] newKeys = new String[blended.size()];
		ArrayUtil.fromIterator(blended.keySet(), newKeys);

		assertEquals("Key count incorrect after blend: " + Arrays.deepToString(newKeys), 5, blended.size());
		assertEquals("Update value lost in blend.", blended.get("one"), 1);
		
		String[] keys = new String[]{"one","two","three","four","five"};
		assertTrue("Keys incorrect after blend:" + Arrays.deepToString(newKeys), Arrays.deepEquals(keys, newKeys));
	}
	
	public void testStreamStream() throws Exception {
		String input = "stream VertexList(ID1, ID2)\n"
			+ "stream VertexPair(ID1, ID2) from VertexList (ID1, ID2): (ID1, ID2)";
		
		ParseStencil.program(input, ADAPTER);
	}
}
