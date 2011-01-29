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
package stencil.unittests.module.operator;

import stencil.module.Module;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.wrappers.InvokeableOperator;
import stencil.modules.Numerics;
import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Specializer;
import stencil.unittests.StencilTestCase;

public class TestNumerics extends StencilTestCase {
	final Module numerics;
	
	public TestNumerics() throws Exception {
		numerics = new Numerics();
	}

	public void testLog() throws Exception {
		Specializer spec10 = ParseStencil.specializer("[base: 10, range: LAST]");
		Specializer specE = ParseStencil.specializer("[base: \"e\", range: LAST]");
		Specializer spec2 = ParseStencil.specializer("[base: 2, range: LAST]");
		Specializer specNone = ParseStencil.specializer("[base: NULL, range: LAST]");
		
		StencilOperator log10 = numerics.instance("Log", null, spec10);
		StencilOperator logE = numerics.instance("Log", null, specE);
		StencilOperator log2 = numerics.instance("Log", null, spec2);
		StencilOperator logNone = numerics.instance("Log", null, specNone);
		
		assertEquals(InvokeableOperator.class, log10.getClass());
		assertEquals(InvokeableOperator.class, logE.getClass());
		assertEquals(stencil.modules.Numerics.LogFixed.class, log2.getClass());
		assertEquals(InvokeableOperator.class, logNone.getClass());
		
		testInvokes(log10, new Object[]{10, 100, 200, 5}, new Object[]{1.0d, 2.0d, 2.3010299956639813, 0.6989700043360189}); 
		testInvokes(logE, new Object[]{Math.E, 100, 200, 5}, new Object[]{1.0d,  4.605170185988092, 5.298317366548036,  1.6094379124341003}); 
		testInvokes(log2, new Object[]{2, 4, 8, 100, 200, 5}, new Object[]{1.0d, 2.0d, 3.0d, 6.643856189774725, 7.643856189774724, 2.321928094887362});
		testInvokes(logNone, new Object[]{new Object[]{2,2}, new Object[]{3,3},new Object[]{11,11},new Object[]{10.5,10.5}}, 
								new Object[]{1.0d,1.0d,1.0d,1.0d});
	}	
	
	private void testInvokes(StencilOperator op, Object[] input, Object[] expected) {
		assert input.length == expected.length;
		
		Invokeable query = op.getFacet("query");
		for (int i=0; i< input.length; i++) {
			Object[] args;
			if (input[i].getClass().isArray()) {
				args = (Object[]) input[i];
			} else {
				args = new Object[]{input[i]};
			}
			Object result = query.invoke(args);
			assertEquals(op.getName() + " did not return expected result for " + input[i] + ";", expected[i], result);
		}
	}
}
