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
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;

public class PointLabel extends Guide2D {
	private Table data;
	private final Renderer renderer;
	private final PrototypedTuple updateMask;
	
	public PointLabel(Guide guideDef) {
		super(guideDef);
		
		data = LayerTypeRegistry.makeTable(guideDef.identifier(), "TEXT");
		renderer = LayerTypeRegistry.makeRenderer(data.prototype());
		
		updateMask = SchemaFieldDef.asTuple(data.prototype());
	}
		

	private final String[] FIELDS = new String[]{"ID"};
	private final Object[] VALUES = new Object[FIELDS.length];	
	
	@Override
	public synchronized void setElements(List<PrototypedTuple> elements, Rectangle2D bounds, AffineTransform viewTransform) {
		data = LayerTypeRegistry.makeTable(identifier(), "TEXT");
		
		int i=0;
		for (PrototypedTuple t: elements) {
			VALUES[0] = i++;
			PrototypedTuple u = new PrototypedArrayTuple(FIELDS, VALUES);
			PrototypedTuple m = Tuples.mergeAll(updateMask, t,u);
			data.update(m);
		}
		
		Table.Util.genChange(data, renderer, viewTransform);
	}

	public Rectangle2D getBoundsReference() {return data.getBoundsReference();}
	
	public synchronized void render(Graphics2D g, AffineTransform viewTransform) {
		renderer.render(data.tenured(), g, viewTransform);
	}
}
