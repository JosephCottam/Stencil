
package stencil.adapters.java2D.data;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.*;

import stencil.adapters.java2D.data.glyphs.Arc;
import stencil.adapters.java2D.data.glyphs.Image;
import stencil.adapters.java2D.data.glyphs.Line;
import stencil.adapters.java2D.data.glyphs.Pie;
import stencil.adapters.java2D.data.glyphs.Poly;
import stencil.adapters.java2D.data.glyphs.Shape;
import stencil.adapters.java2D.data.glyphs.Slice;
import stencil.adapters.java2D.data.glyphs.Text;
import stencil.display.Glyph;
import stencil.display.IDException;
import stencil.display.LayerView;
import stencil.display.DisplayLayer;
import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.Specializer;
import stencil.parser.ParserConstants;
import stencil.parser.string.StencilParser;
import stencil.parser.tree.StencilTree;
import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import static stencil.display.LayerView.DynamicEntry;

/**Layer with two related stores (Permanent and Update) to 
 * allow concurrent editing and iteration.  
 * 
 * The Update store is accumulated until a generation change,
 * then it is moved to the Permanent store when a generation change made.
 */
public class DoubleBufferLayer<T extends Glyph2D> implements DisplayLayer<T> {
	private static final String PROTOTYPE_ID = "PROTOTYPE ID -- IF YOU EVER READ THIS IN OUTPUT, ITS PROBABLY AN ERROR.";
	
	/**Lock around updates to the tenured generation store.  
	 * Any calls that modify the tenured generation should be protected by this lock.
	 * Batch updates should acquire this lock before any updates and release it after all updates.
	 */
	private final Object TENURED_UPDATE_LOCK = new Object(); 
	
	private Map<Object, Integer> index = new HashMap();
	private Map<String, DynamicEntry> sourceData = new HashMap();
	private List<Glyph2D> store = new ArrayList();

	private Map<String, Integer> updateIndex = new HashMap();
	private Map<String, DynamicEntry> updateSources = new HashMap();
	private Rectangle2D updateBounds = null;						//The bounds of the update.   Includes both the original and new if the update modified an existing thing
	private List<Glyph2D> update = new ArrayList();
	
	
	private int storeStateID = 0;
	protected int stateID = 0; 
	
	private final String name;
	private final boolean sortZ;
	
	private T prototype;

	
	
	private DoubleBufferLayer(String name, boolean sortZ) {
		this.name = name;
		this.sortZ = sortZ;
	}

	public String getName() {return name;}
	public TuplePrototype getPrototype() {return prototype.getPrototype();}
	
	public boolean contains(String ID) {
		Integer idx = updateIndex.get(ID);
		return (idx != null && update.get(idx)!=DELETE) 
			   || (!updateIndex.containsKey(ID) && index.containsKey(ID));
	}

	public Glyph find(String ID) {
		Integer idx = updateIndex.get(ID);
		if (idx != null) {return update.get(idx);}
		
		idx= index.get(ID);
		if (idx != null) {return store.get(idx);}
		return null;
	}
	
	public Glyph makeOrFind(Tuple values) {
		Object id = values.get(ParserConstants.SELECTOR_FIELD);
		if (id == null) {throw new IDException(name);}		
		
		String ID = Converter.toString(id);
		Glyph rv = find(ID);
		if (rv != null) {
			update((T) rv.update(values));
		} else {
			rv = make(values);
		}
		return rv;
	}
	
	public Glyph make(Tuple values) throws IDException {
		Glyph2D update = prototype.update(values);
		String ID = update.getID();
		if (ID == null || index.containsKey(ID) || updateIndex.containsKey(ID)) {
			throw new IDException(ID, name);
		}
		
		addToUpdate(ID, update);
		return update;
	}
	
	public void remove(String ID) {addToUpdate(ID, DELETE);}

	public void update(T update) throws IllegalArgumentException {
		String ID = update.getID();
		if (!(index.containsKey(ID) || updateIndex.containsKey(ID))) {
			throw new IllegalArgumentException("Could not update glyph " + ID);
		}
		addToUpdate(ID, update);
	}
	
	private synchronized void addToUpdate(String ID, Glyph2D glyph) {
		stateID++;
		Integer idx = updateIndex.get(ID);
		if (idx != null) {
			update.set(idx, glyph);
		} else {
			idx = update.size();
			updateIndex.put(ID, idx);
			update.add(glyph);
		}			
	}

	public void updatePrototype(Tuple defaults) {prototype = (T) prototype.update(defaults);}
		
	/**Merge the accumulated update into the store.
	 * Returns a view into the updated store.
	 * Prior created views will still work if no changes are actually
	 * made, but cannot be relied on if changes were made.
	 * 
	 * TODO: Calculate the bounds of the store at this time;
	 * 			Add requires a union 
	 * 			Delete requires a full iteration iff the deleted item was on an edge
	 * 			Updates requires a full iteration iff it was on the edge AND it did not move the edge it was on to make things larger
	 * 
	 */
	public synchronized StoreView changeGenerations() {
		// merge generations across
		synchronized(TENURED_UPDATE_LOCK){
			for (String id: updateIndex.keySet()) {
				int updateIDX = updateIndex.get(id);
				Glyph2D value = update.get(updateIDX);
				directUpdate(id, value);
			}
		}
	
		sourceData.putAll(updateSources);

		if (update.size() >0) {storeStateID=stateID;}
		
		// Remove the update
		updateSources.clear();
		updateIndex.clear();
		update.clear();
		
		return viewpoint();
	}
	
	/**Directly change a single glyph in the tenured collection.*/
	private void directUpdate(String id, Glyph2D glyph) {
		Integer storeIDX = index.get(id);
		
		if (glyph == DELETE && storeIDX != null) {
			updateUpdateBounds(store.get(storeIDX).getBoundsReference());
			store.set(storeIDX, DELETE); //HACK: Causes an index leak (in the long run, a very slow memory leak)
			index.remove(id);
		} else if (storeIDX != null) {
			updateUpdateBounds(store.get(storeIDX).getBoundsReference());
			updateUpdateBounds(glyph.getBoundsReference());
			store.set(storeIDX, glyph);
		} else {
			updateUpdateBounds(glyph.getBoundsReference());
			store.add(glyph);
			storeIDX = store.size()-1;
			index.put(id, storeIDX);
		}
	}
	
	private void updateUpdateBounds(Rectangle2D r) {
		if (updateBounds == null) {updateBounds = r.getBounds2D();}
		updateBounds.add(r);
	}
	
	/**For internal use only.
	 * Updates the tenured collection directly to include
	 * the glyphs indicated.  This method is used for dynamic updates
	 * to permit results of such updates to be cached even if new data
	 * is loaded while they are being calculated.   If this method is removed,
	 * find can be simplified to no longer do its own dynamic binding calculation.
	 * 
	 * @param glyph
	 */
	public synchronized void directUpdate(List<Tuple> updates) {
		synchronized (TENURED_UPDATE_LOCK) {
			for (Tuple update: updates) {
				assert update.getPrototype().get(0).getFieldName().equals("ID");
	
				T glyph = (T) find((String) update.get(0));
				T newGlyph = (T) glyph.update(update);
				String id = glyph.getID();
				if (glyph != newGlyph) {directUpdate(id, newGlyph);}
			}
			storeStateID++;
			stateID++;
		}
	}

	public void addDynamic(int groupID, T target, Tuple sourceData) {
		updateSources.put(target.getID(), new DynamicEntry(groupID, sourceData));
	}

	
	private StoreView cachedView;
	@Override
	public StoreView viewpoint() {
		if (cachedView == null || cachedView.creationGeneration !=storeStateID) {
			cachedView = new StoreView(storeStateID);
		}
		return cachedView;
	}

	protected static final Comparator<Glyph2D> Z_SORTER = new Comparator<Glyph2D>() {
		public int compare(Glyph2D o1, Glyph2D o2) {
			return (int) o1.getZ() - (int) o2.getZ();
		}
	};
	
	/**Sentinel class used to indicate a value has been deleted in an 
	 * update, though it still exists in the full store.
	 */
	private static final Glyph2D DELETE = new Glyph2D() {
		public String getID() {return null;}
		public DisplayLayer getLayer() {return null;}
		public double getZ() {return 0;}
		public boolean isVisible() {return false;}
		public Glyph2D update(Tuple t) throws IllegalArgumentException {return null;}
		public Glyph2D updateID(String id) {return null;}
		public Object get(String name) throws InvalidNameException {return null;}
		public Object get(int idx) throws TupleBoundsException {return null;}
		public TuplePrototype getPrototype() {return null;}
		public boolean isDefault(String name, Object value) {return false;}
		public int size() {return 0;}
		public Rectangle2D getBoundsReference() {return null;}
		public void render(Graphics2D g, AffineTransform viewTransform) {}
	};

	/**Class to access the backing store, bypassing the update.
	 * Throw an exception on access if the store state has changed
	 * since this view was created.*/
	public final class StoreView implements LayerView<T> {
		private final int creationGeneration;
		
		//TODO: Might be able to avoid recalculating the bounds calculation by storing it in the layer
		private Rectangle bounds = new Rectangle(0,0,-1,-1); 
		
		public StoreView(int generation) {creationGeneration = generation;}
		
		@Override
		public Iterator iterator() {
			if (storeStateID != creationGeneration) {throw new ConcurrentModificationException();}
			return store.iterator();
		}
		
		@Override
		public Collection renderOrder() {
			if (sortZ) {
				ArrayList sorted = new ArrayList();
				sorted.addAll(store);
				Collections.sort(sorted, Z_SORTER);
				return sorted;
			} else {
				return store;
			}
		}

		@Override
		public Rectangle getBoundsReference() {
			if (bounds.isEmpty() && store.size() >0) {
				bounds.setRect(store.get(0).getBoundsReference());
				for (Glyph2D g: store) {
					bounds.add(g.getBoundsReference());
				}
			}
			return bounds;
		}
		
		@Override
		public T find(String id) {
			if (storeStateID != creationGeneration) {throw new ConcurrentModificationException();}

			T g = null;
			Integer idx = index.get(id);
			if (idx != null) {
				g = (T) store.get(idx);
			}
			return g;
		}
		
		@Override
		public Map<String, DynamicEntry> getSourceData() {return sourceData;}

		@Override
		public int getStateID() {return creationGeneration;}
		
		@Override
		public String getLayerName() {return name;}

		@Override
		public int size() {return index.size();}
	}
		
	protected void updateStateID() {stateID++;} //Occasional missed updates are OK
	public int getStateID() {return stateID;}
	
	private void setPrototype(T prototype) {this.prototype = prototype;}
	
	public static DoubleBufferLayer<?> instance(StencilTree layerDef) {
		String name = layerDef.getText();
		Specializer spec = Freezer.specializer(layerDef.find(StencilParser.SPECIALIZER));
		String layerType = (String) spec.get(TYPE_KEY);
		
		boolean sortZ = false;
		for (StencilTree field: layerDef.findAllDescendants(StencilParser.TUPLE_FIELD_DEF)) {
			String fieldName = field.find(StencilParser.ID).getText();
			if (fieldName.equals("Z")) {sortZ = true; break;}
		}
		
		DoubleBufferLayer layer = new DoubleBufferLayer(name, sortZ);
		Glyph2D prototype = null;
		try {
			if (layerType.equals(Shape.IMPLANTATION)) {
				prototype = new Shape(layer, PROTOTYPE_ID);
			} else if (layerType.equals(Line.IMPLANTATION)) {
				prototype = new Line(layer, PROTOTYPE_ID);
			} else if (layerType.equals(Text.IMPLANTATION)) {
				prototype = new Text(layer, PROTOTYPE_ID);
			} else if (layerType.equals(Pie.IMPLANTATION)) {
				prototype = new Pie(layer, PROTOTYPE_ID);
			} else if (layerType.equals(Image.IMPLANTATION)) {
				prototype = new Image(layer, PROTOTYPE_ID);
			} else if (layerType.equals(Poly.PolyLine.IMPLANTATION)) {
				prototype = new Poly.PolyLine(layer, PROTOTYPE_ID);
			} else if (layerType.equals(Poly.Polygon.IMPLANTATION)) {
				prototype = new Poly.Polygon(layer, PROTOTYPE_ID);
			} else if (layerType.equals(Arc.IMPLANTATION)) {
				prototype = new Arc(layer, PROTOTYPE_ID);
			} else if (layerType.equals(Slice.IMPLANTATION)) {
				prototype = new Slice(layer, PROTOTYPE_ID);
			} 
		} catch (Throwable e) {throw new RuntimeException("Error instantiating table for implantation: " + layerType, e);}
		if (prototype == null) {throw new IllegalArgumentException("Glyph type not know: " + layerType);}
		
		layer.setPrototype(prototype);

		return layer;
	}
}
