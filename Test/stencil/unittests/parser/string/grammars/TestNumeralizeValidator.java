package stencil.unittests.parser.string.grammars;

import junit.framework.TestCase;
import stencil.parser.string.validators.*;
import stencil.parser.string.ValidationException;
import stencil.parser.tree.StencilTree;
import static stencil.parser.ParseStencil.*;
import static stencil.parser.string.StencilParser.*;

public class TestNumeralizeValidator extends TestCase {
	public void testValid() throws Exception {
		for (int i=0; i<10; i++) {
			StencilTree root = (StencilTree) TREE_ADAPTOR.create(TUPLE_REF, "");
			for (int j=0; j<i; j++) {
				TREE_ADAPTOR.addChild(root, TREE_ADAPTOR.create(NUMBER, Integer.toString(i)));
			}
			try {FullNumeralize.apply(root);}
			catch (ValidationException e) {
				String message = String.format("Tree of length %1$d did not validate: %2$s", i, e.getMessage());
				fail(message);
			}
		}
	}

	
	public void testInvalidID() throws Exception {testAll(ID, "X");}
	public void testInvalidAll() throws Exception {testAll(ALL, "ALL");}
	
	private static void testAll(int token, String value) {
		for (int i=0; i< 10; i++) {
			StencilTree root = makeTree(i, token, value);
			test(root, i);
		}		
	}
	
	
	private static void test(StencilTree candidate, int i) {
		boolean failed = false;
		try {FullNumeralize.apply(candidate);}
		catch (ValidationException e) {failed = true;}
		String message = String.format("Failed to detect non-numeralization in %1$s", candidate.toStringTree());
		assertTrue(message, failed);		
	}
	
	private static StencilTree makeTree(int i, int token, String value) {
		StencilTree root = (StencilTree) TREE_ADAPTOR.create(TUPLE_REF, "TUPLE_REF");
		for (int j=0; j<10; j++) {
			TREE_ADAPTOR.addChild(root, TREE_ADAPTOR.create(NUMBER, Integer.toString(i)));
		}
		TREE_ADAPTOR.setChild(root, i, TREE_ADAPTOR.create(token, value));
		return root;
	}
}
