package stencil.module.operator.wrappers;


import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


import stencil.display.DisplayLayer;
import stencil.display.LayerView;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.UnknownFacetException;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.geometry.RectangleTuple;

/**Wraps a layer as an operator. 
 * Exposes find, makeOrFind, make and remove.
 * 
 * MakeOrFind is used for Map.
 * Find is used for Query.
 */

//Marked final because it is immutable (however, it has mutable components....)
public class LayerOperator implements StencilOperator<StencilOperator> {
	private static final String FIND = "find";
	private static final String NEAR = "nearest";
	private static final String BOUNDS = "bounds";
	private static final String REMOVE= "remove";
	private static final String CONTAINS = "contains";
	private static final String STATE_ID = "stateID";
	
	protected final DisplayLayer layer;
	protected final OperatorData operatorData;
	
	public LayerOperator(String module, DisplayLayer layer) {
		this.layer = layer;

		TuplePrototype prototype = layer.prototype();
		List<FacetData> facets = new ArrayList();
		facets.add(new FacetData(FIND, MemoryUse.READER, prototype));
		facets.add(new FacetData(NEAR, MemoryUse.READER, prototype));
		facets.add(new FacetData(BOUNDS, MemoryUse.READER, RectangleTuple.PROTO));
		facets.add(new FacetData(REMOVE, MemoryUse.OPAQUE, prototype));
		facets.add(new FacetData(CONTAINS, MemoryUse.READER, prototype));
		facets.add(new FacetData(STATE_ID, MemoryUse.READER, "VALUE"));
		operatorData = new OperatorData(module, getName(), EMPTY_SPECIALIZER, null, FIND, facets);
	}
	
	@Override
	public String getName() {return layer.name();}

	@Override
	public Invokeable getFacet(String facet) throws UnknownFacetException {
		FacetData fd = operatorData.getFacet(facet);	//Throws exception if facet is not found...

		if (fd.name().equals(NEAR)) {
			return new ReflectiveInvokeable(fd.target(), this);
		} else {
			return new ReflectiveInvokeable(fd.target(), layer);
		}
	}

	public DisplayLayer layer() {return layer;}
	@Override
	public OperatorData getOperatorData() {return operatorData;}
	@Override
	public LayerViewOperator viewpoint() {return new LayerViewOperator(layer.viewpoint(), this.operatorData);}
	
	@Override
	public LayerOperator duplicate() {throw new UnsupportedOperationException();}
	
	public Tuple nearest(double x, double y) {
		Point2D p = new Point2D.Double(x,-y);
		return layer.nearest(p);
	}
	
	
	protected static class LayerViewOperator implements StencilOperator<LayerViewOperator> {
		final LayerView layer;
		final OperatorData operatorData;
		
		public LayerViewOperator(LayerView view, OperatorData od) {this.layer = view; operatorData=od;}
		@Override
		public StencilOperator duplicate() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Invokeable getFacet(String facet) throws IllegalArgumentException {
			FacetData fd = operatorData.getFacet(facet);	//Throws exception if facet is not found...
			
			if (fd.name().equals(NEAR)) {
				return new ReflectiveInvokeable(fd.target(), this);
			} else {
				return new ReflectiveInvokeable(fd.target(), layer);
			}
		}

		
		public Tuple nearest(double x, double y) {
			Point2D p = new Point2D.Double(x,-y);
			return layer.nearest(p);
		}

		@Override
		public String getName() {return layer.getName();}

		@Override
		public OperatorData getOperatorData() {throw new UnsupportedOperationException();}

		@Override
		public LayerViewOperator viewpoint() {return this;}
	}
}