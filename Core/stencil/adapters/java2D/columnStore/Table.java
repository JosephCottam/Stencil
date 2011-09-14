package stencil.adapters.java2D.columnStore;


import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.render.CompoundRenderer;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.DisplayLayer;
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
    
    /**Sentinel value used to indicate a value has been deleted in an 
	 * update, though it still exists in the full store.
	 */
	public static final PrototypedTuple DELETE = new PrototypedTuple() {
		public Object get(String name) throws InvalidNameException {throw new UnsupportedOperationException();}
		public Object get(int idx) throws TupleBoundsException {throw new UnsupportedOperationException();}
		public TuplePrototype prototype() {throw new UnsupportedOperationException();}
		public int size() {throw new UnsupportedOperationException();}		
	};
	
	public static final class Util {
		public static final void genChange(Table table, Renderer renderer, AffineTransform viewTransform) {
			if (table instanceof CompoundTable && renderer instanceof CompoundRenderer) {
				CompoundTable.fullGenChange((CompoundTable) table, (CompoundRenderer)renderer, viewTransform);
			} else if (table instanceof CompoundTable && renderer instanceof CompoundRenderer) {
				throw new IllegalArgumentException("Mismatch between tabel and renderer.");
			} else {
				table.changeGenerations();
				TableShare share = table.viewpoint();
				share.simpleUpdate();
				renderer.calcFields(share, viewTransform);
				table.merge(share);		
			}
		}
	}
} 
