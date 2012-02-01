package stencil.unittests.adapters;

import stencil.adapters.Adapter;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.StencilPanel;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.instances.Singleton;

public abstract class LayerBase extends junit.framework.TestCase {
	private static final int TUPLE_COUNT = 100;
	
	private String ruleSources ="stream Stream1(Source, A,B,C) from Text layer Layer1 from Stream1 (ID,X,Y,Z) : (Source,A,B,C)";
	private StencilPanel panel;

	public void setUp() throws Exception {stencil.Configure.loadProperties("./TestData/Stencil.properties");}
	public void tearDown() {
		if (panel != null) {
			panel.dispose();
			panel = null;
		}
	}
	
	private DisplayLayer loadData(Adapter adapter) throws Exception {
		panel= adapter.compile(ruleSources);
		DisplayLayer layer = panel.getLayer("Layer1");
		panel.signalStop();

		synchronized(panel.getCanvas().getComponent().visLock) {
			for (int i=0; i<TUPLE_COUNT; i++) {
				PrototypedTuple values = new PrototypedArrayTuple(new String[]{"ID", "X","Y","Z"}, new Object[]{Integer.toString(i), i,i,i});
				layer.update(values);
			}
			Table.Util.genChange(((Table) layer), null, null);
		}
		return layer;
	}

	public void testMake(Adapter adapter) throws Exception {
		panel= adapter.compile(ruleSources);

		DisplayLayer layer = panel.getLayer("Layer1");

		synchronized(panel.getCanvas().getComponent().visLock) {
			for (int i=0; i<TUPLE_COUNT; i++) {
				String id = Integer.toString(i);
				assertEquals(i, layer.size());
				layer.update(Singleton.from("ID", id));
				PrototypedTuple t = layer.find(id);
				assertEquals(id, t.get(Renderer.ID.name()));
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
		TableShare share = ((Table) layer).viewpoint();
		share.simpleUpdate();
		
		int expectedSize = layer.size();
		assertEquals(layer.size(), TUPLE_COUNT);
		
		synchronized(panel.getCanvas().getComponent().visLock) {
			for (int i=0; i< TUPLE_COUNT; i++) {
				Glyph source = layer.find(Integer.toString(i));
				assertNotNull(source);
				assertEquals(source.get(0).toString(), Integer.toString(i));
				try {
					layer.remove((String) source.get("ID"));
					expectedSize = expectedSize -1;
				} catch (Exception e) {
					fail("Exception on element " + i + ": " + e.getMessage());
				}
				
				Table.Util.genChange(((Table) layer), null, null);
	
				assertEquals("Remove did not appear to delete existing tuple", expectedSize, layer.size());
				source = layer.find(Integer.toString(i));
				assertNull("Item " + i + " not deleted when expected.", source);
				
				
				for (int j=i+1; j<TUPLE_COUNT; j++) {
					Glyph alt = layer.find(Integer.toString(j));
					assertNotNull("Premature delete of item " + j + "; gone after delete of " + i, alt);
				}
			}
		}
	}
}
