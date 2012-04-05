package stencil.unittests.adapters;

import java.io.File;

import stencil.WorkingDir;
import stencil.adapters.Adapter;
import stencil.adapters.TupleLoader;
import stencil.display.StencilPanel;
import stencil.interpreter.tree.Layer;
import stencil.interpreter.tree.Program;
import stencil.parser.ParseStencil;
import stencil.unittests.StencilTestCase;
import stencil.util.FileUtils;
import stencil.util.streams.txt.DelimitedParser;

public abstract class GeneratorBase extends StencilTestCase {
	private static final String SOURCE_DIR = "./TestData/RegressionImages/SeeTest/";
	private static final String SOURCE_STENCIL = "./SeeTest.stencil";
	private static final String NA_DATA = "./NoData-Comp.txt";
	private File originalWorkingDir;
	
	private StencilPanel panel;
	
	public void setUp() throws Exception {
		super.setUp();
		originalWorkingDir = WorkingDir.get();
		WorkingDir.set(SOURCE_DIR);
	}
	
	public void tearDown() {
		if (panel !=null) {panel.dispose();}
		WorkingDir.set(originalWorkingDir);
	}
	
	public StencilPanel testCompile(Adapter adapter) throws Exception {
		String streamRules = FileUtils.readFile(SOURCE_STENCIL);
		Program program = ParseStencil.program(streamRules, adapter);
		panel = adapter.compile(streamRules);

		assertNotNull("Program not found", panel.getProgram());

		assertEquals("Layer count mismatch", program.layers().length, panel.layers().size());

		for (Layer layer:program.layers()) {
			String name = layer.getName();
			assertNotNull("Could not find layer " + name, panel.getLayer(name));
		}

		return panel;
	}

	public TupleLoader testMakeLoader(Adapter adapter) throws Exception {
		String streamRules = FileUtils.readFile(SOURCE_STENCIL);
		panel = adapter.compile(streamRules);
		DelimitedParser stream = new DelimitedParser("MTTNotApplicable",NA_DATA, "\\s*,\\s*", 4, true,1);
		TupleLoader loader = new TupleLoader(panel, stream);

		assertNotNull(loader);
		return loader;
	}
}
