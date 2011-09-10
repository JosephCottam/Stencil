package stencil.unittests.parser.string;

import java.util.ArrayList;
import java.util.Map;

import org.antlr.runtime.tree.Tree;

import stencil.parser.ParseStencil;
import stencil.parser.string.StencilParser;
import stencil.parser.tree.StencilTree;
import stencil.testUtilities.StringUtils;
import stencil.unittests.StencilTestCase;
import static stencil.adapters.java2D.Adapter.ADAPTER;
import static stencil.parser.string.StencilParser.*;

public class TestParseStencil extends StencilTestCase {
	public void testParse() throws Exception {
		StencilTree p = ParseStencil.programTree(StringUtils.getContents("./TestData/RegressionImages/VSM/VSM.stencil"), ADAPTER);
		assertNotNull(p);

		assertEquals("Incorrect number of layers found.", 1, p.find(LIST_LAYERS).getChildCount());
		
		
		p = ParseStencil.programTree(StringUtils.getContents("./TestData/RegressionImages/Stocks/Stocks.stencil"), ADAPTER);
		assertNotNull(p);
		
		assertEquals("Incorrect number of layers found.", 2, p.find(LIST_LAYERS).getChildCount());
		assertEquals("Incorrect number of layers groups found.", 3, p.find(LIST_LAYERS).getChild(1).findAllDescendants(CONSUMES).size());
		assertEquals("Incorrect number or defaults found.", 1, p.find(LIST_LAYERS).getChild(1).find(RULES_DEFAULTS).getChildCount());
		assertEquals("Incorrect number or rules found, rule combine likely not performed.", 1, p.find(LIST_LAYERS).getChild(1).findAllDescendants(CONSUMES).get(1).find(RULES_RESULT).getChildCount());
		assertNull("Root synthetic operator found; inline did not identify oportunity", findChild(p.find(LIST_LAYERS).getChild(1).findAllDescendants(CONSUMES).get(1).find(StencilParser.RULES_RESULT), StencilParser.FUNCTION, "Price.map"));
	}

	/**Find a child with the given type and the given content value.
	 * Content value is treated as a string literal (not a pattern or prefix).
	 **/
	private static StencilTree findChild(StencilTree node, int type, String content) {
		for (int i=0; i < node.getChildCount(); i++) {
			StencilTree t = node.getChild(i);
			if (t.getType() == type 
				&& (content == null || content.equals(t.getText()))) {
				return t;
			}
		}
		return null;
	}
	
	public void testParseNull() throws Exception {
		boolean failed=false;

		try {ParseStencil.program(null, ADAPTER);}
		catch (Exception e) {failed = true;}
		finally {if (!failed) {fail("Parser accepted null program, should have thrown an exception.");}}
	}

	public void testParseAllStored() throws Exception {
		ArrayList errors = new ArrayList();
		
		Map<String, String> programs = StringUtils.allPrograms(StringUtils.STENCIL_CACHE_DIRECTORY);
		assertTrue("Insufficient programs found to conduct test.", programs.size() >0);

		for (String name: programs.keySet()) {
			try {
				StencilTree p = ParseStencil.programTree(programs.get(name), ADAPTER);
				ancestryCheck(p);
			}
			catch (Throwable e) {
				errors.add(name +  ":"+ e.getClass().getSimpleName());
				e.printStackTrace();
			}			
		}

		assertEquals("Errors found parsing " + errors.size() + " file(s).", "[]", java.util.Arrays.deepToString(errors.toArray()));
	}
	

	/**Checks that children/parents agree on their relationship.
	 * 
	 * Throws an exception if any root has a child that does not identify the root as its parent.
	 */
	public static final void ancestryCheck(Tree root) {
		for (int i=0; i< root.getChildCount(); i++) {
			Tree child = root.getChild(i);
			if (child.getParent() != root) {throw new AncestryException(child);}
			ancestryCheck(child);
		}
	}


	private static final class AncestryException extends RuntimeException {
		public AncestryException(Tree child) {
			super("Parent/Child mis-match at " + calcPath(child));
		}
		
		private static String calcPath(Tree child) {
			StringBuilder b = new StringBuilder();
			b.append(child.toString());
			Tree parent = child.getParent();
			while (parent != null) {
				b.append(" <- ");
				b.append(parent.getText());
				parent = parent.getParent();
			}
			return b.toString();
		}
	}
	
}
