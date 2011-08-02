package stencil.adapters.java2D;

import stencil.adapters.java2D.columnStore.SimpleTable;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.ImageRenderer;
import stencil.adapters.java2D.render.LineRenderer;
import stencil.adapters.java2D.render.PieRenderer;
import stencil.adapters.java2D.render.PolyRenderer;
import stencil.adapters.java2D.render.Renderer;
import stencil.adapters.java2D.render.ShapeRenderer;
import stencil.adapters.java2D.render.SliceRenderer;
import stencil.adapters.java2D.render.TextRenderer;

import java.util.Map;
import java.util.HashMap;
import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import static stencil.parser.ParserConstants.DYNAMIC_STORE_FIELD;


/**Tracks the known table types and handles instantiation.*/
public class LayerTypeRegistry {
	/**Field a schema uses to identify the glyph type.
	 * To be used with the layer type registry, a schema must include a field with this name.
	 * Is automatically added to schemas when they are registered HOWEVER, this implies that
	 * schema identity cannot be used.
	 * **/
	public static final String TYPE_FIELD = "#TYPE";
	public static final SchemaFieldDef DYNAMIC_FIELD =  new SchemaFieldDef(DYNAMIC_STORE_FIELD, null, Tuple.class);
	
	/**Stores a renderer with the standard prototype.**/
	private static class LayerPair {
		final TuplePrototype<SchemaFieldDef> schema;
		final Class<? extends Renderer> renderer;
		
		public LayerPair(TuplePrototype schema, Class<? extends Renderer> renderer) {
			this.schema = schema;
			this.renderer = renderer;
		}
		
	}
	private static final Map<String, LayerPair> types = new HashMap();
	
	//Load the default layer types
	//TODO: Should the guide layer types be added here?  Maybe under "special" names...
	static {
		register("PIE", PieRenderer.SCHEMA, PieRenderer.class, true);
		register("SLICE", SliceRenderer.SCHEMA, SliceRenderer.class, true);
		register("POLY_POINT", PolyRenderer.SCHEMA, PolyRenderer.class,true);
		register("IMAGE", ImageRenderer.SCHEMA, ImageRenderer.class, true);
		register("SHAPE", ShapeRenderer.SCHEMA, ShapeRenderer.class, true);
		register("TEXT", TextRenderer.SCHEMA, TextRenderer.class, true);
		register("LINE", LineRenderer.LINE_SCHEMA, LineRenderer.class, true);
		register("ARC", LineRenderer.ARC_SCHEMA, LineRenderer.class, true);
	}
	
	
	/**Register an glyph type.
	 * 
	 * @param type       The name of the type
	 * @param schema     The field definitions of the type
	 * @param renderer	 Which renderer class to use with this schema
	 * @param addDynamic Should support for dynamic binding be added to the schema?
	 */
	private static void register(String type, TuplePrototype<SchemaFieldDef> schema, Class<? extends Renderer> renderer, boolean addDynamic) {
		schema = extend(type, schema, addDynamic);
		types.put(type, new LayerPair(schema, renderer));
	}
	
	private static TuplePrototype<SchemaFieldDef> extend(String type, TuplePrototype<SchemaFieldDef> schema, boolean addDynamic) {
		int extendBy = addDynamic ? 2 : 1;
		SchemaFieldDef[] fields = new SchemaFieldDef[schema.size() + extendBy];
		stencil.util.collections.ArrayUtil.fromIterator(schema, fields);
		fields[schema.size()] = new SchemaFieldDef(TYPE_FIELD, type, String.class, true);	//Since layers only have on type of entity in them, this is a constant column
		if (addDynamic) {fields[schema.size()+1] = DYNAMIC_FIELD;}
		return new TuplePrototype<SchemaFieldDef> (fields);
	}
	
	
	/**Get a display layer for this adapter of the given type.**/
	public static Table makeTable(String name, String type) {
		LayerPair pair = types.get(type);
		if (pair == null) {throw new RuntimeException(String.format("Error constructing table '%1$s', type %2$s not known.", name, type));}
		
		return new SimpleTable(name, pair.schema);
	}
	
	public static Renderer makeRenderer(TuplePrototype<SchemaFieldDef> prototype) {
		int idx = prototype.indexOf(TYPE_FIELD);
		if (idx < 0) {throw new RuntimeException("Schemas int he type registry must include the type field: " + TYPE_FIELD);}

		String type = (String) prototype.get(idx).defaultValue();
		LayerPair pair = types.get(type);
		if (pair == null) {throw new RuntimeException(String.format("Error constructing renderer, type %2$s not known.", type));}
		
		try {
			return pair.renderer.getConstructor(TuplePrototype.class).newInstance(prototype);
		}
		catch (Exception e) {throw new RuntimeException("Error constructing renderer of type " + type,  e);}
	}
}
