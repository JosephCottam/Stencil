package stencil.adapters.java2D.render.guides;

import static stencil.parser.ParserConstants.GUIDE_ELEMENT_TAG;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;
import static stencil.parser.ParserConstants.INPUT_FIELD;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.Guide2D;
import stencil.display.SchemaFieldDef;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Specializer;
import stencil.parser.ParseStencil;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.instances.Singleton;
import stencil.types.Converter;


/**Show a set of lines in the body of the plot.**/
public class Gridlines extends Guide2D {
	public static enum AXIS {X,Y}
	private static final String IMPLANT_KEY = "implant";
	
	private Table data;
	private final Renderer renderer;
	private final PrototypedTuple updateMask;

	protected final AXIS axis;
	protected final String axisTag;
	
	private static final String DEFAULT_SPECIALIZER_SOURCE = "[PEN_COLOR: \"GRAY60\"]";
	private static final String[] DEFAULTS_KNOCKOUT = new String[]{};
	public static final Specializer DEFAULT_SPECIALIZER;
	static {
		try {DEFAULT_SPECIALIZER = ParseStencil.specializer(DEFAULT_SPECIALIZER_SOURCE);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}	


	public Gridlines(Guide guideDef) {
		super(guideDef);

		//Which axis is this?
		axisTag = guideDef.selectors().keySet().iterator().next(); 
		axis = AXIS.valueOf(axisTag);
		
		data = LayerTypeRegistry.makeTable(guideDef.identifier(), "LINE");
		renderer = LayerTypeRegistry.makeRenderer(data.prototype());

		PrototypedTuple updateMask = Tuples.merge(SchemaFieldDef.asTuple(data.prototype()), Tuples.delete(DEFAULT_SPECIALIZER, DEFAULTS_KNOCKOUT));
		if (guideDef.specializer().containsKey(IMPLANT_KEY)) {
			updateMask = Tuples.merge(updateMask, Singleton.from("IMPLANT", Converter.toString(guideDef.specializer().get(IMPLANT_KEY))));
		}
		this.updateMask = updateMask;
	}
	
	@Override
	public void setElements(List<PrototypedTuple> elements, Rectangle2D parentBounds, AffineTransform viewTransform) {
		data = LayerTypeRegistry.makeTable(guideDef.identifier(), "LINE");
		if (elements.size() ==0) {return;}
		
		int offset_idx = elements.get(0).prototype().indexOf(GUIDE_ELEMENT_TAG + NAME_SEPARATOR + axisTag);
		int input_idx  = elements.get(0).prototype().indexOf(INPUT_FIELD);

		int idCounter=1;	//Start at 1 so the axis line gets 0
		double lineSize = axis == AXIS.X ? parentBounds.getHeight() : parentBounds.getWidth();
		double floor = axis == AXIS.X ? parentBounds.getMinY() : parentBounds.getMinX();
		for (PrototypedTuple t: elements) {
			double location = Converter.toDouble(t.get(offset_idx));
			
			PrototypedTuple tickParts = makeLine(location, floor, lineSize, idCounter++);
			PrototypedTuple merged = Tuples.mergeAll(updateMask, tickParts, Tuples.delete(t, offset_idx, input_idx));
			
			data.update(merged);
		}
		Table.Util.genChange(data, renderer, viewTransform);
	}

	private static final String[] fields = new String[]{"X1", "Y1", "X2", "Y2", "ID"};
	private PrototypedTuple makeLine(double location, double floor, double size, int id) {
		Object[] values = new Object[fields.length];
		values[values.length-1] = id;
		
		if (axis == AXIS.X) {
			values[0] = location;
			values[1] = -floor;
			values[2] = location;
			values[3] = -(floor+size);
		} else {
			values[0] = floor;
			values[1] = location;
			values[2] = (floor+size);
			values[3] = location;			
		}
		return new PrototypedArrayTuple(fields, values);
	}
	
	@Override
	public Rectangle2D getBoundsReference() {return data.getBoundsReference();}

	@Override
	public void render(Graphics2D g, AffineTransform viewTransform) {
		renderer.render(data.tenured(), g, viewTransform);
	}

}
