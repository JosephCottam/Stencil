package stencil.unittests.interpreter;

import static stencil.unittests.adapters.TestTupleLoader.OVERLAY_SHORT;

import java.awt.BasicStroke;

import stencil.adapters.Adapter;
import stencil.adapters.TupleLoader;
import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.IDException;
import stencil.display.StencilPanel;
import stencil.module.ModuleCache;
import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Program;
import stencil.testUtilities.StringUtils;
import stencil.testUtilities.TestModule;
import stencil.tuple.Tuple;
import stencil.util.streams.txt.DelimitedParser;

public abstract class InterpreterBase extends junit.framework.TestCase{
	public static String registerFailRule = "import TestModule " +
											"stream NodeAttributes(ID, ATT, Source)"+
											"layer Overlay from NodeAttributes" +
											"   filter(ATT =~ \"C\")" +
											"   ID: FilterFail(ID) -> (VALUE)";

	StencilPanel panel;
	
	public void setUp() throws Exception {
		ModuleCache.register(new TestModule());
		stencil.Configure.loadProperties("./TestData/Stencil.properties");
	}

	public void tearDown() throws Exception {
		ModuleCache.remove("TestModule");
		if (panel != null) {panel.dispose();}
	}

	public void testRegisterFails(Adapter adapter) throws Exception{
		Program program = ParseStencil.program(registerFailRule, adapter);
		panel = adapter.generate(program);
		boolean error =false;
		
		DelimitedParser stream = new DelimitedParser("NodeAttributes", "ID|ATT",OVERLAY_SHORT, "\\|", true,0);
		TupleLoader loader = new TupleLoader(panel, stream);
		try {loader.load();}
		catch (IDException e) {error=true;}
		
		assertTrue("IDException not caught when expected.", error);
	}

	public void testSimpleLines(Adapter adapter) throws Exception {
		String source = StringUtils.getContents("./TestData/RegressionImages/SimpleLines/Lines.stencil");

		Program program = ParseStencil.program(source, adapter);
		panel = adapter.generate(program);
		DelimitedParser stream = new DelimitedParser("LineSource", "graphLabel | axis1A | axis1B | axis2A | axis2B | suite_name | pass | fail", "./TestData/RegressionImages/SimpleLines/18049-arch-compiler.output.txt", "\\s+\\|\\s+", false, 0);

		TupleLoader loader = new TupleLoader(panel, stream);
		loader.load();

		DisplayLayer<Glyph> layer = panel.getLayer("GridLines");
		((DoubleBufferLayer) layer).changeGenerations();
		assertNotNull(layer);
		assertEquals(6, layer.getView().size());

		for (Tuple t: layer.getView()) {
			assertEquals(3.0f, ((BasicStroke) t.get("PEN")).getLineWidth());
			assertEquals(new java.awt.Color(.8f,.8f,.8f), t.get("PEN_COLOR"));
		}
	}
}
