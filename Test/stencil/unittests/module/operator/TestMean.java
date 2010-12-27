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
import stencil.modules.Average;
import stencil.parser.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.unittests.StencilTestCase;

public class TestMean extends StencilTestCase {
	final Module average;
	
	public TestMean() throws Exception {
		average = new Average();
	}
	
	public void testFullRange() throws Exception {
		Specializer spec = ParseStencil.parseSpecializer("[range: ALL, split: 0]");
		StencilOperator meaner = average.instance("Mean", spec);
		Invokeable map = meaner.getFacet(StencilOperator.MAP_FACET);
		Invokeable query = meaner.getFacet(StencilOperator.QUERY_FACET);
		
		int count =0;
		double sum =0;
		
		for (int i=0; i < 1000; i++) {
			sum = sum+i;
			count++;
			map.invoke(new Object[]{i});
			assertEquals(sum/count, query.invoke(new Object[0]));			
		}
	}
	
	public void testSplitFullRange() throws Exception {
		Specializer spec = ParseStencil.parseSpecializer("[split: \"1,pre\", range: ALL]");
		StencilOperator meaner = average.instance("Mean", spec);
		Invokeable map = meaner.getFacet(StencilOperator.MAP_FACET);
		Invokeable query = meaner.getFacet(StencilOperator.QUERY_FACET);

		String[] splits = new String[]{"One", "Two", "Three"};
		
		for (String split: splits) {
			int count =0;
			double sum =0;
			for (int i=0; i<100; i++) {
				sum = sum+i;
				count++;
				map.invoke(new Object[]{split, i});
				assertEquals(sum/count, query.invoke(new Object[]{split}));			

			}
		}
	}
	
}
