package stencil.unittests.module.operator;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ViewpointCache;
import stencil.modules.Projection;
import stencil.unittests.StencilTestCase;
import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
import static stencil.parser.ParserConstants.MAP_FACET;

public class TestViewpointCache extends StencilTestCase {
	private static final Projection PROJECTION = new Projection();
	
	public void testCache() throws Exception {
		StencilOperator op = PROJECTION.instance("Count", null, EMPTY_SPECIALIZER);
		ViewpointCache cache = new ViewpointCache(op);
		
		StencilOperator vp1 = cache.viewpoint();
		assertNotNull("Unexpected null at initial viewpoint.", vp1);
		StencilOperator vp2 = cache.viewpoint();
		assertSame("New viewpoint after no change to operator.", vp1, vp2);
		
		Invokeable inv = op.getFacet(MAP_FACET);
		inv.invoke(new Object[]{});
		vp2 = cache.viewpoint();
		assertNotSame("No new viewpoint after change.", vp1,vp2);
		
		vp1 = cache.viewpoint();
		assertSame("New viewpoint after no change to operator.", vp1, vp2);
	}
}
