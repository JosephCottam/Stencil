package stencil.unittests.parser.string;

import java.util.ArrayList;
import java.util.Map;

import org.antlr.runtime.tree.Tree;

import stencil.parser.ParseStencil;
import stencil.parser.tree.Program;
import stencil.testUtilities.StringUtils;
import stencil.unittests.StencilTestCase;
import static stencil.adapters.java2D.Adapter.ADAPTER;

public class TestParseStencil extends StencilTestCase {
	public void testParse() throws Exception {
		Program p = ParseStencil.parse(StringUtils.getContents("./TestData/RegressionImages/VSM/VSM.stencil"), ADAPTER);
		assertNotNull(p);

		assertEquals(2, p.getLayers().size());
		
		
		p = ParseStencil.parse(StringUtils.getContents("./TestData/RegressionImages/Stocks/Stocks.stencil"), ADAPTER);
		assertNotNull(p);
		
		assertEquals(2, p.getLayers().size());
		assertEquals(3, p.getLayers().get(1).getGroups().size());
		assertEquals(4, p.getLayers().get(1).getGroups().get(1).getResultRules().size());
	}

	public void testParseNull() throws Exception {
		boolean failed=false;

		try {ParseStencil.parse(null, ADAPTER);}
		catch (Exception e) {failed = true;}
		finally {if (!failed) {fail("Parser accepted null program, should have thrown an exception.");}}
	}

	public void testParseAllStored() throws Exception {
		ArrayList errors = new ArrayList();
		
		Map<String, String> programs = StringUtils.allPrograms(StringUtils.STENCIL_CACHE_DIRECTORY);
		assertTrue("Insufficient programs found to conduct test.", programs.size() >0);

		for (String name: programs.keySet()) {
			try {
				Program p = ParseStencil.parse(programs.get(name), ADAPTER);
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
