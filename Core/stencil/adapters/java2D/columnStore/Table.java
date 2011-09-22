package stencil.adapters.java2D.columnStore;


import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.render.CompoundRenderer;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.prototype.TuplePrototype;

public interface Table extends DisplayLayer<StoreTuple> {    
    /**Creates a table share that can asynchronously calculate the update (including column merges).
     * 
     * A share is a view that can be edited and merged back in to the table.
     * Any early created view or share can still be read, but no merge-back is possible.
     */
    public TableShare changeGenerations();
    
    /**Takes a table share and merges its updates with the current table.
     * This is mutative on the current table, but does not affect any TableViews.
     * Only consistent shares may be used.
     * 
     * @param col
     * @param update
     */
    public void merge(TableShare share);
            
    /**What is  the internally consistent subset of the contents of this layer?
     * This data can be used anywhere internal consistency is required, such as in the rendering.**/
    public TableView tenured();
    
    @Override
    public TableShare viewpoint();
    
    /**What screen bounds of the components of this table?  
     * This is a reference to the internally held bounds it IS NOT safe to modify or pass outside of the rendering framework.
     * Use bounds if safety may be an issue.
     * */
    public Rectangle2D getBoundsReference();
    
    /**Special tuple that indicates a value should be deleted on the next round.**/
	public static final class DeleteTuple implements PrototypedTuple {
		private final Comparable id;

		private DeleteTuple(Comparable id) {this.id = id;}

		public Object get(int idx) throws TupleBoundsException {
			if (idx ==0) {return id;}
			throw new UnsupportedOperationException();
		}

		public Object get(String name) throws InvalidNameException {throw new UnsupportedOperationException();}
		public TuplePrototype prototype() {throw new UnsupportedOperationException();}
		public int size() {throw new UnsupportedOperationException();}
		

		public static final DeleteTuple with(Comparable id) {return new DeleteTuple(id);}
	}

	
	public static final class Util {
		public static final void genChange(Table table, Renderer renderer, AffineTransform viewTransform) {
			if (table instanceof CompoundTable && renderer instanceof CompoundRenderer) {
				CompoundTable.fullGenChange((CompoundTable) table, (CompoundRenderer)renderer, viewTransform);
			} else if (table instanceof CompoundTable && renderer instanceof CompoundRenderer) {
				throw new IllegalArgumentException("Mismatch between tabel and renderer.");
			} else {
				TableShare share = table.changeGenerations();
				share.simpleUpdate();
				if (renderer != null) {renderer.calcFields(share, viewTransform);}
				table.merge(share);		
			}
		}
		

		/**Find the item nearest point p in the given source.*/
		public static Glyph nearest(Point2D p, Iterable<? extends Glyph> source) {
			double distance = Double.POSITIVE_INFINITY;
			Glyph nearest=null;
			
			for (Glyph g: source) {
				Rectangle2D b = g.getBoundsReference();
				double dist = ShapeUtils.distance(p, b);
				if (dist < distance) {nearest = g; distance=dist;}
			}
			
			return nearest;
		}
	}
} 
