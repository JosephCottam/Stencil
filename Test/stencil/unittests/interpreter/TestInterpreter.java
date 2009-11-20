package stencil.unittests.interpreter;

import static stencil.unittests.adapters.TestTupleLoader.OVERLAY_SHORT;
import stencil.adapters.Adapter;
import stencil.adapters.TupleLoader;
import stencil.display.DisplayLayer;
import stencil.display.StencilPanel;
import stencil.operator.module.ModuleCache;
import stencil.parser.tree.Program;
import stencil.parser.string.ParseStencil;
import stencil.testUtilities.StringUtils;
import stencil.testUtilities.TestModule;
import stencil.tuple.Tuple;
import stencil.util.streams.txt.DelimitedParser;

public abstract class TestInterpreter extends junit.framework.TestCase{
	public static String registerFailRule = "import TestModule " +
											"external stream NodeAttributes(ID, ATT)"+
											"layer Overlay from NodeAttributes" +
											"   filter(ATT =~ \"C\") : ATT" +
											"   ID: FilterFail(ID) -> (VALUE)";

	public void setUp() throws Exception {
		ModuleCache.register(new TestModule());
		stencil.Configure.loadProperties("./TestData/Stencil.properties");
	}

	public void tearDown() throws Exception {ModuleCache.remove("TestModule");}
	
	public void testRegisterFails(Adapter adapter) throws Exception{
		Program program = ParseStencil.parse(registerFailRule, adapter);
		StencilPanel panel = adapter.generate(program);

		DelimitedParser stream = new DelimitedParser("NodeAttributes", "ID|ATT",OVERLAY_SHORT, "\\|", true,0);
		TupleLoader loader = new TupleLoader(panel, stream);
		loader.load();
		DisplayLayer layer = panel.getLayer("Overlay");

		assertEquals("Unexpected number of items loaded.", 90, layer.size());
	}

	public void testSimpleLines(Adapter adapter) throws Exception {
		String source = StringUtils.getContents("./TestData/RegressionImages/SimpleLines/Lines.stencil");

		Program program = ParseStencil.parse(source, adapter);
		StencilPanel panel = adapter.generate(program);
		DelimitedParser stream = new DelimitedParser("LineSource", "graphLabel | axis1A | axis1B | axis2A | axis2B | suite_name | pass | fail", "./TestData/RegressionImages/SimpleLines/18049-arch-compiler.output.txt", "\\s+\\|\\s+", true, 0);

		TupleLoader loader = new TupleLoader(panel, stream);
		loader.load();

		DisplayLayer<Tuple> layer = panel.getLayer("GridLines");
		assertNotNull(layer);
		assertEquals(6, layer.size());

		for (Tuple t: layer) {
			assertEquals(3.0, t.get("STROKE_WEIGHT"));
			assertEquals(new java.awt.Color(.8f,.8f,.8f),t.get("STROKE_COLOR"));
		}
	}
}
