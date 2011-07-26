package stencil.unittests.parser.string.grammars;

import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

import junit.framework.TestCase;
import stencil.parser.string.*;
import stencil.parser.tree.StencilTreeAdapter;

public class TestSplitSigil extends TestCase {
	private static class Case { 
		final int parts;
		final String text;
		final String tree;
		public Case(int parts, String text) {this(parts, text, null);}
		public Case(int parts, String text, String tree) {
			this.parts = parts;
			this.text = text;
			this.tree = tree;
		}
	}
	
	//Syntactically invalid
	private Case[] validateFails = new Case[] {
			new Case(1, "{"),			//Unbalanced and empty
			new Case(1, "}"),
			new Case(1, "{}"),			//Empty
			new Case(1, "{0923"),	    //Unbalanced, non-empty
			new Case(1, "{hello"),
			new Case(1, "}hello"),
			new Case(1, "hello{"),
			new Case(1, "hello}"),
			new Case(1, "he}llo")
	}; 
	
	//Semantically invalid
	private Case[] failCases = new Case[] {
			new Case(1, "{0}"),
			new Case(1, "{300}")
	};
	
	
	//Semantically valid
	private Case[] passCases = new Case[] {
			new Case(1, "", 			"LIST_ARGS"),
			new Case(1, "hello", 		"(LIST_ARGS hello)"),
			new Case(1, "test", 		"(LIST_ARGS test)"),
			new Case(1, "1", 			"(LIST_ARGS 1)"),
			new Case(1, "932", 			"(LIST_ARGS 932)"),
			new Case(2, "{test}", 		"(LIST_ARGS (TUPLE_REF test))"),
			new Case(2, "{test.test}",	"(LIST_ARGS (TUPLE_REF test test))"),
			new Case(2, "{test.0}",		"(LIST_ARGS (TUPLE_REF test 0))"),
			new Case(2, "{test.8640}",	"(LIST_ARGS (TUPLE_REF test 8640))"),
			new Case(1, "1+2",			"(LIST_ARGS 1+2)"),
			new Case(2, "1+{test.2}", 	"(LIST_ARGS 1+ (TUPLE_REF test 2))"),
			new Case(2, "txt{ref}", 	"(LIST_ARGS txt (TUPLE_REF ref))"),
			new Case(3, "{ref}txt", 	"(LIST_ARGS (TUPLE_REF ref) txt)"),
			new Case(4, "{ref}{ref}", 	"(LIST_ARGS (TUPLE_REF ref) (TUPLE_REF ref))"),
			new Case(3, "txt{ref}txt", 	"(LIST_ARGS txt (TUPLE_REF ref) txt)"),
			new Case(3, "txt {ref} txt","(LIST_ARGS txt  (TUPLE_REF ref)  txt)"),
			new Case(4, "{ref}txt{ref}","(LIST_ARGS (TUPLE_REF ref) txt (TUPLE_REF ref))"),
			new Case(4, "{r} txt {r}",	"(LIST_ARGS (TUPLE_REF r)  txt  (TUPLE_REF r))"),
			new Case(4, "{r} t x t {r}","(LIST_ARGS (TUPLE_REF r)  t x t  (TUPLE_REF r))"),
			new Case(1, "all one thing","(LIST_ARGS all one thing)")
	};
	
	
	public void testValidate() throws Exception {
		for (Case c: passCases) {
			assertTrue("Validate failed on: " + c.text, c.text.matches(PrepareCustomArgs.VALIDATE_PATTERN));
		}
		for (Case c: validateFails) {
			assertFalse("Validated succeeded on: " + c.text, c.text.matches(PrepareCustomArgs.VALIDATE_PATTERN));
		}
	}
	
	public void testSplit() throws Exception {
		for (Case c: passCases) {
			String[] parts =PrepareCustomArgs.split(c.text);
			assertEquals("Error in case: " + c.text, c.parts, parts.length);
		}
	}
	
	public void testConstructFail() throws Exception {
		final TreeAdaptor adaptor = new StencilTreeAdapter();

		for (int i=0; i< failCases.length; i++) {
			Case c = failCases[i];

			boolean failed = false;
			try {PrepareCustomArgs.splitArgs(c.text, adaptor);}
			catch (Throwable ex) {failed=true;}
			assertTrue("Did not fail when expected: " + c.text, failed);
		}
	}
	
	public void testConstruct() throws Exception {
		final TreeAdaptor adaptor = new StencilTreeAdapter();
		int i=0;
		for (Case c: passCases) {
			Tree t = null;
			
			try {t = PrepareCustomArgs.splitArgs(c.text, adaptor);}
			catch (Throwable ex) {fail("Error parsing " + c.text + "\n" + ex.getMessage()); continue;}
			
			assertEquals("Error in case " +i, c.tree, t.toStringTree());
			i++;
		}
	}
	
	
}
