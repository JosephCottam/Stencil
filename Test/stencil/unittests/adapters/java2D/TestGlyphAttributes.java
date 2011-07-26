package stencil.unittests.adapters.java2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.adapters.java2D.Adapter;
import stencil.adapters.java2D.columnStore.Table;
import stencil.display.SchemaFieldDef;
import stencil.interpreter.tree.Specializer;
import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.instances.Singleton;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.unittests.StencilTestCase;
import static stencil.display.DisplayLayer.TYPE_KEY;

//TODO: Add tests for failure cases
public class TestGlyphAttributes extends StencilTestCase {
	private void testAttributes(Table table, String...ignores) throws Exception {
		List<String> ignore = new ArrayList(Arrays.asList(ignores));
		ignore.add("ID");
		
		table.update(Singleton.from("ID","ID"));
		PrototypedTuple node = table.find("ID");
		
		for(SchemaFieldDef att : table.prototype()) {
			if (ignore.contains(att.name())) {continue;} 
			Object value = node.get(att.name());
			Object def = att.defaultValue();
			assertEquals(String.format("Default value not returned for %1$s when expected '%2$s' got '%3$s'", att.name(), def, value), att.defaultValue(), value);
		}
		
		for (String att: TuplePrototypes.getNames(node.prototype())) {
			assertTrue(String.format("Expected to find %1$s in glyph of type %2$s", att, node.get("#TYPE")), node.prototype().contains(att));
		}
	}

	private Specializer spec(String type) throws ProgramParseException {return ParseStencil.specializer(String.format("[%1$s : \"%2$s\"]", TYPE_KEY, type));}
 	
	public void testText() throws Exception {
		testAttributes(Adapter.ADAPTER.makeLayer("MyID", spec("TEXT")));
	}
	
	public void testShape() throws Exception {
		testAttributes(Adapter.ADAPTER.makeLayer("MyID", spec("SHAPE")));
	}

	public void testImage() throws Exception {
		testAttributes(Adapter.ADAPTER.makeLayer("MyID", spec("IMAGE")), "HEIGHT", "WIDTH");
	}

	public void testLine() throws Exception {
		testAttributes(Adapter.ADAPTER.makeLayer("MyID", spec("LINE")), "X", "Y", "HEIGHT", "WIDTH");
	}
	
	public void testPolyLine() throws Exception {
		testAttributes(Adapter.ADAPTER.makeLayer("MyID", spec("POLY_POINT")), "X", "Y", "XS", "YS");
	}
	
	
	public void testPie() throws Exception {
		Adapter.ADAPTER.makeLayer("MyID", spec("PIE"));
	}

}
