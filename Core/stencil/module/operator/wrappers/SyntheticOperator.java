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
package stencil.module.operator.wrappers;

import java.util.Arrays;

import stencil.interpreter.Interpreter;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.UnknownFacetException;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import static stencil.parser.ParserConstants.BASIC_SPECIALIZER;
import stencil.parser.ParserConstants;
import stencil.display.Display;
import stencil.parser.tree.OperatorFacet;
import stencil.parser.tree.OperatorRule;
import stencil.parser.tree.util.Environment;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
 
/**Operator defined through a stencil definition.
 * 
 * TODO: These aren't always projects...but they are for now!
 * */
public class SyntheticOperator implements StencilOperator {
	/**Exception to indicate that no rule matches the parameters passed to the given synthetic operator.*/
	public static class NoMatchException extends RuntimeException {
		public NoMatchException(String message) {super(message);}
	}
	
	stencil.parser.tree.Operator opDef;
			
	
	protected final OperatorData operatorData;

	protected final String module;
	
	/**Create a Stencil operator from a specification.
	 * TODO: Some synthetic operators facets are functions...figure out a way to detect this!
	 * */
	public SyntheticOperator(String module, stencil.parser.tree.Operator opDef) {
		this.module = module;
		this.opDef = opDef;

		this.operatorData = new OperatorData(module, opDef.getName(), BASIC_SPECIALIZER);
		
		operatorData.addFacet(new FacetData(ParserConstants.MAP_FACET, MemoryUse.WRITER, opDef.getMap().getResults()));	
		operatorData.addFacet(new FacetData(ParserConstants.QUERY_FACET, MemoryUse.WRITER, opDef.getQuery().getResults()));	
		operatorData.addFacet(new FacetData(ParserConstants.STATE_ID_FACET, MemoryUse.READER, "VALUE"));
	}

	public Invokeable getFacet(String name) throws UnknownFacetException {
		try {
			if (name.equals(StencilOperator.MAP_FACET) 
				|| name.equals(StencilOperator.QUERY_FACET)
			    || name.equals(StencilOperator.STATE_ID_FACET)) {
				return new ReflectiveInvokeable(name, this);
			}
		} catch (Exception e) {throw new RuntimeException("Exception while creating invokeable for standard method", e);}
		throw new UnknownFacetException(operatorData.getName(), name, operatorData.getFacetNames());
	}

	public String getName() {return operatorData.getName();}
	public OperatorData getOperatorData() {return operatorData;}
	public Tuple query(Object... values) {return process(opDef.getQuery(), values);}
	public Tuple map(Object... values) {return process(opDef.getMap(), values);}
	public int stateID(Object... values) {return opDef.getStateQuery().compositeStateID();}
	
	//TODO: Can we do something to support duplicate here?  Maybe the 'pristine clone' trick?
	public SyntheticOperator duplicate() {throw new UnsupportedOperationException();}
	public StencilOperator viewPoint() {
		throw new UnsupportedOperationException("Fix this one...will reqiure some work thought!");
	}

	private Tuple process(OperatorFacet facet, Object... values) {
		if (facet.getArguments().size() != values.length) {
			int expected = facet.getArguments().size();
			throw new IllegalArgumentException(String.format("Incorrect number of arguments passed to synthetic operator.  Expected %1$s.  Recieved %2$d arguments.", expected, values.length));
		}
		Tuple prefilter;
		Tuple tuple = new ArrayTuple(values);
		Environment env = Environment.getDefault(Display.canvas, Display.view, Tuples.EMPTY_TUPLE, tuple);
		
		try {prefilter = Interpreter.process(env, facet.getPrefilterRules());}
		catch (Exception e) {throw new RuntimeException(String.format("Error with prefilter in %1$s.%2$s and tuple %3$s.", opDef.getName(), facet.getName(), tuple.toString()));}
		env.setFrame(Environment.PREFILTER_FRAME, prefilter);
		
		OperatorRule action = matchingAction(facet, env);

		if (action != null) {
			try {
				 Tuple result = action.invoke(env);
				 result = Tuples.align(result, facet.getResults());
				 return result;
			} catch (Exception e) {
				throw new RuntimeException (String.format("Error executing method in %1$s.%2$s.", opDef.getName(), facet.getName()),e);
			}
		}
		
		throw new NoMatchException("No rule to match " + Arrays.deepToString(values));
	}		
			
	/**Find the action that matches the given tuple.  If none does, return null.*/
	private OperatorRule matchingAction(OperatorFacet facet, Environment tuple) {
		for (OperatorRule action: facet.getRules()) {
			if (action.matches(tuple)) {
				return action;
			}
		}
		return null;
	}
}
