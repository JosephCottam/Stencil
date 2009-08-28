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
package stencil.legend.wrappers;

import java.util.Arrays;

import stencil.legend.StencilLegend;
import stencil.legend.DynamicStencilLegend;
import stencil.legend.module.LegendData;
import stencil.legend.module.SpecializationException;
import stencil.legend.module.LegendData.OpType;
import stencil.legend.module.util.Modules;
import static stencil.parser.ParserConstants.*;
import stencil.parser.tree.LegendRule;
import stencil.parser.tree.Specializer;
import stencil.streams.Tuple;
import stencil.util.BasicTuple;
import stencil.util.Invokeable;

/**Legend defined through a stencil definition.
 * TODO: Add compiler step to fold these legends directly into the call chains if only has one rule predicate on ALL and has no specialization
 * TODO: These aren't always projects...but they are for now!
 * */
public class SyntheticLegend extends stencil.legend.util.BasicProject implements DynamicStencilLegend {
	/**Exception to indicate that no rule matches the parameters passed to the given synthetic legend.*/
	public static class NoMatchException extends RuntimeException {
		public NoMatchException(String message) {super(message);}
	}
	
	/**Indicates that rule exists to handle a value, may be returned from a query when a rule exists to handle a value.
	 * Query will not invoke the rule (because it may have side-effects) but will instead return this value in a tuple.*/
	public static final Object RULE_EXISTS = "EXISTS";

	protected final String module;

	protected final stencil.parser.tree.Legend source;
	
	/**Create a Stencil Legend from a specification.*/
	public SyntheticLegend(String module, stencil.parser.tree.Legend source) {
		this.source = source;
		this.module = module;
	}


	/**Returns the mapping set in this legend.  
	 */
	public Tuple map(Object... values) {
		Tuple tuple = new BasicTuple(source.getArguments(), Arrays.asList(values));
		LegendRule action = matchingAction(tuple);

		if (action != null) {
			try {
				 return action.invoke(tuple);
			} catch (Exception e) {
				throw new RuntimeException ("Error executing method.",e);
			}
		}
		
		throw new NoMatchException("No rule to match " + Arrays.deepToString(values));
	}

	public String getName() {return source.getName();}

	public Tuple query(Object... values) {return map(values);}

	/**Find the action that matches the given tuple.  If none does, return null.*/
	private LegendRule matchingAction(Tuple tuple) {
		for (LegendRule action: source.getRules()) {
			if (action.matches(tuple)) {
				return action;
			}
		}
		return null;
	}

	public LegendData getLegendData(Specializer spec) throws SpecializationException {
		if (spec !=null && !spec.equals(SIMPLE_SPECIALIZER)) {throw new SpecializationException("", getName(), spec);}
		
		return Modules.basicLegendData(module, getName(), OpType.PROJECT, source.getResults().getNames());
	}

	public Invokeable getFacet(String name) throws IllegalArgumentException {
		try {
			if (name.equals(StencilLegend.INVOKE_METHOD) 
				|| name.equals(StencilLegend.QUERY_METHOD)) {
				return new Invokeable(Modules.javaCase(name), this);
			}
		} catch (Exception e) {throw new RuntimeException("Exception while creating invokeable for standard method", e);}
		throw new IllegalArgumentException("Facet not defined: "+ name);
	}
	
	//TODO: Can we do something to support duplicate here?  Maybe the 'pristine clone' trick?
	public DisplayLegend duplicate() {throw new UnsupportedOperationException();}
}
