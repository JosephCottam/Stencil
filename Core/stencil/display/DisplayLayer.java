package stencil.display;

import java.awt.geom.Point2D;

import stencil.interpreter.Viewpoint;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.geometry.RectangleTuple;

public interface DisplayLayer<T extends Glyph> extends Viewpoint<LayerView<T>> {
	/**Specializer key for signaling what type a layer should have.**/
	public static final String TYPE_KEY = "type";
	
	/**Get a view of this layer. A view is a consistent look at the state of a layer.
	 * This method may or may not actually CREATE that consistent state.
	 * If a multi-generation layeer is used, the state may be created using other methods and simply returned with this method.
	 * */
	@Override
	public LayerView<T> viewpoint();
	
	/**Creates a new item in this layer.
	 * The passed tuple has two parts: 
	 * 		The source indicates the rule group that produced it (-1 if not applicable).
	 * 		The values indicate the properties of the new element; this must include an ID field.
	 *  
	 * 
	 * TODO: Modify so the tuple must be be index aligned to the store; may be sparse and return NO_VALUE for fields no explicit value is given for 
	 * @throws IDException Thrown when the ID is not valid for the layer
	 * */
	public void update(PrototypedTuple updates) throws IDException;

	/**Returns the item associated with the name on this layer.
	 * Returns null if no item is associated.*/
	public Glyph find(Comparable ID);
	
	/**Given an ID, remove the associated tuple from the layer*/
	public void remove(Comparable ID);

	/**Remove all current layer conentents.**/
	public void clear();
	

	/**Is the provided ID associated with a tuple on this layer?*/
	public boolean contains(Comparable ID);
	
	/**What is the name of this layer*/
	public String name();

	/**Identifier for the current state**/
	public int stateID();
	
	/**What is the tuple prototype for this layer?
	 * This is derived directly from the implantation type.
	 */
	public TuplePrototype<SchemaFieldDef> prototype();
	public void updatePrototype(TuplePrototype<SchemaFieldDef> defaults);
	
	/**How many data points are in this layer**/
	public int size();	
	
	//TODO: Extend nearest to handle multiple return values
	public Glyph nearest(Point2D p);
	public RectangleTuple bounds();
}
