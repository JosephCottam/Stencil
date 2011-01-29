package stencil.unittests.parser.tree;

import stencil.parser.tree.StencilTreeAdapter;
import static stencil.parser.string.StencilParser.*;
import junit.framework.TestCase;

public class TestValue extends TestCase {
	private static final StencilTreeAdapter adaptor = new StencilTreeAdapter();
	
	public void testHashCode() {
		Object ref1 = adaptor.create(TUPLE_REF, "Ref");
		Object ref2 = adaptor.create(TUPLE_REF, "Ref");
		
		assertTrue("Hash codes did not match on same type & text", ref1.hashCode() == ref2.hashCode());
		
		ref2 = adaptor.create(TUPLE_REF, "Ref2");
		assertFalse("Hash codes matched on same type & different text", ref1.hashCode() == ref2.hashCode());
		
		Object v2 = adaptor.create(STRING, "Ref2");
		assertFalse("Hash codes matched on different type & same text", v2.hashCode() == ref2.hashCode());

		
		adaptor.addChild(ref1, adaptor.create(ID, "id"));
		ref2 = adaptor.dupTree(ref1);

		assertTrue("Hash codes did not match on duplicate trees", ref1.hashCode() == ref2.hashCode());

		adaptor.addChild(ref2, adaptor.create(NUMBER, "0"));
		assertFalse("Hash codes matched on different child", ref1.hashCode() == ref2.hashCode());		

		adaptor.addChild(ref1, adaptor.create(NUMBER, "0"));
		assertTrue("Hash codes did not match on matching (but not duplicated) trees", ref1.hashCode() == ref2.hashCode());

	}
}
