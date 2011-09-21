package stencil.module.operator.wrappers;

import static java.lang.String.format;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;


import stencil.display.Display;
import stencil.display.DisplayLayer;
import stencil.display.LayerView;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.UnknownFacetException;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.FacetData;
import stencil.module.util.Modules;
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
public final class LayerOperator implements StencilOperator<StencilOperator> {
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
		operatorData = Modules.basicOperatorData(module, getName());
		operatorData.addFacet(new FacetData(FIND, MemoryUse.READER, prototype));
		operatorData.addFacet(new FacetData(NEAR, MemoryUse.READER, prototype));
		operatorData.addFacet(new FacetData(BOUNDS, MemoryUse.READER, RectangleTuple.PROTO));
		operatorData.addFacet(new FacetData(MAP_FACET, MemoryUse.WRITER, prototype));
		operatorData.addFacet(new FacetData(QUERY_FACET, MemoryUse.READER, prototype));
		operatorData.addFacet(new FacetData(REMOVE, MemoryUse.WRITER, prototype));
		operatorData.addFacet(new FacetData(CONTAINS, MemoryUse.READER, prototype));
		operatorData.addFacet(new FacetData(STATE_ID, MemoryUse.READER, "VALUE"));
	}
	
	public String getName() {return layer.name();}

	public Invokeable getFacet(String facet) {
		if (StencilOperator.MAP_FACET.equals(facet) 
			|| StencilOperator.QUERY_FACET.equals(facet)) {
			return new ReflectiveInvokeable(FIND, layer);
		} else if (NEAR.equals(facet)) {
			return new ReflectiveInvokeable(facet, this);
		} else if (operatorData.hasFacet(facet)) {
			return new ReflectiveInvokeable(facet, layer);
		} 
		throw new IllegalArgumentException(format("Could not create facet for requested name '%1$s'.", facet));
	}

	public OperatorData getOperatorData() {return operatorData;}
	public LayerViewOperator viewpoint() {return new LayerViewOperator(layer.viewpoint(), this.operatorData);}
	
	public LayerOperator duplicate() {throw new UnsupportedOperationException();}
	
	public Tuple nearest(double x, double y) {
		AffineTransform t = Display.view.viewTransform();
		Point2D p = new Point2D.Double(x,y);
		t.transform(p, p);
		return layer.nearest(p);
	}
	
	
	protected static class LayerViewOperator implements StencilOperator<LayerViewOperator> {
		final LayerView layer;
		final OperatorData operatorData;
		
		public LayerViewOperator(LayerView view, OperatorData od) {this.layer = view; operatorData=od;}
		public StencilOperator duplicate() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Invokeable getFacet(String facet) throws UnknownFacetException {
			if (StencilOperator.MAP_FACET.equals(facet) 
					|| StencilOperator.QUERY_FACET.equals(facet)) {
					return new ReflectiveInvokeable(FIND, layer);
			} else if (NEAR.equals(facet)) {
				return new ReflectiveInvokeable(facet, this);
			} else if (operatorData.hasFacet(facet)) {
				return new ReflectiveInvokeable(facet, layer);
			}
			throw new IllegalArgumentException(format("Could not create facet for requested name '%1$s'.", facet));
		}
		
		public Tuple nearest(double x, double y) {
			AffineTransform t = Display.view.viewTransform();
			Point2D p = new Point2D.Double(x,y);
			t.transform(p, p);
			return layer.nearest(p);
		}

		public String getName() {return layer.getName();}

		public OperatorData getOperatorData() {
			throw new UnsupportedOperationException();
		}

		public LayerViewOperator viewpoint() {return this;}
	}
}