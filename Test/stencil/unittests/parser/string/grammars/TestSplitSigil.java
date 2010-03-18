/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
	
	private Case[] validateFails = new Case[] {
			new Case(1, "{"),
			new Case(1, "}"),
			new Case(1, "{}"),
			new Case(1, "{0923"),
			new Case(1, "{hello"),
			new Case(1, "}hello"),
			new Case(1, "hello{"),
			new Case(1, "hello}"),
			new Case(1, "he}llo")
	}; 
	
	
	private Case[] passCases = new Case[] {
			new Case(1, "", 			"SIGIL_ARGS"),
			new Case(1, "hello", 		"(SIGIL_ARGS hello)"),
			new Case(1, "test", 		"(SIGIL_ARGS test)"),
			new Case(1, "1", 			"(SIGIL_ARGS 1)"),
			new Case(1, "932", 			"(SIGIL_ARGS 932)"),
			new Case(2, "{[0]}", 		"(SIGIL_ARGS (TUPLE_REF 0))"),
			new Case(2, "{[8640]}", 	"(SIGIL_ARGS (TUPLE_REF 8640))"),
			new Case(2, "{test}", 		"(SIGIL_ARGS (TUPLE_REF test))"),
			new Case(2, "{test[0]}",	"(SIGIL_ARGS (TUPLE_REF test (TUPLE_REF 0)))"),
			new Case(2, "{test[test]}", "(SIGIL_ARGS (TUPLE_REF test (TUPLE_REF test)))"),
			new Case(1, "1+2",			"(SIGIL_ARGS 1+2)"),
			new Case(2, "1+{[2]}", 		"(SIGIL_ARGS 1+ (TUPLE_REF 2))"),
			new Case(2, "txt{ref}", 	"(SIGIL_ARGS txt (TUPLE_REF ref))"),
			new Case(3, "{ref}txt", 	"(SIGIL_ARGS (TUPLE_REF ref) txt)"),
			new Case(4, "{ref}{ref}", 	"(SIGIL_ARGS (TUPLE_REF ref) (TUPLE_REF ref))"),
			new Case(3, "txt{ref}txt", 	"(SIGIL_ARGS txt (TUPLE_REF ref) txt)"),
			new Case(3, "txt {ref} txt","(SIGIL_ARGS txt  (TUPLE_REF ref)  txt)"),
			new Case(4, "{ref}txt{ref}","(SIGIL_ARGS (TUPLE_REF ref) txt (TUPLE_REF ref))"),
			new Case(4, "{r} txt {r}",	"(SIGIL_ARGS (TUPLE_REF r)  txt  (TUPLE_REF r))"),
			new Case(4, "{r} t x t {r}","(SIGIL_ARGS (TUPLE_REF r)  t x t  (TUPLE_REF r))"),
			new Case(1, "all one thing","(SIGIL_ARGS all one thing)")
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
	
	public void testConstruct() throws Exception {
		TreeAdaptor adaptor = new StencilTreeAdapter();
		for (Case c: passCases) {
			Tree t = PrepareCustomArgs.splitArgs(c.text, adaptor);
			assertEquals(c.tree, t.toStringTree());
		}
	}
	
	
}
