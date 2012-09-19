package stencil.adapters.java2D.render.guides;

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
import stencil.types.Converter;

public class Title extends Guide2D {
	private static final String[] DEFAULTS_KNOCKOUT = new String[]{"title", "subtitle", "gap"};
	private static final String DEFAULT_SPECIALIZER_SOURCE = "[title: \"\", subtitle:\"\", gap:5]";
	public static final Specializer DEFAULT_SPECIALIZER;
	static {
		try {DEFAULT_SPECIALIZER = ParseStencil.specializer(DEFAULT_SPECIALIZER_SOURCE);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}	

	
	
	private Table data;
	private final Renderer renderer;
	private final String title;
	private final String subtitle;
	private final int gap;
	private final PrototypedTuple updateMask;
	
	public Title(Guide guideDef) {
		super(guideDef);
		
		data = LayerTypeRegistry.makeTable(guideDef.identifier(), "TEXT");
		renderer = LayerTypeRegistry.makeRenderer(data.prototype());
		updateMask = Tuples.merge(SchemaFieldDef.asTuple(data.prototype()), Tuples.delete(DEFAULT_SPECIALIZER, DEFAULTS_KNOCKOUT));

		title = Converter.toString(guideDef.specializer().get("title"));
		subtitle = Converter.toString(guideDef.specializer().get("subtitle"));
		gap = Converter.toInteger(guideDef.specializer().get("gap"));
	}
		

	private final String[] FIELDS = new String[]{"ID", "TEXT", "X","Y", "REGISTRATION"};
	private final Object[] VALUES = new Object[FIELDS.length];	
	
	@Override
	public synchronized void setElements(List<PrototypedTuple> elements, Rectangle2D bounds, AffineTransform viewTransform) {
		data = LayerTypeRegistry.makeTable(identifier(), "TEXT");
		if (elements.size() >0) {
			VALUES[0] = "ID";
			VALUES[1] = title;
			VALUES[2] = bounds.getCenterX();
			VALUES[3] = -(bounds.getMinY()-gap);
			VALUES[4] = "BOTTOM";
			
			PrototypedTuple update = new PrototypedArrayTuple(FIELDS, VALUES);
			PrototypedTuple format = elements.get(0);
			PrototypedTuple m = Tuples.mergeAll(updateMask, format, update);
			data.update(m);
			Table.Util.genChange(data, renderer, viewTransform);
		}		
	}

	@Override
	public Rectangle2D getBoundsReference() {return data.getBoundsReference();}
	
	@Override
	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		renderer.render(data.tenured(), g, viewTransform);
	}
}
