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
package stencil.unittests.operator;

import junit.framework.TestCase;
import stencil.operator.StencilOperator;
import stencil.operator.module.Module;
import stencil.operator.module.ModuleData;
import stencil.operator.module.provided.Average;
import stencil.operator.module.util.ModuleDataParser;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;

public class TestMean extends TestCase {
	final  Module average;

	public TestMean() throws Exception {
		ModuleData MD = ModuleDataParser.parse("./Core/stencil/operator/module/provided/Average.xml");
		average = new Average(MD);
	}
	
	public void testFullRange() throws Exception {
		Specializer spec = ParseStencil.parseSpecializer("[1..n]");
		StencilOperator meaner = average.instance("Mean", spec);
		
		int count =0;
		double sum =0;
		
		for (int i=0; i < 1000; i++) {
			sum = sum+i;
			count++;
			meaner.map(i);
			assertEquals(sum/count, meaner.query().get(0));			
		}
	}
	
	public void testSplitFullRange() throws Exception {
		Specializer spec = ParseStencil.parseSpecializer("[v|1..n]");
		StencilOperator meaner = average.instance("Mean", spec);
		
		String[] splits = new String[]{"One", "Two", "Three"};
		
		for (String split: splits) {
			int count =0;
			double sum =0;
			for (int i=0; i<100; i++) {
				sum = sum+i;
				count++;
				meaner.map(split, i);
				assertEquals(sum/count, meaner.query(split).get(0));							
			}
		}
	}
	
}
