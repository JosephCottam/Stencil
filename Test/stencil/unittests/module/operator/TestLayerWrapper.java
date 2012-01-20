 package stencil.unittests.module.operator;

import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.display.DisplayLayer;
import stencil.module.operator.wrappers.LayerOperator;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.unittests.StencilTestCase;

public class TestLayerWrapper extends StencilTestCase {
	public void testMetaData() {
		DisplayLayer l = LayerTypeRegistry.makeTable("Temp", "SHAPE");
		LayerOperator op = new LayerOperator("test", l);
		
		OperatorData od = op.getOperatorData();
		for (String facetName: od.getFacetNames()) {
			FacetData fd = od.getFacet(facetName);
			assertNotNull("Facet listed in facet data, but not found in layer operator", fd);
		}
		
	}
}
