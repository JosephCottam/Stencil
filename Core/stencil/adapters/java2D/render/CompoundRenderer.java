package stencil.adapters.java2D.render;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.adapters.java2D.columnStore.column.ReferenceColumn;
import stencil.adapters.java2D.columnStore.util.ReferenceFieldDef;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.columnStore.util.TupleIterator;
import stencil.adapters.java2D.render.mixins.Placer;
import stencil.display.SchemaFieldDef;
import stencil.tuple.prototype.TuplePrototype;

/** The foundation of the scene graph system is to put multiple glyphs
 * into a logical unit.  The compound renderer expects a schema where 
 * certain fields are actually tables to be rendered.  The root schema
 * also includes position information and encodes the overall size of 
 * all elements in all sub groups.
 * 
 * Compound glyph:
 *    (Element + Element)* -- One table, use the permutation mask (this is an optimization...)
 *    Element + Element*   -- Two tables
 *    
 *    
 * In this schema, the pie chart is an (Shape Element + Slice Element)*
 * and is either (ID X Y shape-fields slice-fields) or a (ID, X, Y (shape) (slice))*
 *     
 * An axis is--
 * Line Element + (line element + text element)*
 * and is thus the schema is (ID X Y (line) ((line) (text))*)
 * and is thus the schema is (ID X Y line... ((line) (text))*)
 * or is thus the schema is (ID X Y line... (line... text...)*)
 * or is thus the schema is (ID X Y line... (line)* (text)*)
 * etc.
 * 
 * where line... means "enough fields to describe a line".
 * 
 * Which form is preferred?  The first seems the simplest...Just keep nesting
 * This class needs to do the renderer type resolution, since it is usually done
 * by the painter class (and this class essentially becomes a painter for its children).
 */

public class CompoundRenderer implements Renderer<TableView> {
	/**Fields used directly by the compound renderer.
	 * This schema is never sufficient by itself.  
	 * It should always be extended with additional table fields.
	 * Table fields must include the table definition as the default value.
	 */
	public static final TuplePrototype<SchemaFieldDef> SCHEMA_BASIS = new TuplePrototype(ID,X,Y,VISIBLE,Z,BOUNDS);
	
	protected final Map<String, RenderColPair> subs = new HashMap();	//Which schema fields need which renderers?
	private static final class RenderColPair {
		final int idx;
		final Renderer renderer;
		public RenderColPair(int idx, Renderer renderer) {
			this.idx = idx;
			this.renderer = renderer;
		}
	}
	

	private final int boundsIdx;
	private final Placer placer;
	
	public CompoundRenderer(TuplePrototype<SchemaFieldDef> schema) {
		final int[] subSchemas = siftSubSchemas(schema);
		final Renderer[] renderers = makeRenderers(schema, subSchemas);
		assert subSchemas.length == renderers.length;
		
		for (int i=0; i< subSchemas.length;i++) {
			int fieldIdx = subSchemas[i];
			subs.put(schema.get(fieldIdx).name(),  new RenderColPair(fieldIdx, renderers[i]));
		}
		
		placer  = Placer.Util.instance(schema, schema.indexOf(X), schema.indexOf(Y));
		boundsIdx = schema.indexOf(BOUNDS.name());
	}
	

	/**Identify the sub-schemas of a schema**/
	private static int[] siftSubSchemas(TuplePrototype<SchemaFieldDef> schema) {
		ArrayList<Integer> subSchemas = new ArrayList();
		for (int i=0; i< schema.size(); i++) {
			SchemaFieldDef def = schema.get(i);
			if (def instanceof ReferenceFieldDef) {subSchemas.add(i);}
		}
		
		int[] places = new int[subSchemas.size()];
		for (int i=0; i<places.length;i++) {
			places[i] = subSchemas.get(i);
		}
		return places;
	}
	
	private static Renderer[] makeRenderers(TuplePrototype<SchemaFieldDef> schema, int[] subSchemas) {
		Renderer[] renderers = new Renderer[subSchemas.length];
		for (int i=0; i<subSchemas.length;i++) {
			ReferenceFieldDef def = (ReferenceFieldDef) schema.get(subSchemas[i]);
			renderers[i] = LayerTypeRegistry.makeRenderer(def.prototype());
		}
		return renderers;
	}


	/**Pre-render calculations for compound tables assumes that the child tables have  already had calcFields run on them.
	 * Otherwise, calcFields would have to be done under a global lock...
	 * TODO: Is there a way to JUST update the bounds (skip the whole share-thing and just do an update to all bounds...)
	 */
	@Override
	public void calcFields(TableShare share, AffineTransform viewTransform) {
//		for (RenderColPair entry: subs.values()) {
//			Renderer rend = entry.renderer;
//			Column col = share.columns()[entry.idx];
//			TableShare subTable = ((ReferenceColumn) col).target().viewpoint();
//			rend.calcFields(subTable);
//		}
//		
		final Rectangle2D[] bounds = new Rectangle2D[share.size()];
		Rectangle2D fullBounds = new Rectangle2D.Double(0,0,-1,-1);
		
		for (StoreTuple glyph: new TupleIterator(share, true)) {
			Rectangle2D bound = new Rectangle(0,0,-1,-1);
			for (RenderColPair entry: subs.values()) {
				List<Comparable> children = (List) glyph.get(entry.idx);
				Column col = share.columns()[entry.idx];
				TableView subTable = ((ReferenceColumn) col).target().tenured();
				for (Comparable id: children) {
					ShapeUtils.add(bound, subTable.find(id).getBoundsReference());
				}
			}
			AffineTransform trans = placer.place(new AffineTransform(), glyph);
			Rectangle2D b =trans.createTransformedShape(bound).getBounds2D();
			bounds[glyph.row()] = b;
			ShapeUtils.add(fullBounds, b);
		}
		
		Column newCol = share.columns()[boundsIdx].replaceAll(bounds);
		share.setColumn(boundsIdx, newCol);
		share.setBounds(fullBounds);
	}


	@Override
	public void render(TableView layer, Graphics2D g, AffineTransform viewTransform) {
		for (StoreTuple glyph: new TupleIterator(layer, layer.renderOrder(),  true)) {
			if (!glyph.isVisible()) {continue;}

			Rectangle2D bounds = glyph.getBoundsReference();
		   if (!(g.hitClip((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight()))) {continue;}

			
			AffineTransform trans = placer.place(new AffineTransform(),glyph);
			g.transform(trans);
			for (RenderColPair entry: subs.values()) {
				Renderer rend = entry.renderer;
				Column col = layer.columns()[entry.idx];
				TableView subTable = ((ReferenceColumn) col).target().tenured();
				List<Comparable> children = (List) glyph.get(entry.idx);

				TableView subset = TableView.mask(subTable, children);
				rend.render(subset, g, g.getTransform());
			}
			g.setTransform(viewTransform);
		}
	}
	
	public Renderer rendererFor(String name) {
		try {return subs.get(name).renderer;}
		catch (NullPointerException ex) {throw new IllegalArgumentException("Could not find renderer for `" + name + "'");}
	}	
}
