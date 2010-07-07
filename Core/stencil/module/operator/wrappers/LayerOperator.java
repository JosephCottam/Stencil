package stencil.module.operator.wrappers;

import static java.lang.String.format;

import stencil.display.DisplayLayer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.FacetData;
import stencil.module.util.Modules;
import stencil.module.util.OperatorData;
import static stencil.module.util.OperatorData.TYPE_NA;
import stencil.tuple.prototype.TuplePrototype;

/**Wraps a layer as an operator. 
 * Exposes find, makeOrFind, make and remove.
 * 
 * MakeOrFind is used for Map.
 * Find is used for Query.
 */

//Marked final because it is immutable (however, it has mutable components....)
public final class LayerOperator implements StencilOperator {
	public static final String FIND_FACET = "find";
	private static final String FIND_METHOD = "find";
	
	public static final String REMOVE_FACET = "remove";
	private static final String REMOVE_METHOD = "remove";
	
	private static final String CONTAINS_FACET = "contains";
	private static final String CONTAINS_METHOD = "contains";

	private static final String STATE_ID_METHOD = "getStateID";
	
	protected final DisplayLayer layer;
	protected final OperatorData operatorData;
	
	public LayerOperator(String module, DisplayLayer layer) {
		this.layer = layer;

		TuplePrototype prototype = layer.getPrototype();
		operatorData = Modules.basicOperatorData(module, getName());
		operatorData.addFacet(new FacetData(FIND_FACET, TYPE_NA, false, prototype));
		operatorData.addFacet(new FacetData(MAP_FACET, TYPE_NA, false, prototype));
		operatorData.addFacet(new FacetData(QUERY_FACET, TYPE_NA, false, prototype));
		operatorData.addFacet(new FacetData(REMOVE_FACET, TYPE_NA, false, prototype));
		operatorData.addFacet(new FacetData(CONTAINS_FACET, TYPE_NA, false, prototype));
		operatorData.addFacet(new FacetData(STATE_ID_FACET, TYPE_NA, false, "VALUE"));
	}
	
	public String getName() {return layer.getName();}

	public Invokeable getFacet(String facet) {
		if (StencilOperator.MAP_FACET.equals(facet) 
			|| StencilOperator.QUERY_FACET.equals(facet)
			|| FIND_FACET.equals(facet)) {
			return new ReflectiveInvokeable(FIND_METHOD, layer);
		} else if (CONTAINS_FACET.equals(facet)) {
			return new ReflectiveInvokeable(CONTAINS_METHOD, layer);
		} else if (REMOVE_FACET.equals(facet)) {
			return new ReflectiveInvokeable(REMOVE_METHOD, layer);
		} else if (STATE_ID_FACET.equals(facet)) {
			return new ReflectiveInvokeable(STATE_ID_METHOD, layer);
		}
		throw new IllegalArgumentException(format("Could not create facet for requested name '%1$s'.", facet));
	}

	public OperatorData getOperatorData() {return operatorData;}
	public LayerOperator duplicate() {throw new UnsupportedOperationException();}
}