package stencil.unittests.parser.tree;

import stencil.parser.tree.StencilTreeAdapter;
import static stencil.parser.string.StencilParser.*;
import stencil.parser.tree.*;
import junit.framework.TestCase;

public class TestValue extends TestCase {
	private static final StencilTreeAdapter adaptor = new StencilTreeAdapter();
	
	public void testRefHashCode() {
		TupleRef ref1 = (TupleRef) adaptor.create(TUPLE_REF, "Ref");
		TupleRef ref2 = (TupleRef) adaptor.create(TUPLE_REF, "Ref");
		
		assertEquals("Hash codes did not match on same text", ref1.hashCode(), ref2.hashCode());
		
		ref2 = (TupleRef) adaptor.create(TUPLE_REF, "Ref2");
		assertEquals("Hash codes did not match on different text", ref1.hashCode(), ref2.hashCode());
		
		adaptor.addChild(ref1, adaptor.create(ID, "id"));
		adaptor.addChild(ref2, adaptor.create(ID, "id"));

		assertEquals("Hash codes did not match on same child", ref1.hashCode(), ref2.hashCode());

		adaptor.addChild(ref2, adaptor.create(NUMBER, "0"));
		assertFalse("Hash codes matched on different child", ref1.hashCode()==ref2.hashCode());		

		adaptor.addChild(ref1, adaptor.create(NUMBER, "0"));
		assertEquals("Hash codes did not match on same children", ref1.hashCode(), ref2.hashCode());

	}
}
