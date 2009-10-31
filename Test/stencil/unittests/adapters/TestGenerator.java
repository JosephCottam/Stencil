package stencil.unittests.adapters;

import static stencil.unittests.adapters.TestTupleLoader.OVERLAY_SHORT;
import stencil.adapters.Adapter;
import stencil.adapters.TupleLoader;
import stencil.display.StencilPanel;
import stencil.parser.tree.*;
import stencil.parser.string.ParseStencil;
import stencil.testUtilities.StringUtils;
import stencil.util.streams.txt.DelimitedParser;

public abstract class TestGenerator extends junit.framework.TestCase {
	private static final String sourceFile = "./TestData/RegressionImages/SeeTest/SeeTest.stencil"; 

	public void setUp() throws Exception {stencil.Configure.loadProperties("./TestData/Stencil.properties");}
	
	public StencilPanel testGenerate(Adapter adapter) throws Exception {
		String streamRules = StringUtils.getContents(sourceFile);
		Program program = ParseStencil.parse(streamRules, adapter);
		StencilPanel panel = adapter.generate(program);

		assertNotNull("Program not found", panel.getProgram());
		assertEquals("Program not as expected", program, panel.getProgram());

		assertEquals("Layer count mismatch", program.getLayers().size(), panel.getLayers().size());

		for (Layer layer:program.getLayers()) {
			String name = layer.getName();
			assertNotNull("Could not find layer " + name, panel.getLayer(name));
		}

		return panel;
	}

	public TupleLoader testMakeLoader(Adapter adapter) throws Exception {
		String streamRules = StringUtils.getContents(sourceFile);
		Program program = ParseStencil.parse(streamRules, adapter);
		StencilPanel panel = adapter.generate(program);
		DelimitedParser stream = new DelimitedParser("NodeAttributes", "ID|ATT", OVERLAY_SHORT, "\\|", true,1);
		TupleLoader loader = new TupleLoader(panel, stream);

		assertNotNull(loader);
		return loader;
	}
}
