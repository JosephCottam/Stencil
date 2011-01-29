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

import stencil.adapters.java2D.Adapter;
import stencil.module.Module;
import stencil.module.operator.StencilOperator;
import stencil.modules.Projection;
import stencil.parser.ParseStencil;
import stencil.parser.string.StencilParser;
import stencil.parser.tree.OperatorProxy;
import stencil.parser.tree.StencilTree;
import stencil.unittests.StencilTestCase;

public class TestProjection extends StencilTestCase {
	final Module project;
	
	public TestProjection() throws Exception {
		project = new Projection();
	}

	public void testCount() throws Exception {
		StencilOperator op;
		
		String simple = "stream S(A,B,C) layer L from S ID: Count()";		
		StencilTree s = ParseStencil.programTree(simple, Adapter.ADAPTER);
		op = ((OperatorProxy) s.find(StencilParser.LIST_OPERATORS).getChild(0)).getOperator();
		assertEquals("Did not find simple operator when expected.", "Counter", op.getOperatorData().getTarget());
		
		String complex = "stream S(A,B,C) layer L from S ID: Count(A)";
		StencilTree c = ParseStencil.programTree(complex, Adapter.ADAPTER);
		op = ((OperatorProxy) c.find(StencilParser.LIST_OPERATORS).getChild(0)).getOperator();
		assertEquals("Did not find complex operator when expected.", "Count", op.getOperatorData().getTarget());
	}	
	
	
	public static void main(String[] args) throws Exception {
		TestProjection t = new TestProjection();
		t.setUp();
		t.testCount();
	}
}
