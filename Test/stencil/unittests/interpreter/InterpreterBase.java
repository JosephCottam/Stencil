package stencil.unittests.interpreter;

import static stencil.unittests.adapters.TestTupleLoader.OVERLAY_SHORT;

import java.awt.BasicStroke;

import stencil.adapters.Adapter;
import stencil.adapters.TupleLoader;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.IDException;
import stencil.display.StencilPanel;
import stencil.module.ModuleCache;
import stencil.testUtilities.TestModule;
import stencil.tuple.PrototypedTuple;
import stencil.util.streams.numbers.SequenceStream;
import stencil.util.streams.txt.DelimitedParser;
import stencil.util.FileUtils;

public abstract class InterpreterBase extends junit.framework.TestCase{
	public static String registerFailRule = "import TestModule\n" +
											"stream NodeAttributes(ID, ATT, Source)\n"+
											"layer Overlay from NodeAttributes\n" +
											"   filter(ATT =~ \"C\")\n" +
											"   ID: FilterFail(ID)";

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
		panel = adapter.compile(registerFailRule);
		boolean error =false;
		
		DelimitedParser stream = new DelimitedParser("NodeAttributes", OVERLAY_SHORT, "\\|", 2, true,0);
		TupleLoader loader = new TupleLoader(panel, stream);
		try {loader.load();}
		catch (IDException e) {error=true;}
		
		assertTrue("IDException not caught when expected.", error);
	}

	public void testSimpleLines(Adapter adapter) throws Exception {
		String source = FileUtils.readFile("./TestData/RegressionImages/SimpleLines/Lines.stencil");
		panel = adapter.compile(source);
		
		SequenceStream stream = new SequenceStream("LineSource", 0, 1, 5);

		TupleLoader loader = new TupleLoader(panel, stream);
		loader.load();

		DisplayLayer<? extends Glyph> layer = panel.getLayer("GridLines");
		assertNotNull(layer);
		TableShare share = ((Table) layer).changeGenerations();
		share.simpleUpdate();
		assertEquals(6, share.size());

		for (PrototypedTuple t: layer.viewpoint()) {
			assertEquals(3.0f, ((BasicStroke) t.get("PEN")).getLineWidth());
			assertEquals(new java.awt.Color(.8f,.8f,.8f), t.get("PEN_COLOR"));
		}
	}
}
