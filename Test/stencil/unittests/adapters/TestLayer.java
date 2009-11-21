package stencil.unittests.adapters;

import static stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.Adapter;
import stencil.adapters.Glyph;
import stencil.display.DisplayLayer;
import stencil.display.StencilPanel;
import stencil.parser.tree.Program;
import stencil.parser.string.ParseStencil;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;

public abstract class TestLayer extends junit.framework.TestCase {
	String ruleSources ="external stream Stream1(A,B,C) layer Layer1 from Stream1 X: A";

	public void setUp() throws Exception {stencil.Configure.loadProperties("./TestData/Stencil.properties");}
	
	private DisplayLayer loadData(Adapter adapter) throws Exception {
		Program program = ParseStencil.parse(ruleSources, adapter);
		StencilPanel panel= adapter.generate(program);
		DisplayLayer layer = panel.getLayer("Layer1");

		for (int i=0; i<100; i++) {
			Tuple values = new PrototypedTuple(new String[]{"ID", "X","Y","Z"}, new Object[]{Integer.toString(i), i,i,i});
			layer.make(values);
		}
		return layer;
	}

	public void testMake(Adapter adapter) throws Exception {
		Program program = ParseStencil.parse(ruleSources, adapter);
		StencilPanel panel= adapter.generate(program);

		DisplayLayer layer = panel.getLayer("Layer1");

		for (int i=0; i<100; i++) {
			String id = Integer.toString(i);
			assertEquals(i, layer.size());
			Tuple t = layer.make(PrototypedTuple.singleton("ID", id));
			assertEquals(id, t.get(StandardAttribute.ID.name()));
		}
	}

	public DisplayLayer testFind(Adapter gen) throws Exception {
		DisplayLayer layer = loadData(gen);


		//Test them not in linear order
		for (int i=1; i<100; i=i+2) {
			assertNotNull("Could not find item " +i, layer.find(Integer.toString(i)));
			assertNotNull("Could not find item " +(100-i), layer.find(Integer.toString(100-i)));
		}

		assertNull("Item returned when expected null.", layer.find(Integer.toString(-1)));
		assertNull("Item returned when expected null.", layer.find(Integer.toString(1000)));

		return layer;
	}


	public void testRemove(Adapter gen) throws Exception {
		DisplayLayer layer = loadData(gen);
		int expectedSize = layer.size();

		for (int i=0; i< 100; i++) {
			Glyph source = layer.find(Integer.toString(i));
			layer.remove((String) source.get("ID"));
			expectedSize = expectedSize -1;

			assertEquals("Remove did not appear to delete existing tuple", expectedSize, layer.size());
		}
	}




}
