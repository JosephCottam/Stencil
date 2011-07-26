package stencil.adapters.java2D.render;

import stencil.adapters.general.Shapes.StandardShape;
import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.adapters.java2D.render.mixins.*;


public class ShapeRenderer extends AbstractShapeRenderer {
	/**Basic expected table schema.**/
	public static final TuplePrototype<SchemaFieldDef> SCHEMA = new TuplePrototype(
				ID,
				new SchemaFieldDef("SHAPE", StandardShape.ELLIPSE),
				new SchemaFieldDef("ROTATION", 0d),
				new SchemaFieldDef(SIZE.name(), -1d), //-1 is the sentinel value that says use width and height instead
				new SchemaFieldDef(WIDTH.name(), 5d),
				new SchemaFieldDef(HEIGHT.name(), 5d),
				X,
				Y,
				IMPLANT,
				FILL_COLOR,
				PEN_COLOR,
				PEN,
				VISIBLE,
				REGISTRATION,
				Z,
				BOUNDS);

    public ShapeRenderer(TuplePrototype<SchemaFieldDef> schema) {
    	super( Colorer.Util.instance(schema, schema.indexOf(FILL_COLOR)),
    		   Colorer.Util.instance(schema, schema.indexOf(PEN_COLOR)),
	           Stroker.Util.instance(schema, schema.indexOf(PEN), schema.indexOf(PEN_COLOR)),
	           Implanter.Util.instance(schema, schema.indexOf(IMPLANT)),
	           Shaper.Util.instance(schema, schema.indexOf("SHAPE"), schema.indexOf(WIDTH), schema.indexOf(HEIGHT), schema.indexOf(SIZE)),
	           Placer.Util.instance(schema, schema.indexOf(X), schema.indexOf(Y)),
	           Registerer.Util.instance(schema, schema.indexOf(REGISTRATION)),
	           Rotater.Util.instance(schema, schema.indexOf(ROTATION)),
	           schema.indexOf(BOUNDS));
    }
}
