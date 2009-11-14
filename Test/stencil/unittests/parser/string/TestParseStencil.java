package stencil.unittests.parser.string;

import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.Map;

import stencil.adapters.Adapter;
import stencil.parser.tree.Program;
import stencil.parser.string.ParseStencil;
import stencil.testUtilities.StringUtils;
import stencil.unittests.operator.module.TestModuleCache;


public class TestParseStencil extends TestCase {
	public static final Adapter ADAPTER = stencil.adapters.java2D.Adapter.INSTANCE;
	
	public void setUp() throws Exception {
		TestModuleCache.initCache();
	}
	
	public void testParse() throws Exception {
		Program p = ParseStencil.parse(StringUtils.getContents("./TestData/RegressionImages/VSM/VSM.stencil"), ADAPTER);
		assertNotNull(p);

		assertEquals(2, p.getLayers().size());
		
		
		p = ParseStencil.parse(StringUtils.getContents("./TestData/RegressionImages/Stocks/Stocks.stencil"), ADAPTER);
		assertNotNull(p);
		
		assertEquals(2, p.getLayers().size());
		assertEquals(3, p.getLayers().get(1).getGroups().size());
		assertEquals(4, p.getLayers().get(1).getGroups().get(1).getGlyphRules().size());
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
				ParseStencil.parse(programs.get(name), ADAPTER);
			}
			catch (Throwable e) {
				errors.add(name);
			}			
		}

		assertEquals("Errors found parsing file(s).", "[]", java.util.Arrays.deepToString(errors.toArray()));

	}
}
