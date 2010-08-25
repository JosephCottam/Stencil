package stencil.unittests.adapters;

import static stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.Adapter;
import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.StencilPanel;
import stencil.parser.ParseStencil;
import stencil.parser.tree.Program;
import stencil.tuple.Tuple;
import stencil.tuple.instances.PrototypedTuple;

public abstract class LayerBase extends junit.framework.TestCase {
	private static final int TUPLE_COUNT = 100;
	
	private String ruleSources ="stream Stream1(Source, A,B,C) layer Layer1 from Stream1 X: A";
	private StencilPanel panel;

	public void setUp() throws Exception {stencil.Configure.loadProperties("./TestData/Stencil.properties");}
	public void tearDown() {
		if (panel != null) {
			panel.dispose();
			panel = null;
		}
	}
	
	private DisplayLayer loadData(Adapter adapter) throws Exception {
		Program program = ParseStencil.parse(ruleSources, adapter);
		panel= adapter.generate(program);
		DisplayLayer layer = panel.getLayer("Layer1");

		synchronized(panel.visLock) {
			for (int i=0; i<TUPLE_COUNT; i++) {
				Tuple values = new PrototypedTuple(new String[]{"ID", "X","Y","Z"}, new Object[]{Integer.toString(i), i,i,i});
				layer.make(values);
			}
			((DoubleBufferLayer) layer).changeGenerations();
		}
		return layer;
	}

	public void testMake(Adapter adapter) throws Exception {
		Program program = ParseStencil.parse(ruleSources, adapter);
		panel= adapter.generate(program);

		DisplayLayer layer = panel.getLayer("Layer1");

		synchronized(panel.visLock) {
			for (int i=0; i<TUPLE_COUNT; i++) {
				String id = Integer.toString(i);
				assertEquals(i, layer.getView().size());
				Tuple t = layer.make(PrototypedTuple.singleton("ID", id));
				assertEquals(id, t.get(StandardAttribute.ID.name()));
				((DoubleBufferLayer) layer).changeGenerations();
			}
		}

	}

	public DisplayLayer testFind(Adapter gen) throws Exception {
		DisplayLayer layer = loadData(gen);


		//Test them not in linear order
		for (int i=1; i<TUPLE_COUNT; i=i+2) {
			assertNotNull("Could not find item " +i, layer.find(Integer.toString(i)));
			assertNotNull("Could not find item " +(TUPLE_COUNT-i), layer.find(Integer.toString(TUPLE_COUNT-i)));
		}

		assertNull("Item returned when expected null.", layer.find(Integer.toString(-1)));
		assertNull("Item returned when expected null.", layer.find(Integer.toString(1000)));

		return layer;
	}


	public void testRemove(Adapter gen) throws Exception {
		DisplayLayer layer = loadData(gen);
		((DoubleBufferLayer) layer).changeGenerations();
		int expectedSize = layer.getView().size();

		assertEquals(layer.getView().size(), TUPLE_COUNT);
		
		synchronized(panel.visLock) {
			for (int i=0; i< TUPLE_COUNT; i++) {
				Glyph source = layer.find(Integer.toString(i));
				try {
					layer.remove((String) source.get("ID"));
					((DoubleBufferLayer) layer).changeGenerations();
					expectedSize = expectedSize -1;
				} catch (Exception e) {
					fail("Exception on element " + i + ": " + e.getMessage());
				}
	
				assertEquals("Remove did not appear to delete existing tuple", expectedSize, layer.getView().size());
			}
		}
	}
}
