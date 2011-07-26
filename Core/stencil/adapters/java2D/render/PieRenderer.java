package stencil.adapters.java2D.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

import stencil.adapters.general.Registrations.Registration;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.color.ColorCache;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.adapters.java2D.util.TupleAdapter;

public class PieRenderer implements Renderer<TableView> {
	public static final TuplePrototype<SchemaFieldDef> SCHEMA = new TuplePrototype(
				ID,
				X,
				Y,
				new SchemaFieldDef("SLICE", 0d),
				new SchemaFieldDef("FIELD", 1d),
				new SchemaFieldDef("#END_ANGLE", 1d),
				SIZE,
				PEN,
				OPAQUE_PEN_COLOR,
				new SchemaFieldDef("FIELD_COLOR", new java.awt.Color(0,0,0, ColorCache.CLEAR_INT), Paint.class),
				new SchemaFieldDef("SLICE_COLOR", Color.BLACK, Paint.class),
				new SchemaFieldDef("ANGLE", 0.0d),
				VISIBLE,
				Z,
				BOUNDS,
				IMPLANT,
				new SchemaFieldDef("#REG", Registration.CENTER),
				new SchemaFieldDef("#SLICE_PEN", new java.awt.Color(0,0,0, ColorCache.CLEAR_INT), Paint.class),
				new SchemaFieldDef("#SHAPE", StandardShape.ELLIPSE),
				new SchemaFieldDef("#ROTATION", 0d));


	private static final String[][] FIELD_TRANSLATION = new String[][]{{FILL_COLOR.name(), "FIELD_COLOR"}, {HEIGHT.name(), SIZE.name()}, {WIDTH.name(), SIZE.name()}, {"SHAPE", "#SHAPE"}, {ROTATION.name(), "#ROTATION"}, {REGISTRATION.name(), "#REG"}};
	private static final String[][] SLICE_TRANSLATION = new String[][]{{FILL_COLOR.name(), "SLICE_COLOR"}, {"START", "ANGLE"}, {"END", "#END_ANGLE"}, {REGISTRATION.name(), "#REG"}, {"PEN_COLOR", "#SLICE_PEN"}};

	
	private final ShapeRenderer field;
	private final SliceRenderer slice;
	private final TuplePrototype<SchemaFieldDef> fieldSchema;
	private final TuplePrototype<SchemaFieldDef> sliceSchema;
	
	private final int endAngleIdx;
	private final int fieldIdx;
	private final int sliceIdx;
	
    public PieRenderer(TuplePrototype<SchemaFieldDef> schema) {
    	fieldSchema = new TupleAdapter.APrototype(FIELD_TRANSLATION, schema);
    	sliceSchema = new TupleAdapter.APrototype(SLICE_TRANSLATION, schema);
    	
     	field = new ShapeRenderer(fieldSchema);
    	slice = new SliceRenderer(sliceSchema);
    	
    	endAngleIdx = schema.indexOf("#END_ANGLE");
    	fieldIdx = schema.indexOf("FIELD");
    	sliceIdx = schema.indexOf("SLICE");
    }

	@Override
	public void render(TableView layer, Graphics2D g, AffineTransform viewTransform) {
		field.render(layer, g, viewTransform);
		slice.render(layer, g, viewTransform);
	}

	@Override
	public void calcFields(TableShare share) {
		field.calcFields(share);
		double[] percents = new double[share.size()];		
		for (StoreTuple t: new TupleIterator(share, true)) {
			double field = (Double) t.get(fieldIdx);
			double slice = (Double) t.get(sliceIdx);
			percents[t.row()] = (slice/field) * 360;
			
			
		}
		Column newCol = share.columns()[endAngleIdx].replaceAll(percents);
		share.setColumn(endAngleIdx, newCol);
	}
    
    
    
}
